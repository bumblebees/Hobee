package bumblebees.hobee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import bumblebees.hobee.utilities.MQTT;

public class NewEventActivity extends AppCompatActivity {

    Button btnAddEvent;

    TextView inputEventName;
    TextView inputEventLocation;
    TextView inputEventDescription;
    TextView inputEventDate;
    TextView inputEventTime;
    Spinner inputEventGender;

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

        //set gender spinner options
        ArrayAdapter<String> genderChoice = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"any gender", "male", "female"});
        inputEventGender.setAdapter(genderChoice);

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
     * Creates the JSON that will be sent over MQTT using the completed fields in the form.
     */
    public void addNewEvent(){
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

            event.put("host", host);
            event.put("event", eventDetails);
            //get the current time as a UNIX timestamp
            long dateCreated = System.currentTimeMillis() / 1000L;

            event.put("date_created", dateCreated);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        MqttMessage msg = new MqttMessage(event.toString().getBytes());
        String topic = "hobby/test2";
        MQTT.getInstance().publishMessage(topic, msg);

    }
}
