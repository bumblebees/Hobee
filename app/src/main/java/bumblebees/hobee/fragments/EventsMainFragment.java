package bumblebees.hobee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.HobbyExpandableListAdapter;
import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.MQTTMessageReceiver;
import bumblebees.hobee.utilities.Notification;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SocketIO;
import io.socket.client.Ack;


public class EventsMainFragment extends Fragment {

    Gson gson = new Gson();
    ArrayList<Pair<String, ArrayList<Event>>> content;

    ExpandableListView eventsTabList;

    ArrayList<Event> hostedEvents, acceptedEvents, pendingEvents;
    ArrayList<String> hostedEventsTopics, acceptedEventsTopics, pendingEventsTopics;

    HobbyExpandableListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hostedEvents = new ArrayList<>();
        acceptedEvents = new ArrayList<>();
        pendingEvents = new ArrayList<>();
        hostedEventsTopics = new ArrayList<>();
        acceptedEventsTopics = new ArrayList<>();
        pendingEventsTopics = new ArrayList<>();

        content = new ArrayList<>();

        getHostedEvents();
        getPendingEvents();
        getAcceptedEvents();

        content.add(new Pair<>("Hosted events", hostedEvents));
        content.add(new Pair<>("Joined events", acceptedEvents));
        content.add(new Pair<>("Pending events", pendingEvents));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.events_tab, container, false);
        eventsTabList = (ExpandableListView) view.findViewById(R.id.eventsTabList);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new HobbyExpandableListAdapter(getActivity().getApplicationContext(), content);
        eventsTabList.setAdapter(adapter);

        //expand all groups by default
        for(int i=0;i<3; i++){
            eventsTabList.expandGroup(i);
        }
    }


    public void getHostedEvents(){
        SocketIO.getInstance().getHostedEvents(Profile.getInstance().getUserID(), new Ack() {
            @Override
            public void call(Object... objects) {
                JSONArray array = (JSONArray) objects[0];
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject obj = array.getJSONObject(i);
                        String topic = "hobby/event/" + obj.getString("type") + "/" + obj.getString("eventID");
                        MQTT.getInstance().subscribe(topic, 1, new MQTTMessageReceiver() {
                            @Override
                            public void onMessageReceive(MqttMessage message) {
                                final Event event = gson.fromJson(message.toString(), Event.class);
                                if(hostedEvents.contains(event)){
                                    hostedEvents.remove(event);
                                }
                                hostedEvents.add(event);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getAcceptedEvents(){
        SocketIO.getInstance().getAcceptedEvents(Profile.getInstance().getUserID(), new Ack() {
            @Override
            public void call(Object... objects) {
                JSONArray array = (JSONArray) objects[0];
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject obj = array.getJSONObject(i);
                        String topic = "hobby/event/" + obj.getString("type") + "/" + obj.getString("eventID");
                        MQTT.getInstance().subscribe(topic, 1, new MQTTMessageReceiver() {
                            @Override
                            public void onMessageReceive(MqttMessage message) {
                                final Event event = gson.fromJson(message.toString(), Event.class);
                                if(acceptedEvents.contains(event)){
                                    acceptedEvents.remove(event);
                                }
                                acceptedEvents.add(event);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    public void getPendingEvents(){
        SocketIO.getInstance().getPendingEvents(Profile.getInstance().getUserID(), new Ack() {
            @Override
            public void call(Object... objects) {
                JSONArray array = (JSONArray) objects[0];
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject obj = array.getJSONObject(i);
                        final String topic = "hobby/event/" + obj.getString("type") + "/" + obj.getString("eventID");
                        MQTT.getInstance().subscribe(topic, 1, new MQTTMessageReceiver() {
                            @Override
                            public void onMessageReceive(MqttMessage message) {
                                final Event event = gson.fromJson(message.toString(), Event.class);
                                if(pendingEvents.contains(event)){
                                    pendingEvents.remove(event);
                                }
                                if(event.getEvent_details().getUsers_accepted().contains(Profile.getInstance().getUser().getSimpleUser())){
                                    acceptedEvents.add(event);
                                    new Notification(getActivity()).sendUserEventAccepted(event);
                                }
                                else if (!event.getEvent_details().getUsers_pending().contains(Profile.getInstance().getUser().getSimpleUser())){
                                      new Notification(getActivity()).sendUserEventRejected(event);
                                      MQTT.getInstance().unsubscribe(topic);
                                }
                                else{
                                    pendingEvents.add(event);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

}
