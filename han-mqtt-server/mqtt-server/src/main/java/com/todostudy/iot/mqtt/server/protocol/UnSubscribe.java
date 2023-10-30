/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */

package com.todostudy.iot.mqtt.server.protocol;

import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * UNSUBSCRIBE连接处理
 */
@Slf4j
public class UnSubscribe {

	private ISubscribeStoreService subscribeStoreService;

	public UnSubscribe(ISubscribeStoreService subscribeStoreService) {
		this.subscribeStoreService = subscribeStoreService;
	}

	public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
		List<String> topicFilters = msg.payload().topics();
		String clinetId = (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get();
		topicFilters.forEach(topicFilter -> {
			subscribeStoreService.remove(topicFilter, clinetId);
			log.debug("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clinetId, topicFilter);
		});
		MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
		channel.writeAndFlush(unsubAckMessage);
	}

}
