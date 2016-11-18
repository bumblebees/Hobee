package bumblebees.hobee.utilities;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MQTTMessageReceiver {
    void onMessageReceive(MqttMessage message);

}
