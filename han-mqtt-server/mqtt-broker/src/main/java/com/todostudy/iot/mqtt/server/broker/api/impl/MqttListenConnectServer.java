package com.todostudy.iot.mqtt.server.broker.api.impl;

import com.todostudy.iot.mqtt.server.api.IMqttListenConnect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 服务端使用topic发布 t1= lai/v1/ts/ssss ， 客户端则不能使用这个 topic发消息。不然c2也会收到。
 * 使用topic check   t1只能是服务端使用。client只能订阅
 */
@Slf4j
@ConditionalOnProperty(prefix = "han",name = "mqtt.broker.http-enable",havingValue = "false")
@Service
public class MqttListenConnectServer implements IMqttListenConnect {

    @Override
    public void online(String clientId, String username) {
        System.out.println("--online--"+clientId);
    }

    @Override
    public void offline(String clientId, String username, String reason) {
        System.out.println("------offline--"+clientId);
    }
}
