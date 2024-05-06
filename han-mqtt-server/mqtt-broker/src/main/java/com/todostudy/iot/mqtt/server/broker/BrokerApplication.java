/**
 * @Author: hanson
 * email :317856394@qq.com
 */
package com.todostudy.iot.mqtt.server.broker;

import com.todostudy.hans.stater.config.MqttServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 通过SpringBoot启动服务
 */

@SpringBootApplication(scanBasePackages = {"com.todostudy.iot.mqtt.server.broker"})
public class BrokerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BrokerApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        System.out.println("(♥◠‿◠)ﾉﾞ Hanson Broker模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
        application.run(args);
    }


}
