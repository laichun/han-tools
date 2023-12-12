package com.todostudy.iot.mqtt.server.broker.api.httpimpl;

import com.todostudy.hans.stater.config.HanMqttBrokerProperties;
import com.todostudy.iot.mqtt.server.api.IMqttListenMessage;
import com.todostudy.iot.mqtt.server.common.Tools;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONStringer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @Author: hanson
 */
@ConditionalOnProperty(value = "han.mqtt.broker.http-enable", havingValue = "true")
@Component
public class HttpMqttListenMessage implements IMqttListenMessage  {
    @Autowired
    HanMqttBrokerProperties hanMqttBrokerProperties;
    @Override
    public void onMessage(String clientId, String topic, MqttQoS qos, byte[] message) {
        if(hanMqttBrokerProperties.isHttpEnable()){
            // http 接口
            Assert.notNull(hanMqttBrokerProperties.getHttpApi().getSendMsg(),"sendMsg is null");
            try {
                Tools.httpBuilder().postJson(hanMqttBrokerProperties.getHttpApi().getSendMsg(), new JSONStringer().object().key(Tools.clientId).value(clientId)
                        .key("topic").value(topic).key("qos").value(qos).key("message").value(new String(message)).endObject().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
