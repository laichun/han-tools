/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.han.mqtt.server.example.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.todostudy.iot.mqtt.server.api.IAuthService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;

/**
 * 用户名和密码认证服务
 */
@Service
@Slf4j
public class AuthService implements IAuthService {

	//private RSAPrivateKey privateKey;


	public boolean verifyAuth(String username, String password,String clientId) {
		log.info("verifyAuth------------- {} ----", username+"_"+password+"_"+clientId);
		if (StrUtil.isBlank(username)) return false;
		if (StrUtil.isBlank(password)) return false;
		else
			return true;

		/*RSA rsa = new RSA(privateKey, null);
		String value = rsa.encryptBcd(username, KeyType.PrivateKey);
		return value.equals(password) ? true : false;*/
	}

//	@PostConstruct
//	public void init() {
//		privateKey = IoUtil.readObj(AuthService.class.getClassLoader().getResourceAsStream("keystore/auth-private.key"));
//	}

}
