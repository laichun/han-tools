package com.todostudy.iot.mqtt.server.api;

public interface IMqttListenConnect {

    /**
     * 在线状态监听
     * @param clientId
     * @param username
     */
    public void online(String clientId, String username) ;
    /**
     * 离线状态监听
     * @param clientId
     * @param username
     */
    public void offline(String clientId, String username, String reason) ;

}
