package bumblebees.hobee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.MQTTMessageReceiver;
import io.socket.client.Ack;

public class HomeActivity extends AppCompatActivity {

    Button btnProfile;
    Button btnLogout;
    Button btnNewEvent;
    Button btnGetEvents;
    SharedPreferences preferences;
    String origin;
    TextView textView;
    LinearLayout eventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MQTT.getInstance().connect(this.getApplicationContext());

        final SessionManager session = new SessionManager(getApplicationContext());

        // for testing
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(session.getId() + " " + session.getOrigin());

        btnNewEvent = (Button) findViewById(R.id.btnCreateEvent);

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newEventIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                HomeActivity.this.startActivity(newEventIntent);
            }
        });

        btnGetEvents = (Button) findViewById(R.id.btnGetEvents);
        btnGetEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeTopics();
            }
        });

        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                HomeActivity.this.startActivity(profileIntent);
            }
        });

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.getOrigin().equals("facebook")){
                    LoginManager.getInstance().logOut();
                }
                else {

                }

                // clear user from preferences
                session.logoutUser();

                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                HomeActivity.this.startActivity(logoutIntent);

            }
        });


        eventList = (LinearLayout) findViewById(R.id.listParticipatingEvents);

        /**
        SocketIO.getEvents(session.getId(), "host", new Ack() {
            @Override
            public void call(Object... objects) {
                JSONArray array = (JSONArray) objects[0];
                for (int i=0; i<array.length(); i++){

                    try {
                        JSONObject data = array.getJSONObject(i);
                        JSONObject event = data.getJSONObject("event");
                        final Button btn = new Button(HomeActivity.this);
                        btn.setText(event.getString("name"));
                        btn.setTag(data.getString("eventID"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                eventList.addView(btn);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                SocketIO.disconnect();
            }
        });

         */
    }


    public void subscribeTopics(){
            //we pretend these are the hobbies for now
            String[] hobbies = {"basketball", "football", "fishing", "cooking"};
            for (int i = 0; i < hobbies.length; i++) {
                String topic = "hobby/event/" + hobbies[i] + "/#";
                MQTT.getInstance().subscribe(topic, 1, new MQTTMessageReceiver() {
                    @Override
                    public void onMessageReceive(MqttMessage message) {
                        Log.d("mqtt", "received message");
                        try {
                            JSONObject data = new JSONObject(message.toString());
                            JSONObject event = data.getJSONObject("event");
                            final Button btn = new Button(HomeActivity.this);
                            btn.setText(data.getString("category")+": "+ event.getString("name"));
                            btn.setTag(data.getString("eventID"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    eventList.addView(btn);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        }

    }

    @Override
    public void onResume(){

        //TODO: refresh event list somehow
        super.onResume();
    }




}
