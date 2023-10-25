package com.todostudy.tools.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * author:laich  Date:2019/9/10
 *
 * @Desciprtion: reids 配置 可以通过@Import 导入
 */
//@Configuration
//@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    //    为了在service中调用redis的操作类, 提前通过@Bean的方式准备好
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
        // 实例化一个RedisTemplate(操作redis中不同数据类型的CRUD)
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        // 给template配置一个factory
        template.setConnectionFactory(factory);

        //配置序列化器: 针对String和hash采用何种序列化方式(java把数据传给redis时使用何种格式: jdk/string/jackson)

        //Jackson序列化器
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //String序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        // user[name=张三] -> {"user":{"name":"张三"}}
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
