package com.todostudy.iot.mqtt.server.broker.api.httpimpl;

import com.todostudy.hans.stater.config.HanMqttBrokerProperties;
import com.todostudy.iot.mqtt.server.api.ICheckSubscribeValidator;
import com.todostudy.iot.mqtt.server.common.Tools;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONStringer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Author: hanson
 * 检查订阅的mqtt
 */
@ConditionalOnProperty(value = "han.mqtt.broker.http-enable", havingValue = "true")
@Component
public class HttpCheckSubscribeValidator implements ICheckSubscribeValidator {

    @Autowired
    HanMqttBrokerProperties hanMqttBrokerProperties;

    public boolean subscribeValidator(String clientId, List<MqttTopicSubscription> topicSubscriptions){
        if(hanMqttBrokerProperties.isHttpEnable()){
            // http 接口
            Assert.notNull(hanMqttBrokerProperties.getHttpApi().getSendSubTopic(),"sendSubTopic is null");
            try {
                String res = Tools.httpBuilder().postJson(hanMqttBrokerProperties.getHttpApi().getSendSubTopic(), new JSONStringer().object().key(Tools.clientId).value(clientId)
                        .key(Tools.topic).value(topicSubscriptions).endObject().toString());
                return Boolean.valueOf(res);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

}
