/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */

package com.todostudy.iot.mqtt.server;

import com.todostudy.iot.mqtt.server.codec.MqttWebSocketCodec;
import com.todostudy.iot.mqtt.server.handler.MqttBrokerHandler;
import com.todostudy.iot.mqtt.server.protocol.MqttServerTemplateProcessor;
import com.todostudy.iot.mqtt.server.session.SessionStoreService;
import com.todostudy.iot.mqtt.server.ssl.SSLContextFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Netty启动Broker
 */
@Getter
public class MqttBrokerServer {

	private final SessionStoreService sessionStoreService;

	private final MqttServerCreator serverCreator;

	private static final Logger log = LoggerFactory.getLogger(MqttBrokerServer.class);

	private final MqttBrokerHandler mqttBrokerHandler;

	private EventLoopGroup bossGroup;

	private EventLoopGroup workerGroup;

	private Channel channel;

	private Channel websocketChannel;

	private  final MqttServerTemplateProcessor mqttServerTemplateProcessor;

	public MqttBrokerServer(SessionStoreService sessionStoreService, MqttServerCreator serverCreator,
							MqttBrokerHandler mqttBrokerHandler,MqttServerTemplateProcessor mqttServerTemplateProcessor) {
		this.sessionStoreService = sessionStoreService;
		this.serverCreator = serverCreator;
		this.mqttBrokerHandler = mqttBrokerHandler;
		this.mqttServerTemplateProcessor = mqttServerTemplateProcessor;
	}

	public static MqttServerCreator createServer() {
		return new MqttServerCreator();
	}

	private static final String PROTOCOL = "TLS";

	public void start() {
		bossGroup = serverCreator.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
		workerGroup = serverCreator.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
		SSLEngine sslServerEngine=null;
		try {
			// start mqtt
			if (serverCreator.isSslAuth()) {
				InputStream caInputStream=null;
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(serverCreator.getSslConfig().getKeystorePath());
				if(serverCreator.getSslConfig().isEnable()){
					caInputStream = this.getClass().getClassLoader().getResourceAsStream(serverCreator.getSslConfig().getTruststorePath());
				}
				 sslServerEngine = SSLContextFactory.getSslServerEngine(serverCreator.getSslConfig().isEnable(),
						inputStream, caInputStream, serverCreator.getSslConfig().getKeystorePwd());
				mqttSSLServer(sslServerEngine);

			} else {
				mqttNoSSLServer();
			}

			// start websocket
			if (serverCreator.isWsEnable()) {
				websocketServer(sslServerEngine);
				log.info("MQTT websocket is start isSslAuth:{} is up and running.  webSocketPort: {},the cacheType:{}", "[" + serverCreator.isSslAuth() + "]",  serverCreator.getWebsocketSslPort(),serverCreator.getCacheType());
			}
			log.info("MQTT Broker is start isSslAuth:{} is up and running. mqPort: {},the cacheType:{}", "[" + serverCreator.isSslAuth() + "]", serverCreator.getSslPort(), serverCreator.getCacheType());
		}catch (Exception e){
			log.error("error=>",e);
		}
	}

	public void stop() {
		bossGroup.shutdownGracefully();
		bossGroup = null;
		workerGroup.shutdownGracefully();
		workerGroup = null;
		channel.closeFuture().syncUninterruptibly();
		channel = null;
		websocketChannel.closeFuture().syncUninterruptibly();
		websocketChannel = null;

	}

	public void mqttNoSSLServer() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();
			b.group(bossGroup, workerGroup)
					.channel( NioServerSocketChannel.class)
					// handler在初始化时就会执行
					.handler(new LoggingHandler(LogLevel.INFO))
					// childHandler会在客户端成功connect后才执行
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							log.info("initChannel ch:{}", ch);
							try {
								ChannelPipeline p = ch.pipeline();
								// Netty提供的心跳检测
								p.addFirst("idle",
										new IdleStateHandler(0, 0, serverCreator.getKeepAlive(), TimeUnit.SECONDS));

								p.addLast(new MqttDecoder(1024 * 1024 * serverCreator.getMaxTransMessage()));//传输大小 2m
								p.addLast(MqttEncoder.INSTANCE);
								p.addLast(mqttBrokerHandler);
							} catch (Exception e) {
								log.warn("new connect exception", e);
								throw new RuntimeException(e);
							}

						}

					}).option(ChannelOption.SO_BACKLOG, serverCreator.getSoBacklog())
					.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

			channel = b.bind(serverCreator.getSslPort()).sync().channel();
			log.info("MQTT 启动，监听端口:{}", serverCreator.getSslPort());
		} catch (Exception e) {
			log.error("启动mqtt server失败", e);
			System.exit(1);
		}

	}

	private void mqttSSLServer(SSLEngine sslEngine) throws Exception {
		ServerBootstrap sb = new ServerBootstrap();
		sb.group(bossGroup, workerGroup)
			.channel(serverCreator.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
			// handler在初始化时就会执行
			.handler(new LoggingHandler(LogLevel.INFO))
			// childHandler会在客户端成功connect后才执行
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline channelPipeline = socketChannel.pipeline();
					// Netty提供的心跳检测
					channelPipeline.addFirst("idle", new IdleStateHandler(0, 0, serverCreator.getKeepAlive()));
					// Netty提供的SSL处理
					channelPipeline.addLast("ssl", new SslHandler(sslEngine));// 不要ssl就去掉
					channelPipeline.addLast("decoder", new MqttDecoder(1024*1024*serverCreator.getMaxTransMessage()));
					channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);

					channelPipeline.addLast(mqttBrokerHandler);

				}
			})
			.option(ChannelOption.SO_BACKLOG, serverCreator.getSoBacklog())
			.childOption(ChannelOption.SO_KEEPALIVE, serverCreator.isSoKeepAlive());
		channel = sb.bind(serverCreator.getSslPort()).sync().channel();
	}

	void websocketServer(SSLEngine sslEngine) throws Exception {
		ServerBootstrap sb = new ServerBootstrap();
		sb.group(bossGroup, workerGroup)
			.channel(serverCreator.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
			// handler在初始化时就会执行
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline channelPipeline = socketChannel.pipeline();
					// Netty提供的心跳检测
					channelPipeline.addFirst("idle", new IdleStateHandler(0, 0, serverCreator.getKeepAlive()));
					// Netty提供的SSL处理
					if(sslEngine!=null) {
						channelPipeline.addLast("ssl", new SslHandler(sslEngine));// ssl
					}
					// 将请求和应答消息编码或解码为HTTP消息
					channelPipeline.addLast("http-codec", new HttpServerCodec());
					// 将HTTP消息的多个部分合成一条完整的HTTP消息
					channelPipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
					// 将HTTP消息进行压缩编码
					channelPipeline.addLast("compressor ", new HttpContentCompressor());
					channelPipeline.addLast("protocol", new WebSocketServerProtocolHandler(serverCreator.getWebsocketPath(), "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
					channelPipeline.addLast("mqttWebSocket", new MqttWebSocketCodec());
					channelPipeline.addLast("decoder", new MqttDecoder(1024*1024*serverCreator.getMaxTransMessage()));
					channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
					channelPipeline.addLast("broker", mqttBrokerHandler);
				}
			})
			.option(ChannelOption.SO_BACKLOG, serverCreator.getSoBacklog())
			.childOption(ChannelOption.SO_KEEPALIVE, serverCreator.isSoKeepAlive());
		websocketChannel = sb.bind(serverCreator.getWebsocketSslPort()).sync().channel();
	}

}
