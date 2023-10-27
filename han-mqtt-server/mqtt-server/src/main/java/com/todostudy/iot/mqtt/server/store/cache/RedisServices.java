package com.todostudy.iot.mqtt.server.store.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisServices {

    public final static String MQTT_RETAIN="mqtt:retain:";
    public final static String MQTT_DUPPUB="mqtt:duppub:";
    public final static String MQTT_PUBREL="mqtt:PUBREL:";

    private RedisTemplate redisTemplate;
    /**
     * Retain 消息过期时间
     */
    private long retainMsgTime; //小时

    public RedisServices(RedisTemplate redisTemplate, long retainMsgTime) {
        this.redisTemplate = redisTemplate;
        this.retainMsgTime = retainMsgTime;
    }

    public RedisTemplate getRedisTemplate(){
        return this.redisTemplate;
    }


    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * retainMsg 专用
     */
    public <T> void setCacheObjectRetainMsg(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value, retainMsgTime, TimeUnit.HOURS);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }


    /**
     * clear cache
     * @param key
     */
    public void delete(final String key) {
        redisTemplate.delete(key);
    }

    /**
     * key的查找
     * @param keys
     * @return
     */
    public Set<String> getKeys(String keys) {
       return redisTemplate.keys(keys);
    }
}
