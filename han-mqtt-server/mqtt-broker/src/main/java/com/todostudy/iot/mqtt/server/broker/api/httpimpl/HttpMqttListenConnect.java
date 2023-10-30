package com.todostudy.iot.mqtt.server.broker.api.httpimpl;

import com.todostudy.hans.stater.config.HanMqttBrokerProperties;
import com.todostudy.iot.mqtt.server.api.IMqttListenConnect;
import com.todostudy.iot.mqtt.server.common.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONStringer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@ConditionalOnProperty(value = "han.mqtt.broker.http-enable", havingValue = "true")
@Component
public class HttpMqttListenConnect implements IMqttListenConnect {

    @Autowired
    HanMqttBrokerProperties hanMqttBrokerProperties;
    public void online(String clientId, String username) {
        if(hanMqttBrokerProperties.isHttpEnable()){
            // http 接口
            Assert.notNull(hanMqttBrokerProperties.getHttpApi().getSendState(),"sendState is null");
            try {
                Tools.httpBuilder().postJson(hanMqttBrokerProperties.getHttpApi().getSendState(), new JSONStringer().object().key(Tools.clientId).value(clientId)
                        .key(Tools.username).value(username).key("state").value("online").endObject().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void offline(String clientId, String username, String reason) {
        if(hanMqttBrokerProperties.isHttpEnable()){
            // http 接口
            Assert.notNull(hanMqttBrokerProperties.getHttpApi().getSendState(),"sendState is null");
            try {
                Tools.httpBuilder().postJson(hanMqttBrokerProperties.getHttpApi().getSendState(), new JSONStringer().object().key(Tools.clientId).value(clientId)
                        .key(Tools.username).value(username).key("state").value("offline").endObject().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
