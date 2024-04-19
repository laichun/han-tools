package com.todostudy.iot.mqtt.server.api;

import com.todostudy.iot.mqtt.server.api.bo.WsAuthBo;

public interface IWebSocketService {

    public WsAuthBo verifyAuth(String str);

}
