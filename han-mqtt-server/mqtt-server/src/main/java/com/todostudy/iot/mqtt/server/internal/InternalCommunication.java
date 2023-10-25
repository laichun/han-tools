/**
 * 
 *//*


package com.todostudy.iot.mqtt.server.internal;

import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import com.todostudy.iot.mqtt.server.common.session.ISessionStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.SubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

*/
/**
 * 内部通信, 基于发布-订阅范式
 *//*

@Component
public class InternalCommunication {

	private static final Logger LOGGER = LoggerFactory.getLogger(InternalCommunication.class);

	private final String internalTopic = "internal-communication-topic";


	@Autowired
	private ISessionStoreService sessionStoreService;

	@Autowired
	private ISubscribeStoreService subscribeStoreService;

	@Autowired
	private IMessageIdService messageIdService;

	*/
/*@PostConstruct
	private void internalListen() {
		igniteMessaging.localListen(internalTopic, (nodeId, msg) -> {
			InternalMessage internalMessage = (InternalMessage) msg;
			this.sendPublishMessage(internalMessage.getTopic(), MqttQoS.valueOf(internalMessage.getMqttQoS()), internalMessage.getMessageBytes(), internalMessage.isRetain(), internalMessage.isDup());
			return true;
		});
	}

	public void internalSend(InternalMessage internalMessage) {
		if (igniteMessaging.clusterGroup().nodes() != null && igniteMessaging.clusterGroup().nodes().size() > 0) {
			igniteMessaging.send(internalTopic, internalMessage);
		}
	}*//*


	private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
		List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
		subscribeStores.forEach(subscribeStore -> {
			if (sessionStoreService.containsKey(subscribeStore.getClientId())) {
				// 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
				MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
				if (respQoS == MqttQoS.AT_MOST_ONCE) {
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, 0), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
				if (respQoS == MqttQoS.AT_LEAST_ONCE) {
					int messageId = messageIdService.getNextMessageId();
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
				if (respQoS == MqttQoS.EXACTLY_ONCE) {
					int messageId = messageIdService.getNextMessageId();
					MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
						new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
					LOGGER.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
					sessionStoreService.get(subscribeStore.getClientId()).getChannel().writeAndFlush(publishMessage);
				}
			}
		});
	}

}
*/
