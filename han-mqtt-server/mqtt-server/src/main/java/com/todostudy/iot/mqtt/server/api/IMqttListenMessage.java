package com.todostudy.iot.mqtt.server.api;

import io.netty.handler.codec.mqtt.MqttQoS;

public interface IMqttListenMessage {
    public void onMessage( String clientId, String topic, MqttQoS qos, byte[] message);

}
