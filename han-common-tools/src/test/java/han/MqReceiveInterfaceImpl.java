package han;

import com.todostudy.tools.service.MqReceiveInterface;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqReceiveInterfaceImpl implements MqReceiveInterface {
    @Override
    public void dealMessageArrived(String topic, MqttMessage message) {
        System.out.println("======接收 Message arrived: " + topic + " " + message);
    }
}
