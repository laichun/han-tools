package com.han.mqtt.server.example.service;

import com.todostudy.iot.mqtt.server.api.IMqttListenConnect;
import com.todostudy.iot.mqtt.server.common.message.IMqttServerTemplate;
import com.todostudy.iot.mqtt.server.store.message.MqttServerTemplate;
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
public class MqttListenConnectServer implements IMqttListenConnect{


    @Override
    public void online(String clientId, String username) {
        log.info("--online--{}", clientId);
    }

    @Override
    public void offline(String clientId, String username, String reason) {
        log.info("------offline--{}", clientId);
    }



}
