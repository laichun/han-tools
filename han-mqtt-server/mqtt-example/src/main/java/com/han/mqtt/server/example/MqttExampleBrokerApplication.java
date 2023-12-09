/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.han.mqtt.server.example;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 通过SpringBoot启动服务
 */
@SpringBootApplication(scanBasePackages = {})
public class MqttExampleBrokerApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(MqttExampleBrokerApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		System.out.println("(♥◠‿◠)ﾉﾞ  MqttExampleBrokerApplication 模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
		application.run(args);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		// 实例化一个RedisTemplate(操作redis中不同数据类型的CRUD)
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
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
