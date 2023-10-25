/**
 * 
 */

package com.todostudy.iot.mqtt.server.protocol;

import com.todostudy.iot.mqtt.server.common.Tools;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBREL连接处理
 */
public class PubRel {

	private static final Logger LOGGER = LoggerFactory.getLogger(PubRel.class);

	public void processPubRel(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		MqttMessage pubCompMessage = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		LOGGER.debug("PUBREL - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), variableHeader.messageId());
		channel.writeAndFlush(pubCompMessage);
	}

}
