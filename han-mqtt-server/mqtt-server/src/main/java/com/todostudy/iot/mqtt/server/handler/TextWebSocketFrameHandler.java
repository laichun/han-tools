package com.todostudy.iot.mqtt.server.handler;

import com.todostudy.iot.mqtt.server.api.IWebSocketService;
import com.todostudy.iot.mqtt.server.api.bo.WsAuthBo;
import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.session.SessionStore;
import com.todostudy.iot.mqtt.server.session.SessionStoreService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final SessionStoreService sessionStoreService;
    private final IWebSocketService iWebSocketService;
    private final String urlPath;

    public TextWebSocketFrameHandler(SessionStoreService sessionStoreService, IWebSocketService iWebSocketService, String urlPath) {
        this.sessionStoreService = sessionStoreService;
        this.iWebSocketService = iWebSocketService;
        this.urlPath = urlPath;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Object id = ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).get();
        if (id != null) {
            String message = msg.text();
            if(message.startsWith("Heartbeat")){ //心跳处理
                log.debug("ping Heartbeat");
            }else {
                iWebSocketService.onMessage(id.toString(), message);
            }
        } else {
            //未登录
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED));
            ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String uri = complete.requestUri();
            Map<String, Object> urlParams = Map.of();
            //得到参数。url传参和form传参 二选一
            if (null != uri && uri.contains(Tools.S_R)) {
                log.debug("===>uri:{}", uri); //处理uri参数数据
                urlParams = Tools.parseUrlParams(uri.substring(uri.indexOf(Tools.S_R) + 1, uri.length()));

            } else {
                //Head传值不用处理。
            }
            WsAuthBo wsAuthBo = iWebSocketService.verifyAuth(complete.requestHeaders(), urlParams);
            if (wsAuthBo.isLogin()) {
                ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).set(wsAuthBo.getSessionId());
                sessionStoreService.put(wsAuthBo.getSessionId(), new SessionStore(wsAuthBo.getSessionId(), ctx.channel()));
                log.debug("登录成功! {}", wsAuthBo.getSessionId());

            } else {
                sendHttpResponse(ctx, new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED));
                // log.debug("登录失败!");
                ctx.close();
            }
        }
        /**
         * 主动断开，jwt方式可以60s后断开和http差不多，长时间连接增加性能开销。 这个时间可以调长在心跳包里面改可以改成 120s
         * 如果做成iot监控设备上线，就要改成心跳包，方式。ws一般很少用于iot设备。多用于web长连接。jwt+60s连接时间是最佳的选择。
         * ws客户端需要发送 心跳包 ws.send('{"event":"ping","content":"Heartbeat Packet"}'); 或者 ws.send('Heartbeat Packet');
         */
        else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {//
                // 连接已经断开，执行相应的断开连接逻辑
                log.debug("IdleStateEvent 关闭，状态码：{}", event.state());
                ctx.close();
            }
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
        // 发送HTTP响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    //有新的连接建立时 每个channel都有一个唯一的id值
  /*  @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }*/

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端连接：{}", ctx.channel().id());
        // 当WebSocket连接激活时，添加IdleStateHandler
       // ctx.pipeline().addAfter("IdleStateHandler", new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Object id = ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).get();
        log.debug("客户端断开连接：{}", id);
        if (id != null) {
            iWebSocketService.offline(id.toString());
            sessionStoreService.remove(id.toString());
        }
        super.channelInactive(ctx);
    }


   /* @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Object id = ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).get();
        if (id != null) {
            log.debug("客户端断开连接：{}", id);
            ctx.close();
            iWebSocketService.offline(id.toString());
            sessionStoreService.remove(id.toString());
        }
        super.exceptionCaught(ctx, cause);
    }

}
