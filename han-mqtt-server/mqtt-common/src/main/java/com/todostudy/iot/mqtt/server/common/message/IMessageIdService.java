/**
 * @author: hanson
 * email :317856394@qq.com
 */

package com.todostudy.iot.mqtt.server.common.message;

/**
 * 分布式生成报文标识符
 */
public interface IMessageIdService {

	/**
	 * 获取报文标识符
	 */
	int getMessageId(String clientId);

}
