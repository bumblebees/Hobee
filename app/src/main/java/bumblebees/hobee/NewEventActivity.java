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

import com.google.gson.Gson;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.EventDetails;
import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.SimpleUser;
import bumblebees.hobee.utilities.DatePickerFragment;
import bumblebees.hobee.utilities.SessionManager;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.TimePickerFragment;
import io.apptik.widget.MultiSlider;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class NewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Button btnAddEvent;
    TextView maxAge;
    TextView minAge;
    TextView inputEventName;
    TextView inputEventLocation;
    TextView inputEventDescription;
    TextView inputEventDate;
    TextView inputEventTime;
    Spinner inputEventGender;
    Spinner eventHobbyChoice;
    TextView inputEventNumber;
    MultiSlider ageRangeSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        View v = findViewById(android.R.id.content);

        inputEventName = (TextView) findViewById(R.id.inputEventName);
        inputEventDescription = (TextView) findViewById(R.id.inputEventDescription);
        inputEventLocation = (TextView) findViewById(R.id.inputEventLocation);
        inputEventDate = (TextView) findViewById(R.id.inputEventDate);
        inputEventTime = (TextView) findViewById(R.id.inputEventTime);
        inputEventGender = (Spinner) findViewById(R.id.inputEventGender);
        inputEventNumber = (TextView) findViewById(R.id.inputEventNumber);
        ageRangeSlider = (MultiSlider) v.findViewById(R.id.age_range_slider);
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

        ageRangeSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
         @Override public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
            if (thumbIndex == 0) {
                minAge.setText(String.valueOf(value));
            } else {
                maxAge.setText(String.valueOf(value));
            }
        }
    });

    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        month = month + 1;
        inputEventDate.setText(year + "-" + month + "-" + day);
        if (month < 10 && day < 10)
            inputEventDate.setText(year + "-0" + month + "-0" + day);
        else {
            if (month < 10)
                inputEventDate.setText(year + "-0" + month + "-" + day);
            if (day < 10)
                inputEventDate.setText(year + "-" + month + "-0" + day);
        }
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        inputEventTime.setText(hourOfDay + ":" + minute);
        if (hourOfDay < 10 && minute < 10)
            inputEventTime.setText("0" + hourOfDay + ":0" + minute);
        else {
            if (hourOfDay < 10)
                inputEventTime.setText("0" + hourOfDay + ":" + minute);
            if (minute < 10)
                inputEventTime.setText(hourOfDay + ":0" + minute);
        }
    }

    /**
     * Creates the JSON that will be sent over MQTT using the completed fields in the form.
     */
    public void addNewEvent() {
        final SessionManager session = new SessionManager(this.getApplicationContext());

        long timeCreated = Calendar.getInstance().getTimeInMillis() / 1000L;
        String eventCategory = eventHobbyChoice.getSelectedItem().toString();
        String hostID = session.getId();

        UUID uuid = UUID.randomUUID();

        //TODO: find a way to make this easier to parse?
        String timestamp = "0";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date date = sdf.parse(inputEventDate.getText().toString()+" "+inputEventTime.getText().toString());
            cal.setTime(date);
            timestamp = String.valueOf(cal.getTimeInMillis() / 1000L);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<SimpleUser> acceptedUsers = new ArrayList<>();
        SimpleUser host = new SimpleUser(session.getId(), session.getfirstName(), session.getLastName());
        acceptedUsers.add(host);

        Hobby hobby = new Hobby();
        EventDetails eventDetails = new EventDetails(inputEventName.getText().toString(), hostID, session.getfirstName()+" "+session.getLastName(),
                Integer.parseInt(minAge.getText().toString()), Integer.parseInt(maxAge.getText().toString()), inputEventGender.getSelectedItem().toString(),
                timestamp, Integer.parseInt(inputEventNumber.getText().toString()), inputEventLocation.getText().toString(), inputEventDescription.getText().toString(),
                new ArrayList<SimpleUser>(), acceptedUsers, hobby);

        Event event = new Event(uuid, eventCategory, String.valueOf(timeCreated), eventDetails);

        Gson g = new Gson();

        MqttMessage msg = new MqttMessage(g.toJson(event, Event.class).getBytes());
        msg.setRetained(true);
        String topic = "hobby/event/" + eventCategory + "/" + uuid.toString();
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


