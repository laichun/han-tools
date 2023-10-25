/**
 * 
 */

package com.todostudy.iot.mqtt.server.protocol;

import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBACK连接处理
 */
public class PubAck {

	private static final Logger LOGGER = LoggerFactory.getLogger(PubAck.class);

	private IMessageIdService messageIdService;

	private IDupPublishMessageStoreService dupPublishMessageStoreService;

	public PubAck(IMessageIdService messageIdService, IDupPublishMessageStoreService dupPublishMessageStoreService) {
		this.messageIdService = messageIdService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
	}

	public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		LOGGER.debug("PUBACK - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), messageId);
		dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), messageId);
		messageIdService.releaseMessageId(messageId);
	}

}
