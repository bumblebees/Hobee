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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import bumblebees.hobee.HomeActivity;

/**
 * Singleton class that provides access to the MQTT service.
 */
public class MQTT implements MqttCallback {

    private String clientID = MqttClient.generateClientId();
    private String mqttAddress = "tcp://129.16.155.22:1883";
    private MqttAndroidClient client;
    private boolean isConnected = false;
    private HashMap<String, ArrayList<MQTTMessageReceiver>> callbackList = new HashMap<>();

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

    public boolean isConnected(){
        return isConnected;
    }

    public void connect(Context context){
        if(client == null){
            client =  new MqttAndroidClient(context, mqttAddress , clientID);
            client.setCallback(this);
            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("mqtt", "connected");
                        isConnected = true;

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

    public void subscribe(final String topic, int QoS, final MQTTMessageReceiver callback){

        try {
            IMqttToken subToken = client.subscribe(topic, QoS);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //subscription was successful
                    ArrayList<MQTTMessageReceiver>list = callbackList.get(topic);
                    if(list==null){
                        list = new ArrayList<>();
                    }
                    list.add(callback);
                    callbackList.put(topic, list);
                    Log.d("mqtt", "subscribed to "+topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(final String topic){
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //unsubscribing was successful, remove all the callbacks
                    callbackList.remove(topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        Log.d("mqtt", "message from: "+topic);
        if(topic.startsWith("hobby/event/")){
            Log.d("mqtt", "pattern match");
            //TODO: make it work for other things than fishing
            String subTopic = "hobby/event/fishing/#";
            Log.d("mqtt", String.valueOf(callbackList.containsKey(subTopic)));
            ArrayList<MQTTMessageReceiver> list = callbackList.get(subTopic);
            for(int i=0; i<list.size(); i++){
                //send the message to the callback function(s)
                Log.d("mqtt", list.get(i).toString());
                list.get(i).onMessageReceive(message);
            }
        }


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
