package bumblebees.hobee.utilities;


import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.gson.Gson;


import java.util.ArrayList;

import bumblebees.hobee.EventViewActivity;
import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.Hobby;

public class HobbyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Pair<String, ArrayList<Event>>> pairArrayList;
    Gson gson = new Gson();

    public HobbyExpandableListAdapter(Context context, ArrayList<Pair<String, ArrayList<Event>>> pairArrayList) {
        this.context = context;
        this.pairArrayList = pairArrayList;
    }



    @Override
    public int getGroupCount() {
        return pairArrayList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(pairArrayList.get(i).second == null){
            return 0;
        }
        return pairArrayList.get(i).second.size();
    }

    @Override
    public Object getGroup(int i) {
        return pairArrayList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return pairArrayList.get(i).second.get(i1);

    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        Pair<String, ArrayList<Event>> pair = pairArrayList.get(i);
;        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.hobby_header_item, viewGroup, false);
        }
        TextView textView = (TextView)view.findViewById(R.id.eventTabHeader);

        textView.setText(pair.first);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final Event event = (Event) getChild(i, i1);

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.event_item, viewGroup, false);
        }
        TextView eventName = (TextView) view.findViewById(R.id.eventName);
        TextView eventHost = (TextView) view.findViewById(R.id.eventHost);
        TextView eventDate = (TextView) view.findViewById(R.id.eventDate);
        TextView eventTime = (TextView) view.findViewById(R.id.eventTime);
        ImageView eventIcon = (ImageView) view.findViewById(R.id.eventIcon);
        LinearLayout eventItemLayout = (LinearLayout) view.findViewById(R.id.eventItemLayout);
        TextView eventPendingCount = (TextView) view.findViewById(R.id.eventPendingCount);

        eventName.setText(event.getEvent_details().getEvent_name());
        eventHost.setText("Hosted by: " + event.getEvent_details().getHost_name());
        eventDate.setText(event.getEvent_details().getDate());
        eventTime.setText(event.getEvent_details().getTime());

        //check if the logged in user is the host of the event
        if(Profile.getInstance().getUserID().equals(event.getEvent_details().getHost_id())) {
            //show the pending count for the event
            if (event.getEvent_details().getUsers_pending().size() > 0) {
                eventPendingCount.setVisibility(View.VISIBLE);
                eventPendingCount.setText(String.valueOf(event.getEvent_details().getUsers_pending().size()));
            }
        }

        eventIcon.setImageResource(event.getEvent_details().getHobbyIcon());

        eventItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewEventIntent = new Intent(context, EventViewActivity.class);
                viewEventIntent.putExtra("event", gson.toJson(event));
                viewEventIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewEventIntent);

            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
