package com.todostudy.iot.mqtt.server.api.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WsAuthBo {

    public WsAuthBo() {
    }

    private String sessionId;
    private boolean login;
    private String sendMsg;
}
