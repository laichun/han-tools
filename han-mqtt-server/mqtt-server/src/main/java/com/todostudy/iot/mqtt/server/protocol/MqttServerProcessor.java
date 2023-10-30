/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 * modify by hanson 2023-10
 */
package com.todostudy.iot.mqtt.server.protocol;


import com.todostudy.iot.mqtt.server.api.IAuthService;
import com.todostudy.iot.mqtt.server.api.ICheckSubscribeValidator;
import com.todostudy.iot.mqtt.server.api.IMqttListenConnect;
import com.todostudy.iot.mqtt.server.api.IMqttListenMessage;
import com.todostudy.iot.mqtt.server.common.message.IDupPubRelMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import com.todostudy.iot.mqtt.server.common.message.IRetainMessageStoreService;
import com.todostudy.iot.mqtt.server.common.session.ISessionStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import lombok.Data;

/**
 * 协议处理
 */
@Data
public class MqttServerProcessor {

	private final ISessionStoreService sessionStoreService;

	private final ISubscribeStoreService subscribeStoreService;

	private final IAuthService authService;

	private final IMessageIdService messageIdService;

	private final IRetainMessageStoreService retainMessageStoreService;

	private final IDupPublishMessageStoreService dupPublishMessageStoreService;

	private final IDupPubRelMessageStoreService dupPubRelMessageStoreService;

	private final IMqttListenMessage listenMessage;

	private final IMqttListenConnect iMqttListenConnect;

	private final ICheckSubscribeValidator iCheckSubscribeValidator;

	private Connect connect;

	private Subscribe subscribe;

	private UnSubscribe unSubscribe;

	private Publish publish;

	private DisConnect disConnect;

	private PingReq pingReq;

	private PubRel pubRel;

	private PubAck pubAck;

	private PubRec pubRec;

	private PubComp pubComp;

	public MqttServerProcessor(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService, IAuthService authService, IMessageIdService messageIdService,
							   IRetainMessageStoreService retainMessageStoreService, IDupPublishMessageStoreService dupPublishMessageStoreService, IDupPubRelMessageStoreService dupPubRelMessageStoreService,
							   IMqttListenMessage listenMessage, IMqttListenConnect iMqttListenConnect, ICheckSubscribeValidator iCheckSubscribeValidator) {
		this.sessionStoreService = sessionStoreService;
		this.subscribeStoreService = subscribeStoreService;
		this.authService = authService;
		this.messageIdService = messageIdService;
		this.retainMessageStoreService = retainMessageStoreService;
		this.dupPublishMessageStoreService = dupPublishMessageStoreService;
		this.dupPubRelMessageStoreService = dupPubRelMessageStoreService;
		this.listenMessage = listenMessage;
		this.iMqttListenConnect = iMqttListenConnect;
		this.iCheckSubscribeValidator = iCheckSubscribeValidator;
	}

	public Connect connect() {
		if (connect == null) {
			connect = new Connect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService, authService,iMqttListenConnect);
		}
		return connect;
	}

	public Subscribe subscribe() {
		if (subscribe == null) {
			subscribe = new Subscribe(subscribeStoreService, messageIdService, retainMessageStoreService,iCheckSubscribeValidator);
		}
		return subscribe;
	}

	public UnSubscribe unSubscribe() {
		if (unSubscribe == null) {
			unSubscribe = new UnSubscribe(subscribeStoreService);
		}
		return unSubscribe;
	}

	public Publish publish() {
		if (publish == null) {
			publish = new Publish(sessionStoreService, subscribeStoreService, messageIdService, retainMessageStoreService, dupPublishMessageStoreService,listenMessage);
		}
		return publish;
	}

	public DisConnect disConnect() {
		if (disConnect == null) {
			disConnect = new DisConnect(sessionStoreService, subscribeStoreService, dupPublishMessageStoreService, dupPubRelMessageStoreService,iMqttListenConnect);
		}
		return disConnect;
	}

	public PingReq pingReq() {
		if (pingReq == null) {
			pingReq = new PingReq();
		}
		return pingReq;
	}

	public PubRel pubRel() {
		if (pubRel == null) {
			pubRel = new PubRel();
		}
		return pubRel;
	}

	public PubAck pubAck() {
		if (pubAck == null) {
			pubAck = new PubAck(messageIdService, dupPublishMessageStoreService);
		}
		return pubAck;
	}

	public PubRec pubRec() {
		if (pubRec == null) {
			pubRec = new PubRec(dupPublishMessageStoreService, dupPubRelMessageStoreService);
		}
		return pubRec;
	}

	public PubComp pubComp() {
		if (pubComp == null) {
			pubComp = new PubComp(messageIdService, dupPubRelMessageStoreService);
		}
		return pubComp;
	}

	public ISessionStoreService getSessionStoreService() {
		return sessionStoreService;
	}

}
