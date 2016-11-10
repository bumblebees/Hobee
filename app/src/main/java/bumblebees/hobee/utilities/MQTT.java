package bumblebees.hobee.utilities;


import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

/**
 * Singleton class that provides access to the MQTT service.
 */
public class MQTT implements MqttCallback {

    private String clientID = MqttClient.generateClientId();
    private String mqttAddress = "tcp://129.16.155.22:1883";
    MqttAndroidClient client;

    private static MQTT instance;

    public static MQTT getInstance(){
        if (instance == null){
            synchronized (MQTT.class){
                if(instance == null){
                    instance = new MQTT();
                }
            }
        }
        return instance;
    }

    //empty constructor
    private MQTT(){

    }

    public String getClientID(){
        return this.clientID;
    }

    public void connect(Context context){
        if(client == null){
            client =  new MqttAndroidClient(context, mqttAddress , clientID);
            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("mqtt", "connected");

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("mqtt", "something went wrong");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publishMessage(String topic, MqttMessage message){
       try{
           client.publish(topic, message);

       } catch (MqttPersistenceException e) {
           e.printStackTrace();
       } catch (MqttException e) {
           e.printStackTrace();
       }
    }

    public void subscribe(String topic, int QoS){
        try {
            IMqttToken subToken = client.subscribe(topic, QoS);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String topic){

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        switch(topic){
            case "hobee/test2":
                //do something here
                Log.d("msg", message.getPayload().toString());
                break;
            default:
                //do something else here
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
