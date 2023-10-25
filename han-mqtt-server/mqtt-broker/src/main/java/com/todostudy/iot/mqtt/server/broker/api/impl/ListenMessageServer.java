package com.todostudy.iot.mqtt.server.broker.api.impl;

import com.todostudy.iot.mqtt.server.api.IMqttListenMessage;
import com.todostudy.iot.mqtt.server.common.message.IMqttServerTemplate;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 服务端使用topic发布 t1= lai/v1/ts/ssss ， 客户端则不能使用这个 topic发消息。不然c2也会收到。
 * 使用topic check   t1只能是服务端使用。client只能订阅
 */
@Slf4j
@ConditionalOnProperty(prefix = "han",name = "mqtt.broker.http-enable",havingValue = "false")
@Service
public class ListenMessageServer implements IMqttListenMessage {

    @Autowired
    private IMqttServerTemplate mqttServerTemplate;

    @Override
    public void onMessage(String clientId, String topic, MqttQoS qos, byte[] message){
        System.out.println(clientId);
        log.info("--onMessage---clientId:{},msg:{}",clientId, new String(message));

        if(clientId.equals("hanson")){
            mqttServerTemplate.sendMsg(clientId,"hanson/dddd/ts",MqttQoS.AT_MOST_ONCE,"{\"msg:\":\"success\"}".getBytes());
           //测试发送离线消息
           // mqttServerTemplate.sendMsgRetain("laich","hanson/dddd/ts",MqttQoS.AT_MOST_ONCE,"{\"msg:\":\"hason-send-retain-msg-success\"}".getBytes());
        }
    }
}
