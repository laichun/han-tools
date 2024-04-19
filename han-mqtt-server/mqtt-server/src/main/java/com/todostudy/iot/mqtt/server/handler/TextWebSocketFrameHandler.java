package com.todostudy.iot.mqtt.server.handler;

import com.todostudy.iot.mqtt.server.api.IWebSocketService;
import com.todostudy.iot.mqtt.server.api.bo.WsAuthBo;
import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.session.SessionStore;
import com.todostudy.iot.mqtt.server.session.SessionStoreService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.IllegalBlockSizeException;
import java.time.LocalDateTime;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

//处理文本协议数据，处理TextWebSocketFrame类型的数据，websocket专门处理文本的frame就是TextWebSocketFrame

/***
 * 这种方式jwt或者token 没有IAuthService 处理，只负责消息中转。
 * 不实现该模式，iot应用不到,web端才用到。直接用springboot websocket
 */
@Slf4j
//@Deprecated
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final SessionStoreService sessionStoreService;
    private final IWebSocketService iWebSocketService;

    public TextWebSocketFrameHandler(SessionStoreService sessionStoreService, IWebSocketService iWebSocketService) {
        this.sessionStoreService = sessionStoreService;
        this.iWebSocketService = iWebSocketService;
    }


    //读到客户端的内容并且向客户端去
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("收到消息：" + msg.text());
        WsAuthBo wsAuthBo = iWebSocketService.verifyAuth(msg.text());
        if (wsAuthBo.isLogin()) {
            ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).set(wsAuthBo.getSessionId());
            log.info("登录成功!");
        }else{
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED));
          //  return;
        }
        /**
         * writeAndFlush接收的参数类型是Object类型，但是一般我们都是要传入管道中传输数据的类型，比如我们当前的demo
         * 传输的就是TextWebSocketFrame类型的数据
         */
        // ctx.channel().writeAndFlush(new TextWebSocketFrame("服务时间："+ LocalDateTime.now()));
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
        // 发送HTTP响应
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


  /*  @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String id = ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).get().toString();
        SessionStore sessionStore = sessionStoreService.get(id);
        if (sessionStore == null) {
            sessionStoreService.put(id, new SessionStore(id, ctx.channel(), false, null));
        }
        // 添加连接
        log.info("Client connected: " + ctx.channel());
    }*/

    //有新的连接建立时 每个channel都有一个唯一的id值
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //打印出channel唯一值，asLongText方法是channel的id的全名
        String id = ctx.channel().attr(AttributeKey.valueOf(Tools.clientId)).get().toString();
        SessionStore sessionStore = sessionStoreService.get(id);
        if (sessionStore == null) {
            sessionStoreService.put(id, new SessionStore(id, ctx.channel(), false, null));
        }
        log.info("handlerAdded：{},id:{}" ,ctx.channel().id().asLongText(),id);
    }

    /**
     * 不活跃时会调用这个方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 断开连接
        String id = ctx.channel().id().asLongText();
        sessionStoreService.remove(id);
        log.info("Client disconnected: " + ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved：" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常发生",cause);
        String id = ctx.channel().id().asLongText();
        SessionStore sessionStore = sessionStoreService.get(id);
        if (sessionStore != null) {
            sessionStoreService.remove(id);
        }
        ctx.close();
    }
}
