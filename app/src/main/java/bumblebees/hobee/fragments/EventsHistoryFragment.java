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


import java.util.ArrayList;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.HobbyExpandableListAdapter;
import bumblebees.hobee.utilities.MQTTService;
import bumblebees.hobee.utilities.SessionManager;


public class EventsHistoryFragment extends Fragment {

    ArrayList<Pair<String, ArrayList<Event>>> content;

    ExpandableListView eventsTabList;
    SwipeRefreshLayout refreshLayout;

    HobbyExpandableListAdapter adapter;
    SessionManager session;

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

        content.clear();
        content.add(new Pair<>("Hosted events", session.getHistoryHosted()));
        content.add(new Pair<>("Joined events", session.getHistoryJoined()));

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

    /**
     * Retrieve the data from the preferences again and add it to the list again.
     */
    private void updateData(){
        if(adapter!= null){
            content.set(0, new Pair<>("Hosted events", session.getHistoryHosted()));
            content.set(1, new Pair<>("Joined events", session.getHistoryJoined()));
            adapter.notifyDataSetChanged();
        }
    }
}
