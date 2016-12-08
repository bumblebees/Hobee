package bumblebees.hobee.hobbycategories;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView.OnItemSelectedListener;

import bumblebees.hobee.R;
import bumblebees.hobee.jsonparser.JSONParser;
import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SocketIO;

import static android.R.id.list;

public class HobbiesActivity extends AppCompatActivity implements OnItemSelectedListener {

    // Checkbox list
    List<String> checkList = new ArrayList<String>();
    Spinner spinnerDifficultyLevel;
    Spinner spinnerTimeFrom;
    Spinner spinnerTimeTo;

    Hobby hobby;
    TextView textView;
    JSONParser jsonParser;
    CheckBox checkBoxMonday;
    CheckBox checkBoxTuesday;
    CheckBox checkBoxWednesday;
    CheckBox checkBoxThursday;
    CheckBox checkBoxFriday;
    CheckBox checkBoxSaturday;
    CheckBox checkBoxSunday;

    Button submitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);
        String hobbyName = getIntent().getExtras().getString("HobbyName");
        hobby = createHobbyInstance(hobbyName);

        textView = (TextView) findViewById(R.id.name);
        textView.setText(hobby.getName());

        Typeface face= Typeface.createFromAsset(getAssets(), "font/Proxima.ttf");
        textView.setTypeface(face);

        submitBtn = (Button) findViewById(R.id.button_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setHobby();
                SocketIO.getInstance().addHobbyToUser(hobby);
                Intent intent = new Intent(HobbiesActivity.this, HobbiesChoiceActivity.class);
                HobbiesActivity.this.startActivity(intent);
            }
        });

        // Spinner element
        spinnerDifficultyLevel = (Spinner) findViewById(R.id.spinner_difficulty_level);
        //spinnerDifficultyLevel.setPrompt("Title");

        spinnerTimeFrom = (Spinner) findViewById(R.id.spinner_time_from);
        spinnerTimeTo = (Spinner) findViewById(R.id.spinner_time_to);


        // Spinner click listener
        spinnerDifficultyLevel.setOnItemSelectedListener(this);
        spinnerTimeFrom.setOnItemSelectedListener(this);
        spinnerTimeTo.setOnItemSelectedListener(this);


        // Spinner Drop down elements
        List<String> difficultyList = new ArrayList<String>();
        difficultyList.add("Beginner");
        difficultyList.add("Intermediate");
        difficultyList.add("Expert");

        List<Double> timeListTo = new ArrayList<Double>();
        timeListTo.add(8.00);
        timeListTo.add(10.00);
        timeListTo.add(12.00);
        timeListTo.add(14.00);
        timeListTo.add(16.00);
        timeListTo.add(18.00);
        timeListTo.add(20.00);
        timeListTo.add(22.00);
        timeListTo.add(24.00);

        List<Double> timeListFrom = new ArrayList<Double>();
        timeListFrom.add(8.00);
        timeListFrom.add(10.00);
        timeListFrom.add(12.00);
        timeListFrom.add(14.00);
        timeListFrom.add(16.00);
        timeListFrom.add(18.00);
        timeListFrom.add(20.00);
        timeListFrom.add(22.00);
        timeListFrom.add(24.00);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, difficultyList);
        ArrayAdapter<Double> dataAdapter2 = new ArrayAdapter<Double>(this, android.R.layout.simple_spinner_item, timeListFrom);
        ArrayAdapter<Double> dataAdapter3 = new ArrayAdapter<Double>(this, android.R.layout.simple_spinner_item, timeListTo);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerDifficultyLevel.setAdapter(dataAdapter1);
        spinnerTimeFrom.setAdapter(dataAdapter2);
        spinnerTimeTo.setAdapter(dataAdapter3);

        // Checkbox element
        checkBoxMonday = (CheckBox)findViewById(R.id.checkBox_monday);
        checkBoxTuesday = (CheckBox)findViewById(R.id.checkBox_tuesday);
        checkBoxWednesday = (CheckBox)findViewById(R.id.checkBox_wednesday);
        checkBoxThursday = (CheckBox)findViewById(R.id.checkBox_thursday);
        checkBoxFriday = (CheckBox)findViewById(R.id.checkBox_friday);
        checkBoxSaturday = (CheckBox)findViewById(R.id.checkBox_saturday);
        checkBoxSunday = (CheckBox)findViewById(R.id.checkBox_sunday);
    }

    /**
     *
     * @param view
     */
    private void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkBox_monday:
                hobby.setDatePreference(checkBoxMonday.getTag().toString());
                //checkList.add(checkBoxMonday.getTag().toString());
                break;

            case R.id.checkBox_tuesday:
                hobby.setDatePreference(checkBoxTuesday.getTag().toString());

                //checkList.add(checkBoxTuesday.getTag().toString());
                break;

            case R.id.checkBox_wednesday:
                hobby.setDatePreference(checkBoxWednesday.getTag().toString());

                //checkList.add(checkBoxWednesday.getTag().toString());
                break;

            case R.id.checkBox_thursday:
                hobby.setDatePreference(checkBoxThursday.getTag().toString());
                //checkList.add(checkBoxThursday.getTag().toString());
                break;

            case R.id.checkBox_friday:
                hobby.setDatePreference(checkBoxFriday.getTag().toString());
                //checkList.add(checkBoxFriday.getTag().toString());
                break;

            case R.id.checkBox_saturday:
                hobby.setDatePreference(checkBoxSaturday.getTag().toString());
                //checkList.add(checkBoxSaturday.getTag().toString());
                break;

            case R.id.checkBox_sunday:
                hobby.setDatePreference(checkBoxSunday.getTag().toString());
                //checkList.add(checkBoxSunday.getTag().toString());
                break;
        }
    }

    /**
     *
     * @param hobbyName
     * @return
     */
    private Hobby createHobbyInstance (String hobbyName) {
        Hobby hobby = new Hobby(hobbyName);
        return hobby;
    }

    /**
     * Reads all information from user input and populates the hobby with the correct values
     */
    private void setHobby(){
        hobby.setDifficultyLevel(spinnerDifficultyLevel.getSelectedItem().toString());
        hobby.setTimeFrom(Double.parseDouble(spinnerTimeFrom.getSelectedItem().toString()));
        hobby.setTimeTo(Double.parseDouble(spinnerTimeTo.getSelectedItem().toString()));
    }

    /*
    * Toast
    */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void addListenerOnButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });
    }



}

