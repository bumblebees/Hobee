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


import java.util.ArrayList;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.HobbyExpandableListAdapter;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SocketIO;


public class EventsHistoryFragment extends Fragment {

    ArrayList<Pair<String, ArrayList<Event>>> content;


    ExpandableListView eventsTabList;
    SwipeRefreshLayout refreshLayout;

    HobbyExpandableListAdapter adapter;
    ArrayList<Event> hostedEvents = new ArrayList<>();
    ArrayList<Event> joinedEvents = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = new ArrayList<>();
        ArrayList<Event> eventHistory = Profile.getInstance().getHistoryEvents();
        for(Event event: eventHistory){
            if(event.getEvent_details().getHost_id().equals(Profile.getInstance().getUserID()))
                hostedEvents.add(event);
            else
                joinedEvents.add(event);
        }

        content.add(new Pair<>("Hosted events", hostedEvents));
        content.add(new Pair<>("Joined events", joinedEvents));
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
        adapter = new HobbyExpandableListAdapter(getActivity().getApplicationContext(), content);
        eventsTabList.setAdapter(adapter);


    }
}
