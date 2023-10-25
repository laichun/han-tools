package com.todostudy.iot.mqtt.server.api;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.util.List;

/**
 * hanson
 * 检查订阅的mqtt
 */
public interface ICheckSubscribeValidator {

    public boolean subscribeValidator(String clientId, List<MqttTopicSubscription> topics);

}
