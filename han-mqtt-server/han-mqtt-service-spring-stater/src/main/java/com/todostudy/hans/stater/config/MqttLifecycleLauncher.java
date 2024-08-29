package com.todostudy.hans.stater.config;

import com.todostudy.iot.mqtt.server.MqttBrokerServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;

/**
 * @Author hanson spring 容器启动
 */
@Slf4j
@RequiredArgsConstructor
public class MqttLifecycleLauncher implements SmartLifecycle, Ordered {

    private final MqttBrokerServer mqttServer;
    /**
     * A 组件的运行状态
     */
    private volatile boolean running = false;

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {
        //===banner
        System.out.println(
                " |_|  _. ._   _  _  ._    _|_  _   _  |  _ \n" +
                " | | (_| | | _> (_) | |    |_ (_) (_) | _> \n" +
                "                                           \n" +
                "Hanson tools ^_^ 0524");
        mqttServer.start();
        running = true;
    }

    @Override
    public void stop() {
        mqttServer.stop();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
