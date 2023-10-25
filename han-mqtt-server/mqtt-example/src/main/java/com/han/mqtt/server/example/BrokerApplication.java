/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.han.mqtt.server.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 通过SpringBoot启动服务
 */
@SpringBootApplication(scanBasePackages = {"com.todostudy.iot.mqtt.server"})
public class BrokerApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(BrokerApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		System.out.println("(♥◠‿◠)ﾉﾞ  Broker模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
		application.run(args);
	}


}
