package bumblebees.hobee.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.gson.Gson;


import java.util.ArrayList;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.HobbyExpandableListAdapter;
import bumblebees.hobee.utilities.MQTTService;
import bumblebees.hobee.utilities.Profile;


public class EventsBrowseFragment extends Fragment {

    Gson gson = new Gson();
    ArrayList<Pair<String, ArrayList<Event>>> content;

    ArrayList<String> hobbies = Profile.getInstance().getHobbyNames();

    ExpandableListView eventsTabList;
    SwipeRefreshLayout refreshLayout;

    HobbyExpandableListAdapter adapter;
    private ServiceConnection serviceConnection;
    private MQTTService service;

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

                for (String hobby : hobbies) {

                    Pair<String, ArrayList<Event>> pair = new Pair<>(hobby.toUpperCase(), service.getEvents().getEligibleEventList().get(hobby));
                    content.add(pair);
                }

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
    }
}
