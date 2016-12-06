package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.PublicUser;
import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SocketIO;


public class EventViewActivity extends AppCompatActivity {
   TextView eventName, eventDescription, eventLocation, eventDate, eventTime,eventPeople,eventGender, eventAge, eventHostName;
    Gson g;
    String eventString;
    LinearLayout containerUsers, containerPending;
    Event event;
    Button btnJoinEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        eventName = (TextView) findViewById(R.id.eventName);
        eventDescription = (TextView) findViewById(R.id.eventDescription);
        eventLocation = (TextView) findViewById(R.id.eventLocation);
        eventDate = (TextView) findViewById(R.id.eventDate);
        eventTime = (TextView) findViewById(R.id.eventTime);
        eventPeople = (TextView) findViewById(R.id.eventPeople);
        eventGender = (TextView) findViewById(R.id.eventGender);
        eventAge = (TextView) findViewById(R.id.eventAge);
        eventHostName = (TextView) findViewById(R.id.eventHostName);


        containerUsers = (LinearLayout) findViewById(R.id.containerUsers);
        containerPending = (LinearLayout) findViewById(R.id.containerPending);


        Intent intent = getIntent();
        g = new Gson();
        eventString = intent.getStringExtra("event");
        event = g.fromJson(eventString, Event.class);

        btnJoinEvent = (Button) findViewById(R.id.btnJoinEvent);

        PublicUser currentUser = Profile.getInstance().getUser().getSimpleUser();


        //check if the user is also the host of the event
        //if so, disable the join button and show the list of pending users
        Log.d("event", Profile.getInstance().getUserID());
        Log.d("event", event.getEvent_details().getHost_id());

        if(event.getEvent_details().getHost_id().equals(Profile.getInstance().getUserID())){
            btnJoinEvent.setText("Event host");
            btnJoinEvent.setEnabled(false);


            containerPending.setVisibility(View.VISIBLE);

            if(event.getEvent_details().getUsers_pending().size()==0){
                TextView pendingUsers = new TextView(this.getApplicationContext());
                pendingUsers.setText("No users found.");
                containerPending.addView(pendingUsers);
            }
            else {
                for (final PublicUser user : event.getEvent_details().getUsers_pending()) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    final View row = inflater.inflate(R.layout.user_accept_item, containerPending, false);


                    TextView pendingUser = (TextView) row.findViewById(R.id.userAcceptName);
                    pendingUser.setText(user.getName());
                    pendingUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //This needs to be fixed
                            viewUserProfile(user.getUserID());
                        }
                    });

                    Button acceptUser = (Button) row.findViewById(R.id.userAcceptButton);
                    acceptUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            event.getEvent_details().confirmUser(user);
                            updateEvent(event);
                            //TODO: refresh users without having to reload the entire activity
                            finish();
                            Intent updatedIntent = getIntent();
                            updatedIntent.putExtra("event", g.toJson(event));
                            startActivity(updatedIntent);
                        }
                    });
                    Button rejectUser = (Button) row.findViewById(R.id.userRejectButton);
                    rejectUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            event.getEvent_details().rejectUser(user);
                            updateEvent(event);

                            //TODO: refresh users without having to reload the entire activity
                            finish();
                            Intent updatedIntent = getIntent();
                            updatedIntent.putExtra("event", g.toJson(event));
                            startActivity(updatedIntent);
                        }
                    });

                    containerPending.addView(row);

                }
            }




        }
        //check if the user has already joined the event
        else if(event.getEvent_details().checkUser(currentUser)){
            //disable the button so that the user cannot join the event again
            btnJoinEvent.setText("You have already joined this event");
            btnJoinEvent.setEnabled(false);
        }
        else{
            //add the on-click listener so that the user can join the event

            btnJoinEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinEvent(event);
                }
            });
        }
        eventName.setText(event.getEvent_details().getEvent_name());
        eventDescription.setText(event.getEvent_details().getDescription());
        eventLocation.setText(event.getEvent_details().getLocation());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(event.getEvent_details().getTimestamp())*1000L);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm");

        eventDate.setText(String.valueOf(sdfDate.format(cal.getTime())));
        eventTime.setText(String.valueOf(sdfTime.format(cal.getTime())));

        eventGender.setText(event.getEvent_details().getGender());
        eventPeople.setText(String.valueOf(event.getEvent_details().getMaximum_people()));
        eventAge.setText(event.getEvent_details().getAge_min()+"-"+event.getEvent_details().getAge_max());
        eventHostName.setText(event.getEvent_details().getHost_name());

        for(final PublicUser publicUser : event.getEvent_details().getUsers_accepted()){
            TextView acceptedUser = new TextView(this.getApplicationContext());
            acceptedUser.setText(publicUser.getName());
            acceptedUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewUserProfile(publicUser.getUserID());
                }
            });
            containerUsers.addView(acceptedUser);
        }

        eventHostName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewUserProfile(event.getEvent_details().getHost_id());
            }
        });
    }



    /**
     * Adds the currently logged in user to the event, publishes it to the MQTT topic.
     * Returns to the home activity with a confirmation Toast.
     * @param event
     */

    public void joinEvent(Event event){
        PublicUser currentUser = Profile.getInstance().getUser().getSimpleUser();
        event.getEvent_details().addUser(currentUser);

        updateEvent(event);
        Profile.getInstance().removeEligibleEvent(event.getType(), event);

        //TODO: add event to user's events

        Context context = getApplicationContext();
        CharSequence text = "Event joined!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent homeIntent = new Intent(EventViewActivity.this, HomeActivity.class);
        EventViewActivity.this.startActivity(homeIntent);

    }

    /**
     * Updates the event on the MQTT broker.
     * @param event - event to be updated
     */
    private void updateEvent(Event event) {
        String topic = event.getTopic();
        MqttMessage message = new MqttMessage(g.toJson(event).getBytes());
        message.setRetained(true);
        message.setQos(1);
        MQTT.getInstance().publishMessage(topic, message);

    }

    public void viewUserProfile(String userID){
        SocketIO.getInstance().getUserAndOpenProfile(userID,getApplicationContext());
    }

    public void rankEvent(View view){
        SocketIO.getInstance().sendUserIDArrayAndOpenRankActivity(eventString, event.getEvent_details().getUsers_unranked());
    }


}
