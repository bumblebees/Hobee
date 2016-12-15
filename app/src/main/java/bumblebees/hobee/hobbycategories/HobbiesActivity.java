package bumblebees.hobee.hobbycategories;

import android.content.Intent;
import android.graphics.Typeface;
import android.provider.ContactsContract;
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

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.Gson;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.User;
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
    User user;

    TextView textView;
    CheckBox checkBoxMonday;
    CheckBox checkBoxTuesday;
    CheckBox checkBoxWednesday;
    CheckBox checkBoxThursday;
    CheckBox checkBoxFriday;
    CheckBox checkBoxSaturday;
    CheckBox checkBoxSunday;

    Button submitBtn;
    private Gson gson = new Gson();

    //TODO: change the fields if the hobby already has values in the profile

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);
        String hobbyName = getIntent().getExtras().getString("HobbyName");
        user = Profile.getInstance().getUser();
        createHobby(hobbyName);

        textView = (TextView) findViewById(R.id.name);
        textView.setText(hobby.getName());

        Typeface face= Typeface.createFromAsset(getAssets(), "font/Proxima.ttf");
        textView.setTypeface(face);

        // When the submit button is clicked, all information from user input is added to the hobby
        submitBtn = (Button) findViewById(R.id.button_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setHobby();
                Profile.getInstance().addOrUpdateHobby(hobby);
                SocketIO.getInstance().addHobbyToUser(hobby, Profile.getInstance().getUserID());
                Intent saveAndGoBackIntent = new Intent(HobbiesActivity.this, HobbiesChoiceActivity.class);
                HobbiesActivity.this.startActivity(saveAndGoBackIntent);
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

        // Checkbox element
        checkBoxMonday = (CheckBox)findViewById(R.id.checkBox_monday);
        checkBoxTuesday = (CheckBox)findViewById(R.id.checkBox_tuesday);
        checkBoxWednesday = (CheckBox)findViewById(R.id.checkBox_wednesday);
        checkBoxThursday = (CheckBox)findViewById(R.id.checkBox_thursday);
        checkBoxFriday = (CheckBox)findViewById(R.id.checkBox_friday);
        checkBoxSaturday = (CheckBox)findViewById(R.id.checkBox_saturday);
        checkBoxSunday = (CheckBox)findViewById(R.id.checkBox_sunday);

        // Spinner Drop down elements
        String[] difficultyList = getResources().getStringArray(R.array.hobbySkillOptions);

        List<String> timeListTo = new ArrayList<String>();
        timeListTo.add("08.00");
        timeListTo.add("10.00");
        timeListTo.add("12.00");
        timeListTo.add("14.00");
        timeListTo.add("16.00");
        timeListTo.add("18.00");
        timeListTo.add("20.00");
        timeListTo.add("22.00");
        timeListTo.add("24.00");

        List<String> timeListFrom = new ArrayList<String>();
        timeListFrom.add("08.00");
        timeListFrom.add("10.00");
        timeListFrom.add("12.00");
        timeListFrom.add("14.00");
        timeListFrom.add("16.00");
        timeListFrom.add("18.00");
        timeListFrom.add("20.00");
        timeListFrom.add("22.00");
        timeListFrom.add("24.00");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, difficultyList);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeListFrom);
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeListTo);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerDifficultyLevel.setAdapter(dataAdapter1);
        spinnerTimeFrom.setAdapter(dataAdapter2);
        spinnerTimeTo.setAdapter(dataAdapter3);
    }

    private int setSpinnerDifficultyLevel(String difficultyLevel){
        if (difficultyLevel.equals("Beginner")){
            return 0;
        }
        if (difficultyLevel.equals("Intermediate")){
            return 1;
        }
        return 3;
    }

    private int setSpinnerTime(String time){
        if (time.equals("08.00")){
            return 0;
        }
        if (time.equals("10.00")){
            return 1;
        }
        if (time.equals("12.00")){
            return 2;
        }
        if (time.equals("14.00")){
            return 3;
        }
        if (time.equals("16.00")){
            return 4;
        }
        if (time.equals("18.00")){
            return 5;
        }
        if (time.equals("20.00")){
            return 6;
        }
        if (time.equals("22.00")){
            return 7;
        }
        return 8;
    }

    /**
     * Reads from the selected checkboxes and adds the checked strings to Hobby.DatePreference
     */
    private void readDayOfWeek(){
        if (checkBoxMonday.isChecked()){
            hobby.setDatePreference(checkBoxMonday.getTag().toString());
        }
        if (checkBoxTuesday.isChecked()){
            hobby.setDatePreference(checkBoxTuesday.getTag().toString());
        }
        if (checkBoxWednesday.isChecked()){
            hobby.setDatePreference(checkBoxWednesday.getTag().toString());
        }
        if (checkBoxThursday.isChecked()){
            hobby.setDatePreference(checkBoxThursday.getTag().toString());
        }
        if (checkBoxFriday.isChecked()){
            hobby.setDatePreference(checkBoxFriday.getTag().toString());
        }
        if (checkBoxSaturday.isChecked()){
            hobby.setDatePreference(checkBoxSaturday.getTag().toString());
        }
        if (checkBoxSunday.isChecked()){
            hobby.setDatePreference(checkBoxSunday.getTag().toString());
        }
        else {
            // TODO: ERROR HANDLING SHOULD BE DONE HERE! - Day must be selected
            // Pop up to promt user to choose a date?
            // Not prio, can be done after 16
        }
    }

    /**
     * When this hobby already exists, populate the fields with already existing info
     * @param hobby
     */
    private void getHobbyFields(Hobby hobby){
            spinnerDifficultyLevel.setSelection(setSpinnerDifficultyLevel(hobby.getDifficultyLevel()));
            spinnerTimeFrom.setSelection(setSpinnerTime(hobby.getTimeFrom()));
            spinnerTimeTo.setSelection(setSpinnerTime(hobby.getTimeTo()));
    }

    /**
     * TODO: Write description
     * @param hobbyName
     */
    private void createHobby(String hobbyName){
        if (hobbyExists(hobbyName)) {
            getHobbyFields(hobby);
        } else {
            createHobbyInstanceFromNull(hobbyName);
        }
    }

    private boolean hobbyExists(String hobbyName){
        for (Hobby hobby : Profile.getInstance().getUser().getHobbies()){
            if (hobby.getName().equals(hobbyName)){
               return true;
            }
        }
        return false;
    }


    /**
     * If this hobby doesnt exist, create new instance
     * @param hobbyName
     */
    private void createHobbyInstanceFromNull(String hobbyName) {
        hobby = new Hobby(hobbyName);
    }

    /**
     * Reads all information from user input and populates the hobby with the correct values
     */
    private void setHobby(){
        hobby.setDifficultyLevel(spinnerDifficultyLevel.getSelectedItem().toString());
        hobby.setTimeFrom(spinnerTimeFrom.getSelectedItem().toString());
        hobby.setTimeTo(spinnerTimeTo.getSelectedItem().toString());
        readDayOfWeek();
    }

    /*
    * Toast
    */

    //public void onItemClicked()

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
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

