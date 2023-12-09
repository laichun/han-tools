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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


/**
 * DISCONNECT连接处理
 */
@Slf4j
public class DisConnect {

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

	public void processDisConnect(ChannelHandlerContext cht, MqttMessage msg) {
		String clientId = (String) cht.channel().attr(AttributeKey.valueOf(Tools.clientId)).get();
		if(!StringUtils.isEmpty(clientId)) {
			SessionStore sessionStore = sessionStoreService.get(clientId);
			if (sessionStore != null) {
				if (sessionStore.isCleanSession()) {
					subscribeStoreService.removeForClient(clientId);
					dupPublishMessageStoreService.removeByClient(clientId);
					dupPubRelMessageStoreService.removeByClient(clientId);
				}
				log.debug("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
				String userName = sessionStore.getUserName();
				sessionStoreService.remove(clientId);
				cht.close();
				iMqttListenConnect.offline(clientId, userName, "DisConnect");
			}
		}else{
			cht.channel().close();
		}
		log.info("processDisConnect-------------");
		/*System.out.println(cht.channel());
		if(cht!=null){
			cht.channel().close();
			cht.close();
		}*/
	}

}
