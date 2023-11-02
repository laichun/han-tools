package com.todostudy.iot.mqtt.server.api;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.util.List;

/**
 * hanson
 * 检查订阅的mqtt
 */
public interface ICheckSubscribeValidator {

    /**
     * 检查订阅是否正确的规则，如果订阅不正确，会中断连接, 有些客户端会有重连机制，不断连接。clientId 登录时候，加入黑名单检查。
     * @param clientId
     * @param topics  订阅的topic 集合
     * @return boolean
     */
    public boolean subscribeValidator(String clientId, List<MqttTopicSubscription> topics);

}
