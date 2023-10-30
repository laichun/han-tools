/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */
package com.todostudy.iot.mqtt.server.protocol;


import com.todostudy.iot.mqtt.server.api.IMqttListenConnect;
import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.todostudy.iot.mqtt.server.common.session.ISessionStoreService;
import com.todostudy.iot.mqtt.server.common.session.SessionStore;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * DISCONNECT连接处理
 */
public class DisConnect {

	private static final Logger LOGGER = LoggerFactory.getLogger(DisConnect.class);

	private ISessionStoreService sessionStoreService;

	private ISubscribeStoreService subscribeStoreService;

	private IDupPublishMessageStoreService dupPublishMessageStoreService;

	private IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	private IMqttListenConnect iMqttListenConnect;

	public DisConnect(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService,
					  IDupPubRelMessageStoreService dupPubRelMessageStoreService, IMqttListenConnect iMqttListenConnect) {
		this.sessionStoreService = sessionStoreService;
		this.subscribeStoreService = subscribeStoreService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
		this.iMqttListenConnect = iMqttListenConnect;
	}

	public void processDisConnect(Channel channel, MqttMessage msg) {
		String clientId = (String) channel.attr(AttributeKey.valueOf(Tools.clientId)).get();
		if(!StringUtils.isEmpty(clientId)) {
			SessionStore sessionStore = sessionStoreService.get(clientId);
			if (sessionStore != null) {
				if (sessionStore.isCleanSession()) {
					subscribeStoreService.removeForClient(clientId);
					dupPublishMessageStoreService.removeByClient(clientId);
					dupPubRelMessageStoreService.removeByClient(clientId);
				}
				LOGGER.debug("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
				String userName = sessionStore.getUserName();
				sessionStoreService.remove(clientId);
				channel.close();
				iMqttListenConnect.offline(clientId, userName, "DisConnect");
			}
		}
	}

}
