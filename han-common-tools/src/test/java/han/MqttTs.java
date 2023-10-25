package han;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttTs {
    public static void main(String[] args) {
        // MQTT服务器地址
        String broker = "tcp://192.168.6.43:18830";
        // 客户端ID
        String clientId = "JavaMqttClient1";
        // 要订阅和发布的主题
        String topic = "test/topic";

        try {
            // 使用指定的服务器、客户端ID和持久性创建一个MQTT客户端
            MqttClient mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());

            // 创建连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("laich");
            connOpts.setPassword("1234qwer".toCharArray());
            // 设置超时时间
            connOpts.setConnectionTimeout(10);
            // 设置会话心跳时间
            connOpts.setKeepAliveInterval(90);
            // 设置清空会话，这样在客户端重启时不会收到旧消息
            connOpts.setCleanSession(true);

            // 连接到MQTT服务器
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            // 创建订阅回调
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("接收 Message arrived: " + topic + " " + message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete");
                }
            });

            // 订阅主题
            mqttClient.subscribe("bk/trigger/+/list");
            System.out.println("Subscribed to topic: " + "v1/gateway/telemetry");

            // 创建要发布的消息
            String payload = "Hello MQTT!";
            MqttMessage message = new MqttMessage(payload.getBytes());

            // 设置消息的QoS级别 (0=最多一次, 1=最少一次, 2=只有一次)
            message.setQos(0);

            // 发布消息到指定主题
           mqttClient.publish("bk/seq/1/2", message);
            System.out.println("Message published: " + payload);



            // 断开连接
           /* mqttClient.disconnect();
            System.out.println("Disconnected");

            // 关闭客户端
            mqttClient.close();
            System.out.println("MQTT client closed");*/

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
