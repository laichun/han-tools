/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */
package com.todostudy.iot.mqtt.server.protocol;

import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBCOMP连接处理
 */
public class PubComp {

	private static final Logger LOGGER = LoggerFactory.getLogger(PubComp.class);

	private IMessageIdService messageIdService;

	private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	public PubComp(IMessageIdService messageIdService, IDupPubRelMessageStoreService dupPubRelMessageStoreService) {
		this.messageIdService = messageIdService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
	}

	public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		LOGGER.debug("PUBCOMP - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), messageId);
		dupPubRelMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get(), variableHeader.messageId());
		messageIdService.releaseMessageId(messageId);
	}
}
