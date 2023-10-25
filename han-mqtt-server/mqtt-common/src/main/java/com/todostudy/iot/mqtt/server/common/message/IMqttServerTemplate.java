package com.todostudy.iot.mqtt.server.common.message;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttQoS;

public interface IMqttServerTemplate {

    public ChannelFuture sendMsg(String clientId, String topic, MqttQoS qos, byte[] message);

    public ChannelFuture sendMsgRetain(String clientId, String topic, MqttQoS qos, byte[] message);

    public void sendAll(String topic, MqttQoS qos, byte[] message);
}
