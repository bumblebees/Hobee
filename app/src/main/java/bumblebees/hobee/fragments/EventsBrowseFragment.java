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


import java.util.ArrayList;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.HobbyExpandableListAdapter;
import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.MQTTMessageReceiver;
import bumblebees.hobee.utilities.Profile;


public class EventsBrowseFragment extends Fragment {

    Gson gson = new Gson();
    ArrayList<Pair<String, ArrayList<Event>>> content;

    //we pretend these are the hobbies for now
    String[] hobbies = {"basketball", "football", "fishing", "cooking"};

    ExpandableListView eventsTabList;
    SwipeRefreshLayout refreshLayout;

    HobbyExpandableListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.events_tab, container, false);
        eventsTabList = (ExpandableListView) view.findViewById(R.id.eventsTabList);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
        findEvents();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new HobbyExpandableListAdapter(getActivity().getApplicationContext(), content);
        eventsTabList.setAdapter(adapter);

        //expand all groups by default
        for(int i=0;i<hobbies.length; i++){
           // eventsTabList.expandGroup(i);
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * Find events that match the user's preferences and add them to the list.
     */
    public void findEvents(){
      for (String hobby : hobbies) {
          Pair<String, ArrayList<Event>> pair = new Pair<>(hobby.toUpperCase(), Profile.getInstance().getEligibleEventList().get(hobby));
          content.add(pair);
      }
  }
}
