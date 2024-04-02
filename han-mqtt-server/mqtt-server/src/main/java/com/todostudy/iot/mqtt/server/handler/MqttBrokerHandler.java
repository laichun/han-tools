/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */

package com.todostudy.iot.mqtt.server.handler;


import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.session.SessionStore;
import com.todostudy.iot.mqtt.server.protocol.MqttServerProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static io.netty.handler.codec.mqtt.MqttMessageType.CONNACK;
import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

/**
 * MQTT消息处理 不能使用单例
 */
@Slf4j
@ChannelHandler.Sharable
public class MqttBrokerHandler extends ChannelInboundHandlerAdapter implements GenericFutureListener<Future<? super Void>> {

	private MqttServerProcessor protocolProcess;

	public MqttBrokerHandler(MqttServerProcessor protocolProcess) {
		this.protocolProcess = protocolProcess;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.debug("===> disconnection------channelUnregistered isRegistered:{}",ctx.channel().isRegistered());//Channel 已经被创建，但还未注册到 EventLoop
		//非正常断开连接事件通知
		protocolProcess.disConnect().processDisConnect(ctx, null);
		super.channelUnregistered(ctx);
		ctx.channel().close();
		//ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		// PUBREC和PUBREL和PUBCOMP只有在QoS2等级时才存在 如果服务端不支持 QoS2 这三个可以注释掉。
		MqttMessage msg=(MqttMessage) obj;
		// logger.debug("收到消息:{}", msg);
		switch (msg.fixedHeader().messageType()) {
			case CONNECT:
				protocolProcess.connect().processConnect(ctx.channel(), (MqttConnectMessage) msg);
				break;
			case CONNACK:
				break;
			case PUBLISH:
				protocolProcess.publish().processPublish(ctx, (MqttPublishMessage) msg);
				break;
			case PUBACK:
				protocolProcess.pubAck().processPubAck(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREC: //PUBREC – 发布收到（QoS 2 )
				protocolProcess.pubRec().processPubRec(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBREL: // PUBREL – 发布 释放（QoS 2 )
				protocolProcess.pubRel().processPubRel(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case PUBCOMP: // PUBCOMP – 发布 完成（QoS 2)
				protocolProcess.pubComp().processPubComp(ctx.channel(), (MqttMessageIdVariableHeader) msg.variableHeader());
				break;
			case SUBSCRIBE:
				protocolProcess.subscribe().processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
				break;
			case SUBACK: // SUBACK – 订阅确认
				break;
			case UNSUBSCRIBE: //UNSUBSCRIBE – 取消订阅
				protocolProcess.unSubscribe().processUnSubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
				break;
			case UNSUBACK:
				break;
			case PINGREQ: //客户机发过来的ping
				protocolProcess.pingReq().processPingReq(ctx.channel(), msg);
				break;
			case PINGRESP:
				break;
			case DISCONNECT:
				protocolProcess.disConnect().processDisConnect(ctx, msg);
				break;
			default:
				break;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof IOException) {
			// 远程主机强迫关闭了一个现有的连接的异常
			ctx.close();
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			if (idleStateEvent.state() == IdleState.ALL_IDLE) {
				Channel channel = ctx.channel();
				String clientId = (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get();
				if(!StringUtils.isEmpty(clientId)) {
					// 发送遗嘱消息
					if (this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
						SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
						if (sessionStore.getWillMessage() != null) {
							this.protocolProcess.publish().processPublish(ctx, sessionStore.getWillMessage());
						}
					}
					ctx.close();
				}
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void operationComplete(Future<? super Void> future) throws Exception {
		log.debug("=============[{}] channel closed", "");
	}

	/**
	 * 服务端每完整的读完一次数据，都会回调该方法
	 */
/*	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
          super.channelReadComplete(ctx);
    }*/

	/**
	 * 该客户端与服务端的连接被关闭时回调
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	private MqttConnAckMessage createMqttConnAckMsg(MqttConnectReturnCode returnCode, MqttConnectMessage msg) {
		MqttFixedHeader mqttFixedHeader =
				new MqttFixedHeader(CONNACK, false, AT_MOST_ONCE, false, 0);
		MqttConnAckVariableHeader mqttConnAckVariableHeader =
				new MqttConnAckVariableHeader(returnCode, !msg.variableHeader().isCleanSession());
		return new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
	}
}
