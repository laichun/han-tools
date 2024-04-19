package com.todostudy.iot.mqtt.server.broker.api.impl;

import com.todostudy.iot.mqtt.server.api.IWebSocketService;
import com.todostudy.iot.mqtt.server.api.bo.WsAuthBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@ConditionalOnProperty(prefix = "han",name = "mqtt.broker.http-enable",havingValue = "false")
@Service
public class WsMessService implements IWebSocketService {
    @Override
    public WsAuthBo verifyAuth(String str) {
        log.info("====>msg:{}",str);
        WsAuthBo bo = new WsAuthBo();
        bo.setLogin(true);
        bo.setSessionId("hanson");
        bo.setSendMsg("hello");
        return bo;
    }
}
