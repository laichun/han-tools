package com.todostudy.tools.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * back message deal
 */
public interface MqReceiveInterface {
    void dealMessageArrived(String topic, MqttMessage message);
}
