package bumblebees.hobee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import bumblebees.hobee.utilities.MQTT;

public class NewEventActivity extends AppCompatActivity {

    Button btnAddEvent;

    TextView inputEventName;
    TextView inputEventLocation;
    TextView inputEventDescription;
    TextView inputEventDate;
    TextView inputEventTime;
    TextView inputEventNumber;
    Spinner inputEventGender;
    Spinner eventHobbyChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        MQTT.getInstance().connect(this.getApplicationContext());

        inputEventName = (TextView) findViewById(R.id.inputEventName);
        inputEventDescription = (TextView) findViewById(R.id.inputEventDescription);
        inputEventLocation = (TextView) findViewById(R.id.inputEventLocation);
        inputEventDate = (TextView) findViewById(R.id.inputEventDate);
        inputEventTime = (TextView) findViewById(R.id.inputEventTime);
        inputEventGender= (Spinner) findViewById(R.id.inputEventGender);
        inputEventNumber = (TextView) findViewById(R.id.inputEventNumber);
        eventHobbyChoice = (Spinner) findViewById(R.id.eventHobbyChoice);


        //set gender spinner options
        ArrayAdapter<String> genderChoice = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"any gender", "male", "female"});
        inputEventGender.setAdapter(genderChoice);

        //TODO: get these from your currently available hobbies
        String[] hobbyChoices = {"basketball", "football", "fishing", "cooking"};
        ArrayAdapter<String> hobbyChoice = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, hobbyChoices);

        eventHobbyChoice.setAdapter(hobbyChoice);

        btnAddEvent = (Button) findViewById(R.id.eventAddNew);

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewEvent();
            }
        });

        inputEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //TODO: open time picker
            }
        });

        inputEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: open date picker
            }
        });
    }

    /**
     * Generates a hash to be used as ID for MQTT topics.
     * Current implementation hashes a  string combination of useID+timestamp
     * @param id name of the event
     * @param time UNIX timestamp when the event was created
     * @return hash
     */
    //TODO:figure out a better way to implement this so that it is for sure unique and easy to generate
    public String generateHash(String id, long time){
        String hash;
        String toBeHashed = id+time;
        hash = Base64.encodeToString(String.valueOf(toBeHashed.hashCode()).getBytes(), Base64.URL_SAFE);
        return hash;
    }


    /**
     * Creates the JSON that will be sent over MQTT using the completed fields in the form.
     */
    public void addNewEvent(){
        long timeCreated = Calendar.getInstance().getTimeInMillis() / 1000L;
        String eventCategory = eventHobbyChoice.getSelectedItem().toString();
        String hostID = "121431535165141"; //temporary value
        String hash = generateHash(hostID, timeCreated);

        JSONObject event = new JSONObject();
        JSONObject host = new JSONObject();
        JSONObject eventDetails = new JSONObject();
        JSONObject hobbyDetails = new JSONObject();
        try {
            //TODO: retrieve this data from somewhere
            host.put("id", "host_id_here");
            host.put("name", "host_name");

            eventDetails.put("name", inputEventName.getText());
            eventDetails.put("location", inputEventLocation.getText());
            eventDetails.put("time", inputEventTime.getText());
            eventDetails.put("date", inputEventDate.getText());
            eventDetails.put("gender", inputEventGender.getSelectedItem().toString());
            eventDetails.put("description", inputEventName.getText());
            eventDetails.put("maximum_people", inputEventNumber.getText());

            event.put("host", host);
            event.put("category", eventHobbyChoice.getSelectedItem().toString());
            event.put("event", eventDetails);
            event.put("createdTime", timeCreated);
            event.put("eventID", hash);



        } catch (JSONException e) {
            e.printStackTrace();
        }


        MqttMessage msg = new MqttMessage(event.toString().getBytes());
        String topic = "hobby/event/"+eventCategory+"/"+hash;
        Log.d("mqtt", topic);
        MQTT.getInstance().publishMessage(topic, msg);

    }
}
