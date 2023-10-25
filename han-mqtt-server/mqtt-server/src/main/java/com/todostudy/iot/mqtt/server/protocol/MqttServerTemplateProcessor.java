package com.todostudy.iot.mqtt.server.protocol;

import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.session.ISessionStoreService;
import com.todostudy.iot.mqtt.server.common.session.SessionStore;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.SubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.*;
import lombok.Data;

import java.util.List;

@Data
public class MqttServerTemplateProcessor {

    private final ISessionStoreService sessionStoreService;

    private final ISubscribeStoreService subscribeStoreService;

    public MqttServerTemplateProcessor(ISessionStoreService sessionStoreService, ISubscribeStoreService subscribeStoreService) {
        this.sessionStoreService = sessionStoreService;
        this.subscribeStoreService = subscribeStoreService;
    }


    public ChannelFuture sendMsg(String clientId, String topic, MqttQoS qos, byte[] message) {
        SessionStore sessionStore = sessionStoreService.get(clientId);
        Channel channel = sessionStore.getChannel();
        MqttPublishMessage pubMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH,
                        false,
                        qos,
                        false,
                        0),
                new MqttPublishVariableHeader(topic, 0),
                Unpooled.buffer().writeBytes(message));
        // 超过高水位，则采取同步模式
        if (channel.isWritable()) {
            return channel.writeAndFlush(pubMessage);
        }
        try {
            return channel.writeAndFlush(pubMessage).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public ChannelFuture sendMsgRetain(String clientId, String topic, MqttQoS qos, byte[] message) {
        SessionStore sessionStore = sessionStoreService.get(clientId);
        Channel channel = sessionStore.getChannel();
        MqttPublishMessage pubMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH,
                        false,
                        qos,
                        true,
                        0),
                new MqttPublishVariableHeader(topic, 0),
                Unpooled.buffer().writeBytes(message));
        // 超过高水位，则采取同步模式
        if (channel.isWritable()) {
            return channel.writeAndFlush(pubMessage);
        }
        try {
            return channel.writeAndFlush(pubMessage).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendAll(String topic, MqttQoS qos, byte[] message) {
        Tools.getGodo().execute(new Runnable() {   //线程发送
            @Override
            public void run() {
                List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
                subscribeStores.forEach(item->{
                    sendMsgRetain(item.getClientId(),  topic,  qos, message);
                });
            }
        });

    }
}
