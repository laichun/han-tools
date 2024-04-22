package com.todostudy.iot.mqtt.server.broker.api.impl;

import com.todostudy.iot.mqtt.server.api.IWebSocketService;
import com.todostudy.iot.mqtt.server.api.bo.WsAuthBo;
import com.todostudy.iot.mqtt.server.protocol.WebSocketServerProcessor;
import com.todostudy.iot.mqtt.server.store.message.MqttServerTemplate;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@ConditionalOnProperty(prefix = "han", name = "mqtt.broker.ws-model", havingValue = "2")
@Service
public class WsMessService implements IWebSocketService, SmartInitializingSingleton {

    @Autowired
    private ApplicationContext applicationContext;

    private WebSocketServerProcessor webSocketServerProcessor;

    @Override
    public WsAuthBo verifyAuth(HttpHeaders headers, Map<String, Object> map) {
        log.info("====>msg:{}", map);
        WsAuthBo bo = new WsAuthBo();
        bo.setLogin(true);
        bo.setSessionId("hanson");
        bo.setSendMsg("hello");
        return bo;
    }

    @Override
    public void onMessage(String id, String content) {
        log.info("===>id:{},content:{}", id, content);
        if (content.contains("cc")) {
            String s = "{\"re\":\"rere\"}";
            webSocketServerProcessor.sendMsg(id, s);
        }
    }

    @Override
    public void offline(String id) {
        log.info("===>offline id:{}", id);
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 单利 bean 初始化完成之后从 ApplicationContext 中获取 bean
        webSocketServerProcessor = applicationContext.getBean(WebSocketServerProcessor.class);
    }
}
