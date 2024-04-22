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
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

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
            iWebSocketService.onMessage(id.toString(), msg.text());
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
            if (null != uri && uri.contains("?")) {
                log.debug("===>uri:{}", uri); //处理uri参数数据
                urlParams = Tools.parseUrlParams(uri.substring(uri.indexOf("?") + 1, uri.length()));

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
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Object id = ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).get();
        log.debug("客户端断开连接：{}", id);
        if (id != null) {
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
            sessionStoreService.remove(id.toString());
        }
        super.exceptionCaught(ctx, cause);
    }

}
