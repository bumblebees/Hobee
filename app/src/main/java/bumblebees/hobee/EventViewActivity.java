package bumblebees.hobee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.MQTTMessageReceiver;


public class EventViewActivity extends AppCompatActivity {
   TextView eventName, eventDescription, eventLocation, eventDate, eventTime,eventPeople,eventGender;
    String eventID;
    String eventJson;

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

        Intent intent = getIntent();
        eventID = intent.getStringExtra("eventID");
        String category = intent.getStringExtra("category");

        MQTT mqtt = MQTT.getInstance();
        String topic = "hobby/event/" + category + "/" + eventID;
        mqtt.subscribe(topic, 1, new MQTTMessageReceiver() {
            @Override
            public void onMessageReceive(MqttMessage message) {
                Log.d("mqtt", "hello");
                JSONObject data = null;
                try {
                    data = new JSONObject(message.toString());
                    final JSONObject event = data.getJSONObject("event");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                eventName.setText(event.getString("name"));
                                eventDescription.setText(event.getString("description"));
                                eventLocation.setText(event.getString("location"));
                                eventDate.setText(event.getString("date"));
                                eventTime.setText(event.getString("time"));
                                eventGender.setText(event.getString("gender"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void setEventJson(String eventJson){
        this.eventJson = eventJson;
    }

    class Events {
        private String name, location, time, date, gender, description, category, timeCreated,
                eventId;
        private int maxPpl;

        Events(String name, String location, String time,
               String date, String gender, String description, int maxPpl,
               String category, String timeCreated, String eventId){
            this.name = name;
            this.location = location;
            this.time = time;
            this.date = date;
            this.gender = gender;
            this.description = description;
        }
    }
}
