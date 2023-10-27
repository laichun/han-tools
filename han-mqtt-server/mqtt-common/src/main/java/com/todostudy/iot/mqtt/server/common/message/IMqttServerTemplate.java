package com.todostudy.iot.mqtt.server.common.message;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * @author hanson
 */
public interface IMqttServerTemplate {

    /**
     * 发送消息
     * @param clientId
     * @param topic
     * @param qos
     * @param message
     * @return
     */
    public ChannelFuture sendMsg(String clientId, String topic, MqttQoS qos, byte[] message);

    public ChannelFuture sendMsgRetain(String clientId, String topic, MqttQoS qos, byte[] message);

    public void sendAll(String topic, MqttQoS qos, byte[] message);
}
