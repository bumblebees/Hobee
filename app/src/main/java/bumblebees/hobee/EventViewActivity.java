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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.zip.Inflater;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.SimpleUser;
import bumblebees.hobee.objects.User;
import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.MQTTMessageReceiver;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SessionManager;


public class EventViewActivity extends AppCompatActivity {
   TextView eventName, eventDescription, eventLocation, eventDate, eventTime,eventPeople,eventGender, eventAge, eventHostName;
    Gson g;
    LinearLayout containerUsers, containerPending;

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
        final Event event = g.fromJson(intent.getStringExtra("event"), Event.class);
        SessionManager session = new SessionManager(this.getApplicationContext());

        btnJoinEvent = (Button) findViewById(R.id.btnJoinEvent);

        SimpleUser currentUser = new SimpleUser(session.getId(), Profile.getInstance().getFirstName(), Profile.getInstance().getLastName());


        //check if the user is also the host of the event
        //if so, disable the join button and show the list of pending users
        if(event.getEvent_details().getHost_id().equals(session.getId())){
            btnJoinEvent.setText("Event host");
            btnJoinEvent.setEnabled(false);


            containerPending.setVisibility(View.VISIBLE);

            if(event.getEvent_details().getUsers_pending().size()==0){
                TextView pendingUsers = new TextView(this.getApplicationContext());
                pendingUsers.setText("No users found.");
                containerPending.addView(pendingUsers);
            }
            else {
                for (final SimpleUser user : event.getEvent_details().getUsers_pending()) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    final View row = inflater.inflate(R.layout.user_accept_item, containerPending, false);


                    TextView pendingUser = (TextView) row.findViewById(R.id.userAcceptName);
                    pendingUser.setText(user.getName());
                    pendingUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewUserProfile(user);
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

                    containerPending.addView(row);

                }
            }




        }
        //check if the user has already joined the event
        //TODO: session manager is inconsistent with how event checks and adds users
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

        for(final SimpleUser simpleUser : event.getEvent_details().getUsers_accepted()){
            TextView acceptedUser = new TextView(this.getApplicationContext());
            acceptedUser.setText(simpleUser.getName());
            acceptedUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewUserProfile(simpleUser);
                }
            });
            containerUsers.addView(acceptedUser);
        }

    }



    /**
     * Adds the currently logged in user to the event, publishes it to the MQTT topic.
     * Returns to the home activity with a confirmation Toast.
     * @param event
     */

    public void joinEvent(Event event){
        SessionManager session = new SessionManager(this.getApplicationContext());
        SimpleUser currentUser = new SimpleUser(session.getId(), Profile.getInstance().getFirstName(), Profile.getInstance().getLastName());
        event.getEvent_details().addUser(currentUser);

        updateEvent(event);

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
        String topic = "hobby/event/" + event.getType() + "/" + event.getEventID();
        MqttMessage message = new MqttMessage(g.toJson(event).getBytes());
        message.setRetained(true);
        message.setQos(1);
        MQTT.getInstance().publishMessage(topic, message);

    }

    public void viewUserProfile(SimpleUser user){
        //TODO: open a new activity and pass user as the intent
    }
}
