package com.todostudy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

public class NettyMqttClient {

    public static void main(String[] args) throws Exception {
        new NettyMqttClient().connect("127.0.0.1",1883);
    }

    private void connect(String address, int port) throws Exception {

        SslContext sslContext = getSslContext();


        EventLoopGroup workGroup=new NioEventLoopGroup();
        try {
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.AUTO_READ,true)
                    .handler(new TsClientChannelInitializer(sslContext));
            ChannelFuture future=bootstrap.connect(address,port).sync();
            System.out.println("netty client start done ....");
            future.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println("netty client start error ....");
        }finally {
            workGroup.shutdownGracefully();
        }

    }
    public static SslContext  getSslContext() throws Exception {
        //同理
        KeyStore keyStore = KeyStore.getInstance("JKS");
        //客户端证书的流 和 客户端证书密码
        keyStore.load(ResourceUtil.getResourceAsStream("keystore/cChat.jks"), "123456".toCharArray());
        //
        TrustManagerFactory tf = TrustManagerFactory.getInstance("SunX509");
        //初始化
        tf.init(keyStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "123456".toCharArray());

        SslContext sslContext = SslContextBuilder.forClient()
                .keyManager(keyManagerFactory)
               // .ciphers(ciphers)
                .sslProvider(SslProvider.OPENSSL) //同样也要加入相应jar包
                .trustManager(tf)
                .build();
        return sslContext;
    }


}
