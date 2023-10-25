package com.todostudy.iot.mqtt.server.store.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

public class RedisServices {

    final static String MQTT_RETAIN="mqtt:retain:";
    final static String MQTT_DUPPUB="mqtt:duppub:";
    final static String MQTT_PUBREL="mqtt:PUBREL:";

    private final RedisTemplate redisTemplate;

    public RedisServices(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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
