package han;

import org.eclipse.paho.client.mqttv3.*;

public class PublishMessage implements MqttCallback {
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，可以做重连");
//            connectMqtt();
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("deliveryComplete---------" + token.isComplete());
    }

    public void messageArrived(String topic, MqttMessage message) {
        // subscribe后得到的消息会执行到这里面
        byte[] messByte = message.getPayload();
        System.out.println("接收消息主题 : " + topic);
        System.out.println("接收消息Qos : " + message.getQos());
        try {
//                System.out.println("接收消息内容 : " + new String(messByte).getBytes("utf8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (topic == null) {
            return;
        }
        try {
            String data = new String(messByte, "utf-8");
           // publish(new MqttTopic("/topic/1",1),)
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void publish(MqttTopic topic , MqttMessage message) throws MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! " + token.isComplete());
    }

}
