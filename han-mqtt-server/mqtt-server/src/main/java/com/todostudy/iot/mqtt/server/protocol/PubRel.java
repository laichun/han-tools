/**
 * 
 */

package com.todostudy.iot.mqtt.server.protocol;
/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */

import com.todostudy.iot.mqtt.server.common.Tools;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBREL连接处理
 *  modify by hanson 2023-10
 */
@Slf4j
public class PubRel {
	public void processPubRel(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		MqttMessage pubCompMessage = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		log.debug("PUBREL - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), variableHeader.messageId());
		channel.writeAndFlush(pubCompMessage);
	}

}
