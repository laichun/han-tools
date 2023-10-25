package com.todostudy.iot.mqtt.server.api;

public interface IMqttListenConnect {

    public void online(String clientId, String username) ;

    public void offline(String clientId, String username, String reason) ;

}
