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
import java.util.HashMap;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.HobbyExpandableListAdapter;
import bumblebees.hobee.utilities.MQTTService;
import bumblebees.hobee.utilities.SessionManager;


public class EventsBrowseFragment extends Fragment {

    ArrayList<Pair<String, ArrayList<Event>>> content;

    ArrayList<String> hobbies;

    ExpandableListView eventsTabList;
    SwipeRefreshLayout refreshLayout;

    HobbyExpandableListAdapter adapter;
    SessionManager session;

    HashMap<String, ArrayList<Event>> browseEvents;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = new ArrayList<>();
        browseEvents = new HashMap<String, ArrayList<Event>>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.events_tab, container, false);
        eventsTabList = (ExpandableListView) view.findViewById(R.id.eventsTabList);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                refreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        session = new SessionManager(getContext());
        hobbies = session.getUser().getHobbyNames();
        browseEvents = session.getBrowseEvents();

        content.clear();
        for (String hobby : hobbies) {
            Pair<String, ArrayList<Event>> pair = new Pair<>(hobby.toUpperCase(), browseEvents.get(hobby));
            content.add(pair);
        }

        adapter = new HobbyExpandableListAdapter(getActivity().getApplicationContext(), content);
        eventsTabList.setAdapter(adapter);

        //expand all groups by default
        for(int i=0;i<content.size(); i++){
            eventsTabList.expandGroup(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       updateData();
    }

    private void updateData(){
        if(adapter!=null){
            browseEvents = session.getBrowseEvents();
            for(int i =0; i<hobbies.size(); i++) {
                Pair<String, ArrayList<Event>> pair = new Pair<>(hobbies.get(i).toUpperCase(), browseEvents.get(hobbies.get(i)));
                content.set(i, pair);

            }
            adapter.notifyDataSetChanged();
        }
    }
}
