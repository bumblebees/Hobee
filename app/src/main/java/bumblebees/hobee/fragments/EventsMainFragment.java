package bumblebees.hobee.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
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

import bumblebees.hobee.HomeActivity;
import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.HobbyExpandableListAdapter;
import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.MQTTMessageReceiver;
import bumblebees.hobee.utilities.MQTTService;
import bumblebees.hobee.utilities.Notification;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SocketIO;
import io.socket.client.Ack;


public class EventsMainFragment extends Fragment {

    Gson gson = new Gson();
    ArrayList<Pair<String, ArrayList<Event>>> content;

    ExpandableListView eventsTabList;

    HobbyExpandableListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ServiceConnection serviceConnection;
    private MQTTService service;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        content = new ArrayList<>();
        content.add(new Pair<>("Hosted events", new ArrayList<Event>()));
        content.add(new Pair<>("Joined events", new ArrayList<Event>()));
        content.add(new Pair<>("Pending events", new ArrayList<Event>()));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.events_tab, container, false);
        eventsTabList = (ExpandableListView) view.findViewById(R.id.eventsTabList);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(service!=null){
                    service.updateData();
                }
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = new Intent(getContext(), MQTTService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MQTTService.MQTTBinder binder = (MQTTService.MQTTBinder) iBinder;
                service = binder.getInstance();

                content.set(0, new Pair<>("Hosted events", service.getEvents().getHostedEvents()));
                content.set(1, new Pair<>("Joined events", service.getEvents().getAcceptedEvents()));
                content.set(2, new Pair<>("Pending events", service.getEvents().getPendingEvents()));



                //expand all groups by default
                for(int i=0;i<content.size(); i++){
                  eventsTabList.expandGroup(i);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        adapter = new HobbyExpandableListAdapter(getActivity().getApplicationContext(), content);
        eventsTabList.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!= null) {
            adapter.notifyDataSetChanged();

        }
        if(service!=null){
            service.updateData();
        }

    }
}
