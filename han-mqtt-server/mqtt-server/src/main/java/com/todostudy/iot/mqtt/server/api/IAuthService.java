/**
 * 
 */

package com.todostudy.iot.mqtt.server.api;

/**
 * 用户和密码认证服务接口
 */
public interface  IAuthService{

	/**
	 * 验证用户名和密码是否正确
	 */
	boolean verifyAuth(String username, String password,String clientId);


}

