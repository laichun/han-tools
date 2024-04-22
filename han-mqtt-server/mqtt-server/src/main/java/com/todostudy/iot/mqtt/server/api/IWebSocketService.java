package com.todostudy.iot.mqtt.server.api;

import com.todostudy.iot.mqtt.server.api.bo.WsAuthBo;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

/**
 * @author hanson
 * websocket 服务接口，改接口和springboot websocket接口相似用netty实现。即配置文件 wsModel = 2
 */
public interface IWebSocketService {

    public WsAuthBo verifyAuth(HttpHeaders headers, Map<String, Object> map);

    public void onMessage(String id, String content);
}
