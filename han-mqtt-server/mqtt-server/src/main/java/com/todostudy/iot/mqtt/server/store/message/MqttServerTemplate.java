package com.todostudy.iot.mqtt.server.store.message;

import com.todostudy.iot.mqtt.server.common.message.IMqttServerTemplate;
import com.todostudy.iot.mqtt.server.protocol.MqttServerTemplateProcessor;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.RequiredArgsConstructor;

/**
 * 对外提供api，发送消息
 * @author  hanson
 */
@RequiredArgsConstructor
public class MqttServerTemplate implements IMqttServerTemplate {

    private final MqttServerTemplateProcessor mqttServerTempProcessor;

    public ChannelFuture sendMsg(String clientId, String topic, MqttQoS qos, byte[] message) {
        return mqttServerTempProcessor.sendMsg(clientId, topic, qos, message);
    }

    public ChannelFuture sendMsgRetain(String clientId, String topic, MqttQoS qos, byte[] message) {
        return mqttServerTempProcessor.sendMsgRetain(clientId, topic, qos, message);
    }

    @Override
    public void sendAll(String topic, MqttQoS qos, byte[] message) {
        mqttServerTempProcessor.sendAll(topic, qos, message);
    }

    @Override
    public void disConnect(String clientId) {
        mqttServerTempProcessor.disConnect(clientId);
    }

    @Override
    public boolean isOnline(String clientId) {
        return mqttServerTempProcessor.isOnline(clientId);
    }
}
