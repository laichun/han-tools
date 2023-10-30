/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */
package com.todostudy.iot.mqtt.server.protocol;

import cn.hutool.core.util.StrUtil;
import com.todostudy.iot.mqtt.server.api.ICheckSubscribeValidator;
import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import com.todostudy.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.RetainMessageStore;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.SubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * SUBSCRIBE连接处理
 *  modify by hanson 2023-10
 */
public class Subscribe {

	private static final Logger LOGGER = LoggerFactory.getLogger(Subscribe.class);

	private ISubscribeStoreService subscribeStoreService;

	private IMessageIdService messageIdService;

	private IRetainMessageStoreService retainMessageStoreService;
	private ICheckSubscribeValidator iCheckSubscribeValidator;

	public Subscribe(ISubscribeStoreService subscribeStoreService, IMessageIdService messageIdService, IRetainMessageStoreService retainMessageStoreService,ICheckSubscribeValidator iCheckSubscribeValidator) {
		this.subscribeStoreService = subscribeStoreService;
		this.messageIdService = messageIdService;
		this.retainMessageStoreService = retainMessageStoreService;
		this.iCheckSubscribeValidator = iCheckSubscribeValidator;
	}

	public void processSubscribe(Channel channel, MqttSubscribeMessage msg) {
		List<MqttTopicSubscription> topicSubscriptions = msg.payload().topicSubscriptions();

		String clientId = (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get();
		if (iCheckSubscribeValidator.subscribeValidator(clientId, topicSubscriptions)) {//TODO:
			List<Integer> mqttQoSList = new ArrayList<Integer>();
			topicSubscriptions.forEach(topicSubscription -> {
				String topicFilter = topicSubscription.topicName();
				MqttQoS mqttQoS = topicSubscription.qualityOfService();
				SubscribeStore subscribeStore = new SubscribeStore(clientId, topicFilter, mqttQoS.value());
				subscribeStoreService.put(topicFilter, subscribeStore);
				mqttQoSList.add(mqttQoS.value());
				LOGGER.debug("SUBSCRIBE - clientId: {}, topFilter: {}, QoS: {}", clientId, topicFilter, mqttQoS.value());
			});
			MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
				new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
				MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
				new MqttSubAckPayload(mqttQoSList));
			channel.writeAndFlush(subAckMessage);
			// 发布保留消息
			topicSubscriptions.forEach(topicSubscription -> {
				String topicFilter = topicSubscription.topicName();
				MqttQoS mqttQoS = topicSubscription.qualityOfService();
				this.sendRetainMessage(channel, topicFilter, mqttQoS);
			});
		} else {
			channel.close();
		}
	}

	private boolean validTopicFilter(List<MqttTopicSubscription> topicSubscriptions) {
		for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
			String topicFilter = topicSubscription.topicName();
			// 以#或+符号开头的、以/符号结尾的及不存在/符号的订阅按非法订阅处理, 这里没有参考标准协议
			if (StrUtil.startWith(topicFilter, '#') || StrUtil.startWith(topicFilter, '+') || StrUtil.endWith(topicFilter, '/') || !StrUtil.contains(topicFilter, '/')) return false;
			if (StrUtil.contains(topicFilter, '#')) {
				// 不是以/#字符串结尾的订阅按非法订阅处理
				if (!StrUtil.endWith(topicFilter, "/#")) return false;
				// 如果出现多个#符号的订阅按非法订阅处理
				if (StrUtil.count(topicFilter, '#') > 1) return false;
			}
			if (StrUtil.contains(topicFilter, '+')) {
				//如果+符号和/+字符串出现的次数不等的情况按非法订阅处理
				if (StrUtil.count(topicFilter, '+') != StrUtil.count(topicFilter, "/+")) return false;
			}
		}
		return true;
	}

	private void sendRetainMessage(Channel channel, String topicFilter, MqttQoS mqttQoS) {
		List<RetainMessageStore> retainMessageStores = retainMessageStoreService.search(topicFilter);
		retainMessageStores.forEach(retainMessageStore -> {
			MqttQoS respQoS = retainMessageStore.getMqttQoS() > mqttQoS.value() ? mqttQoS : MqttQoS.valueOf(retainMessageStore.getMqttQoS());
			if (respQoS == MqttQoS.AT_MOST_ONCE) {
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), 0), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), retainMessageStore.getTopic(), respQoS.value());
				channel.writeAndFlush(publishMessage);
			}
			if (respQoS == MqttQoS.AT_LEAST_ONCE) {
				int messageId = messageIdService.getNextMessageId();
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), retainMessageStore.getTopic(), respQoS.value(), messageId);
				channel.writeAndFlush(publishMessage);
			}
			if (respQoS == MqttQoS.EXACTLY_ONCE) {
				int messageId = messageIdService.getNextMessageId();
				MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
					new MqttFixedHeader(MqttMessageType.PUBLISH, false, respQoS, false, 0),
					new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId), Unpooled.buffer().writeBytes(retainMessageStore.getMessageBytes()));
				LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), retainMessageStore.getTopic(), respQoS.value(), messageId);
				channel.writeAndFlush(publishMessage);
			}
		});
	}

}
