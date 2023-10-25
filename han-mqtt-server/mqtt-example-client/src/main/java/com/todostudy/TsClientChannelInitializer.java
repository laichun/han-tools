package com.todostudy;

import com.todostudy.handler.MyClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class TsClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private SslContext sslContext;

    public TsClientChannelInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 添加SSL安装验证
        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
        sslEngine.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1",
                "TLSv1.2" });

        ch.pipeline().addLast("ssl", new SslHandler(sslEngine));// 不要ssl就去掉
        //ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));

        ch.pipeline().addLast("decoder", new MqttDecoder());
        ch.pipeline().addLast("encoder", MqttEncoder.INSTANCE);
       // ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF8")));
        //ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF8")));
        ch.pipeline().addLast(new MyClientHandler());

    }
}
