package com.todostudy.iot.mqtt.server.protocol;

import com.todostudy.iot.mqtt.server.common.Tools;
import com.todostudy.iot.mqtt.server.common.session.ISessionStoreService;
import com.todostudy.iot.mqtt.server.common.session.SessionStore;
import com.todostudy.iot.mqtt.server.common.subscribe.ISubscribeStoreService;
import com.todostudy.iot.mqtt.server.common.subscribe.SubscribeStore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WebSocketServerProcessor {

    private final ISessionStoreService sessionStoreService;

    public WebSocketServerProcessor(ISessionStoreService sessionStoreService) {
        this.sessionStoreService = sessionStoreService;
    }

    public ChannelFuture sendMsg(String id, String msg){
        SessionStore sessionStore = sessionStoreService.get(id);
        if(sessionStore!=null) {
            Channel channel = sessionStore.getChannel();
            // 超过高水位，则采取同步模式
            if (channel.isWritable()) {
                return channel.writeAndFlush(msg);
            }
            try {
                return channel.writeAndFlush(msg).sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    //群发
    public void sendAll(String topic, MqttQoS qos, String message) {
        Tools.getGodo().execute(()-> {   //线程发送
            Map<String,SessionStore> subscribeStores = sessionStoreService.getAll();
            subscribeStores.forEach((key, sessionStore) -> {
                sendMsg(key, message);
            });
        });

    }

}
