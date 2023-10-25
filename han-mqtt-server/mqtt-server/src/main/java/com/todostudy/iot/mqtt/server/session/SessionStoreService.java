/**
 * 
 */

package com.todostudy.iot.mqtt.server.session;

import com.todostudy.iot.mqtt.server.common.session.ISessionStoreService;
import com.todostudy.iot.mqtt.server.common.session.SessionStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务，存内存，mqtt长连接，不需要加入到 redis中。
 */
public class SessionStoreService implements ISessionStoreService {

	private Map<String, SessionStore> sessionCache = new ConcurrentHashMap<String, SessionStore>();

	@Override
	public void put(String clientId, SessionStore sessionStore) {
		sessionCache.put(clientId, sessionStore);
	}

	@Override
	public SessionStore get(String clientId) {
		return sessionCache.get(clientId);
	}

	@Override
	public boolean containsKey(String clientId) {
		return sessionCache.containsKey(clientId);
	}

	@Override
	public void remove(String clientId) {
		sessionCache.remove(clientId);
	}
}
