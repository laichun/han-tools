package com.laich.test;

import com.todostudy.iot.mqtt.server.common.subscribe.SubscribeStore;
import com.todostudy.iot.mqtt.server.store.subscribe.SubscribeRedisStoreService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//@SpringBootTest(classes = BrokerApplication.class)
//@RunWith(SpringRunner.class)
public class RedisTs {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }
    @Autowired
    SubscribeRedisStoreService subscribeStoreService;

    public void tsMq(){
        redisTemplate.delete("va_001");
        SubscribeStore subscribeStore = new SubscribeStore("laich","sys/laich/#",0);
        redisTemplate.opsForValue().set("va_001",subscribeStore);
        SubscribeStore va_001 = (SubscribeStore) redisTemplate.opsForValue().get("va_001");
        System.out.println(va_001.getClientId());
    }


    public void tsMqMap(){
       // redisTemplate.delete("va_001");
        String topicFilter = "sys/laich/#";
        SubscribeStore subscribeStore = new SubscribeStore("laich","sys/laich/#",0);
        Map map =
                redisTemplate.opsForHash().entries(topicFilter);//!=null ? (ConcurrentHashMap<String, SubscribeStore>) redisTemplate.opsForHash().entries(topicFilter) : new ConcurrentHashMap<String, SubscribeStore>();
        if(map.isEmpty()){
            map=new ConcurrentHashMap<String, SubscribeStore>();
        }
        map.put(subscribeStore.getClientId(), subscribeStore);
        //subscribeWildcardCache.put(topicFilter, map);
        redisTemplate.opsForHash().putAll(topicFilter,map);
        ConcurrentHashMap<String, SubscribeStore> entries = (ConcurrentHashMap<String, SubscribeStore>) redisTemplate.opsForHash().entries(topicFilter);
        System.out.println(entries);
    }


    public void tsMqMap2() throws InterruptedException {
        SubscribeStore subscribeStore = new SubscribeStore("laich","sys/laich/#",0);
        Map map =new HashMap();
        map.put("hanson",subscribeStore);
        this.setCacheObject("aaa",map);
        Map aaa = this.getCacheObject("aaa");
        System.out.println(aaa.get("hanson"));
        Thread.sleep(1000*20);
    }

    @Test
    public void tsMqSearch(){
        List<SubscribeStore> search = subscribeStoreService.search("sys/laich/123");
        System.out.println(search);
    }

    @Test
    public void computeIfAbsent(){
        HashMap<String, Set<String>> hashMap = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.add("zhangSan");
        hashMap.put("china", set);
        // after JDK1.8
        hashMap.computeIfAbsent("china", key -> getValues(key)).add("liSi");
        System.out.println(hashMap.toString());
    }
    public static HashSet getValues(String key) {
        return new HashSet();
    }


}
