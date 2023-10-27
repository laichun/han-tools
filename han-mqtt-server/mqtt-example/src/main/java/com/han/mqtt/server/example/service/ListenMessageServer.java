package com.han.mqtt.server.example.service;

import com.todostudy.iot.mqtt.server.api.IMqttListenMessage;
import com.todostudy.iot.mqtt.server.common.message.IMqttServerTemplate;
import com.todostudy.iot.mqtt.server.store.message.MqttServerTemplate;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 服务端使用topic发布 t1= lai/v1/ts/ssss ， 客户端则不能使用这个 topic发消息。不然c2也会收到。
 * 使用topic check   t1只能是服务端使用。client只能订阅
 */
@Slf4j
@Service
public class ListenMessageServer implements IMqttListenMessage, SmartInitializingSingleton {

    @Autowired
    private ApplicationContext applicationContext;
    private IMqttServerTemplate mqttServerTemplate;

    @Override
    public void onMessage(String clientId, String topic, MqttQoS qos, byte[] message){
        log.info("--onMessage---clientId:{},msg:{}",clientId, new String(message));

        if(clientId.equals("hanson")){
            mqttServerTemplate.sendMsg(clientId,"hanson/dddd/ts",MqttQoS.AT_MOST_ONCE,"{\"msg:\":\"success\"}".getBytes());
           //测试发送离线消息
           // mqttServerTemplate.sendMsgRetain("laich","hanson/dddd/ts",MqttQoS.AT_MOST_ONCE,"{\"msg:\":\"hason-send-retain-msg-success\"}".getBytes());
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 单利 bean 初始化完成之后从 ApplicationContext 中获取 bean
        mqttServerTemplate = applicationContext.getBean(MqttServerTemplate.class);
    }

}
