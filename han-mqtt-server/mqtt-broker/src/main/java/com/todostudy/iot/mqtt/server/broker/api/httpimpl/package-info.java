package com.todostudy.iot.mqtt.server.broker.api.httpimpl;

/**
 * 暴露接口出去，支持 python,go,php 等服务端的接入。
 * 如果需要接入 消息队列，可以在该接口中接入。即将收到的消息放到 Kafka 等组件中。再到业务代码去消费。
 */