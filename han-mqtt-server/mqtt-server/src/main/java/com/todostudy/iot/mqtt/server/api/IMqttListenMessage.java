package com.todostudy.iot.mqtt.server.api;

import io.netty.handler.codec.mqtt.MqttQoS;

public interface IMqttListenMessage {

    /**
     * 消息监听
     * @param clientId
     */
    public void onMessage( String clientId, String topic, MqttQoS qos, byte[] message);

    public void disConnect(String clientId);

    public boolean isOnline(String clientId);
}
