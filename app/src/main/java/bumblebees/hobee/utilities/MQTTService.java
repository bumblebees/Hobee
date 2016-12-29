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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import bumblebees.hobee.broadcastreceiver.PendingNotificationReceiver;
import bumblebees.hobee.objects.CancelledEvent;
import bumblebees.hobee.objects.Deal;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.User;

public class MQTTService extends Service implements MqttCallback {

    private final String TAG = "mqttService";

    SessionManager sessionManager;
    MQTTBinder binder = new MQTTBinder();

    private MqttAndroidClient client;
    private String clientID;

    //HOBEE BROKER
   private String mqttAddress = "tcp://129.16.155.22:1883";

    //PRATA BROKER
     //private String mqttAddress = "tcp://prata.technocreatives.com:1883";

    private User user;
    private EventManager eventManager;
    private HashSet<String> subscribedTopics = new HashSet<>();
    private HashSet<String> possibleTopics = new HashSet<>();
    SharedPreferences preferences;

    private ArrayList<Deal> deals = new ArrayList<Deal>();


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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

    /**
     * Remove an event when it has expired (the event has already taken place).
     */
    public void removeExpiredEvent(Event event){
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload("".getBytes());
            message.setRetained(true);
            message.setQos(1);
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
                options.setCleanSession(true);

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
            if (topic.equals("deal/gogodeals/database/deals")) {
                JSONObject msg = new JSONObject(message.toString());
                //only look for messages that were sent to the requester
                if(msg.getString("id").equals(user.getUserID())) {
                    JSONArray dealArray = msg.getJSONArray("data");
                    for (int i = 0; i < dealArray.length(); i++) {
                        Deal deal = gson.fromJson(dealArray.get(i).toString(), Deal.class);
                        deals.add(deal);
                    }
                }

                //deals are received, unsubscribe
                try {
                    client.unsubscribe("deal/gogodeals/database/deals");
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            } else { //topic is an event topic
                try {
                    Event event = gson.fromJson(message.toString(), Event.class);

                    switch (eventManager.processEvent(user, event)) {
                        case HOST:
                            Intent intent = new Intent(this, PendingNotificationReceiver.class);
                            intent.putExtra("eventManager", gson.toJson(eventManager));
                            PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
                            break;
                        case NEW_ACCEPTED:
                            new Notification(this).sendUserEventAccepted(event);
                            break;
                        case OLD_ACCEPTED:
                            //do nothing
                            break;
                        case PENDING:
                            //do nothing
                            break;
                        case REJECTED:
                            new Notification(this).sendUserEventRejected(event);
                            break;
                        case NEW_MATCH:
                            new Notification(this).sendNewEvent(event);
                            break;
                        case OLD_MATCH:
                            //do nothing
                            break;
                        case NONE:
                            //do nothing
                            break;
                    }
                    sessionManager.saveEvents(eventManager);
                } catch (Exception e) {
                    //check if the message received was a cancelled event
                    try {
                        CancelledEvent cancelledEvent = gson.fromJson(String.valueOf(message), CancelledEvent.class);

                        switch (eventManager.cancelEvent(cancelledEvent.getBasicEvent())) {
                            case HOSTED_EVENT:
                                //do nothing
                                break;
                            case ACCEPTED_EVENT:
                                new Notification(this).sendCancelledEvent(cancelledEvent, "joined");
                                break;
                            case PENDING_EVENT:
                                new Notification(this).sendCancelledEvent(cancelledEvent, "pending");
                                break;
                            case EVENT_NOT_FOUND:
                                //the cancelled event does not concern us
                                //do nothing
                                break;
                        }
                        sessionManager.saveEvents(eventManager);
                    } catch (Exception ee) {
                        //message was something that could not be processed, ignore it
                        ee.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    /**
     * Save data to the shared preferences and refresh the subscribed topics.
     */
    public void updateData(){
        sessionManager.saveDataAndEvents(user, eventManager);
        subscribeTopics();
    }

    /**
     * Retrieve deals from GoGoDeals.
     */
    private void getNewDeals(){
        JSONObject data = new JSONObject();
        JSONObject msg = new JSONObject();
        JSONArray filters = new JSONArray();

        try {
            //location is set to Lindholmen by default
            data.put("longitude", 11.938287);
            data.put("latitude", 57.707760);
            filters.put(new JSONObject().put("filter", "random"));
            filters.put(new JSONObject().put("filter", "clothes"));
            filters.put(new JSONObject().put("filter", "stuff"));
            filters.put(new JSONObject().put("filter", "food"));
            data.put("filters", filters);
            msg.put("id", user.getUserID());
            msg.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MqttMessage message = new MqttMessage(msg.toString().getBytes());
        message.setQos(1);
        try {
            client.publish("deal/gogodeals/deal/fetch", message);
            client.subscribe("deal/gogodeals/database/deals", 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a random deal from the list of available deal.
     * After a deal has been shown, it is removed from the pool.
     * @return random Deal
     */
    public Deal getRandomDeal(){
        if(deals.size() == 0){
            return null;
        }
        int number = new Random().nextInt(deals.size());
        Deal deal = deals.remove(number);
        return deal;
    }

    private void subscribeTopics(){

        //also update the available deals, if the preferences allow it and if there are no more old deals to show
        boolean seeDeals = preferences.getBoolean("deals_preference", false);
        if(seeDeals && deals.size()==0) {
            getNewDeals();
        }

        possibleTopics = getPossibleTopics();
        if (possibleTopics.equals(subscribedTopics)) { //nothing has changed
            //do nothing
        } else { //something has changed in the topics
            //copy the original topic sets to modify
            HashSet<String> cSubscribedTopics = (HashSet<String>) subscribedTopics.clone();
            HashSet<String> cPossibleTopics = (HashSet<String>) possibleTopics.clone();

            //subscribe to the additional topics
            cPossibleTopics.removeAll(subscribedTopics);
            for(String topic : cPossibleTopics){
                try {
                    if(subscribedTopics.add(topic)) {
                        client.subscribe(topic, 1);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            //unsubscribe from the topics
            cSubscribedTopics.removeAll(possibleTopics);
            HashSet<String> removedTopics = new HashSet<>();
            for(String topic : cSubscribedTopics){
                try {
                    client.unsubscribe(topic);
                    removedTopics.add(topic);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<Event> expiredEvents = eventManager.findAndRemoveEvents(removedTopics, user.getHobbyNames());
            for(Event event:expiredEvents){
                removeExpiredEvent(event);
            }
            subscribedTopics = possibleTopics;
            sessionManager.saveEvents(eventManager);
        }

    }

    /**
     * Get a list of all possible combination of topics and hobbies.
     * @return list of topics
     */
    private HashSet<String> getPossibleTopics(){
        HashSet<String> topics = new HashSet<>();
        Set<String> emptyLocation = new HashSet<>(); //to prevent null pointer exception
        Set<String> preferencesStringSet = preferences.getStringSet("location_topics", emptyLocation);

        user = sessionManager.getUser();
        ArrayList<String> hobbies = user.getHobbyNames();
        if(!preferencesStringSet.isEmpty()) {
            //create the product of the location and the available hobbies
            for (String location : preferencesStringSet) {
                for (final String hobby : hobbies) {
                    String topic = "geo/" + location + "/event/hobby/" + hobby + "/#";
                    topics.add(topic);
                }
            }
        }
        return topics;
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

