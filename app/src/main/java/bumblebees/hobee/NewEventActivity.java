package bumblebees.hobee;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import bumblebees.hobee.utilities.SessionManager;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Calendar;
import bumblebees.hobee.utilities.MQTT;
import io.apptik.widget.MultiSlider;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class NewEventActivity extends AppCompatActivity {

    Button btnAddEvent;

    TextView inputEventName;
    TextView inputEventLocation;
    TextView inputEventDescription;
    TextView maxAge;
    TextView minAge;
    static TextView inputEventDate;
    static TextView inputEventTime;
    Spinner inputEventGender;
    Spinner eventHobbyChoice;
    TextView inputEventNumber;
    MultiSlider ageRangeSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);


        inputEventName = (TextView) findViewById(R.id.inputEventName);
        inputEventDescription = (TextView) findViewById(R.id.inputEventDescription);
        inputEventLocation = (TextView) findViewById(R.id.inputEventLocation);
        inputEventDate = (TextView) findViewById(R.id.inputEventDate);
        inputEventTime = (TextView) findViewById(R.id.inputEventTime);
        inputEventGender = (Spinner) findViewById(R.id.inputEventGender);
        inputEventNumber = (TextView) findViewById(R.id.inputEventNumber);
        ageRangeSlider = (MultiSlider) findViewById(R.id.age_range_slider);
        maxAge = (TextView) findViewById(R.id.maxAge);
        minAge = (TextView) findViewById(R.id.minAge);
        minAge.setText(String.valueOf(ageRangeSlider.getThumb(0).getValue()));
        maxAge.setText(String.valueOf(ageRangeSlider.getThumb(1).getValue()));
        eventHobbyChoice = (Spinner) findViewById(R.id.eventHobbyChoice);


        //set gender spinner options
        ArrayAdapter<String> genderChoice = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"any gender", "male", "female"});
        inputEventGender.setAdapter(genderChoice);



        //TODO: get these from your currently available hobbies

        String[] hobbyChoices = {"basketball", "football", "fishing", "cooking"};
//        String[] hobbyChoices = new String[0];
//        JSONArray hobbies;
//        try {
//            hobbies = userData.getJSONArray("hobbies");
//            hobbyChoices = new String[hobbies.length()];
//            for(int i=0; i<hobbies.length(); i++){
//                hobbyChoices[i]=hobbies.getString(i);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        
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
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");

            }
        });

        inputEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
/**
 ageRangeSlider.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
@Override public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
if (thumbIndex == 0) {
minAge.setText(String.valueOf(value));
} else {
maxAge.setText(String.valueOf(value));
}
}
});
 **/


    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            NewEventActivity.inputEventTime.setText(hourOfDay + ":" + minute);
            if (hourOfDay < 10 && minute < 10)
                NewEventActivity.inputEventTime.setText("0" + hourOfDay + ":0" + minute);
            else {
                if (hourOfDay < 10)
                    NewEventActivity.inputEventTime.setText("0" + hourOfDay + ":" + minute);
                if (minute < 10)
                    NewEventActivity.inputEventTime.setText(hourOfDay + ":0" + minute);
            }

        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            NewEventActivity.inputEventDate.setText(year + "-" + month + "-" + day);
            if (month < 10 && day < 10)
                NewEventActivity.inputEventDate.setText(year + "-0" + month + "-0" + day);
            else {
                if (month < 10)
                    NewEventActivity.inputEventDate.setText(year + "-0" + month + "-" + day);
                if (day < 10)
                    NewEventActivity.inputEventDate.setText(year + "-" + month + "-0" + day);
            }
        }
    }

    /**
     * Generates a hash to be used as ID for MQTT topics.
     * Current implementation hashes a  string combination of useID+timestamp
     *
     * @param id   name of the event
     * @param time UNIX timestamp when the event was created
     * @return hash
     */
    //TODO:figure out a better way to implement this so that it is for sure unique and easy to generate
    public String generateHash(String id, long time) {
        String hash;
        String toBeHashed = id + time;
        hash = Base64.encodeToString(String.valueOf(toBeHashed.hashCode()).getBytes(), Base64.URL_SAFE | Base64.NO_PADDING);
        return hash;
    }


    /**
     * Creates the JSON that will be sent over MQTT using the completed fields in the form.
     */
    public void addNewEvent() {
        final SessionManager session = new SessionManager(this.getApplicationContext());

        long timeCreated = Calendar.getInstance().getTimeInMillis() / 1000L;
        String eventCategory = eventHobbyChoice.getSelectedItem().toString();
        String hostID = session.getId();
        String hash = generateHash(hostID, timeCreated);

        JSONObject event = new JSONObject();
        JSONObject host = new JSONObject();
        JSONObject eventDetails = new JSONObject();
        JSONObject hobbyDetails = new JSONObject();
        try {
            //TODO: retrieve this data from somewhere
            host.put("id", hostID);
            host.put("name", "host_name");

            eventDetails.put("name", inputEventName.getText());
            eventDetails.put("location", inputEventLocation.getText());
            eventDetails.put("time", inputEventTime.getText());
            eventDetails.put("date", inputEventDate.getText());
            eventDetails.put("gender", inputEventGender.getSelectedItem().toString());
            eventDetails.put("description", inputEventDescription.getText());
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
        msg.setRetained(true);
        String topic = "hobby/event/" + eventCategory + "/" + hash;
        Log.d("mqtt", topic);
        MQTT.getInstance().publishMessage(topic, msg);

        Context context = getApplicationContext();
        CharSequence text = "Event created!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent homeIntent = new Intent(NewEventActivity.this, HomeActivity.class);
        NewEventActivity.this.startActivity(homeIntent);




    }
}


