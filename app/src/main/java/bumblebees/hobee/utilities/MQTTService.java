package bumblebees.hobee.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import bumblebees.hobee.broadcastreceiver.PendingNotificationReceiver;
import bumblebees.hobee.objects.CancelledEvent;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.User;

public class MQTTService extends Service implements MqttCallback {

    private final String TAG = "mqttService";

    SessionManager sessionManager;
    MQTTBinder binder = new MQTTBinder();

    private MqttAndroidClient client;
    private String clientID;
    private String mqttAddress = "tcp://129.16.155.22:1883";

    private User user;
    private EventManager eventManager;



    public MQTTService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        if(client == null){
            connectMQTT();
        }
       return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new SessionManager(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sessionManager.saveDataAndEvents(user, eventManager);
        sendBroadcast(new Intent("hobee.mqtt.RESTART"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(client == null){
            connectMQTT();
        }
        return START_STICKY;
    }

    public void addOrUpdateEvent(Event event) {
        try {
            Gson gson = new GsonBuilder().setVersion(0.3).create();

            MqttMessage message = new MqttMessage();
            message.setPayload(gson.toJson(event).getBytes());
            message.setQos(1);
            message.setRetained(true);

            client.publish(event.getTopic(), message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connectMQTT(){

        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String clientUUID = sessionManager.getUserID();
            if (!(clientUUID == null)) {
                clientID = "hobee-"+clientUUID;
                client = new MqttAndroidClient(this, mqttAddress, clientID, persistence);
                client.setCallback(this);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(false);
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        subscribeTopics();
                        Log.d(TAG, "connected");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "something went wrong");
                    }
                });
                eventManager = sessionManager.getEventManager();
                user = sessionManager.getUser();
                setUpRepeatingTasks();

            }
            else{
                Log.d(TAG, "preferences not set yet, aborting connection");
            }
            }catch(MqttException e){
                e.printStackTrace();
            }

    }


    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "service disconnected");
        connectMQTT();

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, "message arrived from: "+topic);
        Gson gson = new Gson();

        if(message.getPayload().length>0) {
        //TODO: simplify
            try {
                final Event event = gson.fromJson(message.toString(), Event.class);
                //check if the user's preferences match the event and if the user is not already a member of it
                if (event.getEvent_details().getHost_id().equals(user.getUserID())) {
                    //user is the host
                    eventManager.addHostedEvent(event);
                    sessionManager.saveEvents(eventManager);
                    Intent intent = new Intent(this, PendingNotificationReceiver.class);
                    intent.putExtra("eventManager", gson.toJson(eventManager));
                    PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
                } else if (event.getEvent_details().getUsers_pending().contains(user.getSimpleUser())) {
                    //user is in the pending list
                    eventManager.addPendingEvent(event);
                    sessionManager.saveEvents(eventManager);
                } else if (event.getEvent_details().getUsers_accepted().contains(user.getSimpleUser())) {
                    //user is in the accepted list
                    if (eventManager.getPendingEvents().contains(event)) {
                        eventManager.removePendingEvent(event);
                        sessionManager.saveEvents(eventManager);
                        new Notification(this).sendUserEventAccepted(event);
                    }
                    eventManager.addAcceptedEvent(event);
                    sessionManager.saveEvents(eventManager);
                } else if (eventManager.matchesPreferences(event, user)) {
                    //check if user had been pending on the event
                    if (eventManager.getPendingEvents().contains(event)) {
                        eventManager.removePendingEvent(event);
                        sessionManager.saveEvents(eventManager);
                        new Notification(this).sendUserEventRejected(event);
                    }
                    if (eventManager.addEligibleEvent(event.getType(), event)) {
                        new Notification(this).sendNewEvent(event);
                    }
                }
            } catch (Exception e) {
                //check if the message received was a cancelled event
                try {
                    CancelledEvent cancelledEvent = gson.fromJson(String.valueOf(message), CancelledEvent.class);
                    if(eventManager.getHostedEvents().contains(cancelledEvent.getBasicEvent())){
                        //the user is the host
                        eventManager.removeHostedEvent(cancelledEvent.getBasicEvent());
                    }
                    else if(eventManager.getAcceptedEvents().contains(cancelledEvent.getBasicEvent()) || eventManager.getPendingEvents().contains(cancelledEvent.getBasicEvent())){
                        //the user was participating in the event or waiting to be accepted
                        new Notification(this).sendCancelledEvent(cancelledEvent);
                        eventManager.removePendingEvent(cancelledEvent.getBasicEvent());
                        eventManager.getAcceptedEvents().remove(cancelledEvent.getBasicEvent());
                    }
                    sessionManager.saveEvents(eventManager);
                }
                    catch(Exception ee){
                        //message was something that could not be processed, ignore it
                        ee.printStackTrace();
                    }


            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private void updateData(){
        sessionManager.saveDataAndEvents(user, eventManager);
    }

    private void subscribeTopics(){
        final Gson gson = new Gson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> emptyLocation = new HashSet<>(); //to prevent null pointer exception
        Set<String> preferencesStringSet = preferences.getStringSet("location_topics", emptyLocation);

        ArrayList<String> hobbies = user.getHobbyNames();

        if(!preferencesStringSet.isEmpty()){
            for(String location:preferencesStringSet){
                for(final String hobby : hobbies){
                    //subscribe to all topics that match the location and the hobby
                    String topic = "geo/"+location+"/event/hobby/"+hobby+"/#";
                    try {
                        client.subscribe(topic, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



    }

    public EventManager getEvents(){
        return eventManager;
    }

    /**
     * Set up alarms to trigger events such as notifications.
     */
    public void setUpRepeatingTasks(){
        Gson gson = new Gson();
        AlarmManager alarmManager;
        Intent intent = new Intent(this, PendingNotificationReceiver.class);
        intent.putExtra("eventManager", gson.toJson(eventManager));
        PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntentAlarm);


    }


    public class MQTTBinder extends Binder {
        public MQTTService getInstance(){
            return MQTTService.this;
        }


    }

}

