/**
 * 
 */

package com.todostudy.iot.mqtt.server.store.message;

import com.todostudy.iot.mqtt.server.common.message.IMessageIdService;
import com.todostudy.iot.mqtt.server.store.cache.RedisServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.Collections;

@Slf4j
public class MessageIdRedisService implements IMessageIdService {

	private final RedisServices redisServices;

	//分布式锁过期时间 s  可以根据业务自己调节
	private static final Long LOCK_REDIS_TIMEOUT = 10L;
	//分布式锁休眠 至 再次尝试获取 的等待时间 ms 可以根据业务自己调节
	public static final Long LOCK_REDIS_WAIT = 500L;

	private final int MIN_MSG_ID = 1;

	private final int MAX_MSG_ID = 65535;

	private final int lock = 0;


	final static String inc_re_key="mqttId";

	private int nextMsgId = MIN_MSG_ID - 1;
	private String logck_str= "locke";

	public MessageIdRedisService(RedisServices redisServices) {
		this.redisServices = redisServices;
	}


	/**
	 *  加锁
	 **/
	public Boolean getLock(String key,String value){
		Boolean lockStatus =redisServices.getRedisTemplate().opsForValue().setIfAbsent(key,value, Duration.ofSeconds(LOCK_REDIS_TIMEOUT));
		return lockStatus;
	}

	/**
	 *  释放锁
	 **/
	public Long releaseLock(String key,String value){
		String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript,Long.class);
		Long releaseStatus = (Long)redisServices.getRedisTemplate().execute(redisScript, Collections.singletonList(key),value);
		return releaseStatus;
	}

	@Override
	public int getNextMessageId() {
		try {
			if (getLock(inc_re_key, logck_str)) {
				//自增id 需要加锁
				nextMsgId = Integer.parseInt(redisServices.getRedisTemplate().opsForValue().increment(inc_re_key, nextMsgId) + "");
				if (nextMsgId > MAX_MSG_ID) {
					redisServices.getRedisTemplate().opsForValue().set(inc_re_key, MIN_MSG_ID);//从头开始
				}
				return nextMsgId;
			}else{
				Thread.sleep(2000);
				log.warn("getMessageId is lock waiting...");
				return getNextMessageId();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			releaseLock(inc_re_key, logck_str);//释放锁
		}

	}

	@Override
	public void releaseMessageId(int messageId) {
	}
}
