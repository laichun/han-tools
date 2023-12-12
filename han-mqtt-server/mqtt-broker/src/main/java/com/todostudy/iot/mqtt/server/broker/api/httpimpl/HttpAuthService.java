/**
 * @author: hanson
 * email :317856394@qq.com
 */

package com.todostudy.iot.mqtt.server.broker.api.httpimpl;

import com.todostudy.hans.stater.config.HanMqttBrokerProperties;
import com.todostudy.iot.mqtt.server.api.IAuthService;
import com.todostudy.iot.mqtt.server.common.Tools;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONStringer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 用户和密码认证服务接口
 */
@Slf4j
@ConditionalOnProperty(value = "han.mqtt.broker.http-enable", havingValue = "true")
@Component
public class HttpAuthService implements IAuthService {

	@Autowired
	HanMqttBrokerProperties hanMqttBrokerProperties;

	public boolean verifyAuth(String username, String password,String clientId){
		if(hanMqttBrokerProperties.isHttpEnable()){
			// http 接口
			Assert.notNull(hanMqttBrokerProperties.getHttpApi().getSendAuth(),"sendAuth is null");
			try {
				String res = Tools.httpBuilder().postJson(hanMqttBrokerProperties.getHttpApi().getSendAuth(), new JSONStringer().object().key(Tools.username).value(username)
						.key("password").value(password).key(Tools.clientId).value(clientId).endObject().toString());
				log.info("res--{}",res);
				return Boolean.valueOf(res);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		return false;
	}
}

@Data
@Builder
class Auth{
	String username;
	String password;
	String clientId;
}
