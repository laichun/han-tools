/**
 * @author: hanson
 * email :317856394@qq.com
 */

package com.todostudy.iot.mqtt.server.broker.api.impl;

import cn.hutool.core.util.StrUtil;
import com.todostudy.iot.mqtt.server.api.IAuthService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;

/**
 * 用户名和密码认证服务
 */
@ConditionalOnProperty(prefix = "han",name = "mqtt.broker.http-enable",havingValue = "false")
@Service
public class AuthService implements IAuthService {

	private RSAPrivateKey privateKey;


	public boolean verifyAuth(String username, String password,String clientId) {
		System.out.println("-----------------"+username+"_"+password+"_"+clientId);
		if (StrUtil.isBlank(username)) return false;
		if (StrUtil.isBlank(password)) return false;
		else
			System.out.println(username+"_"+password);
			return true;

		/*RSA rsa = new RSA(privateKey, null);
		String value = rsa.encryptBcd(username, KeyType.PrivateKey);
		return value.equals(password) ? true : false;*/
	}

	@PostConstruct
	public void init() {
		//privateKey = IoUtil.readObj(AuthService.class.getClassLoader().getResourceAsStream("keystore/auth-private.key"));
	}

}
