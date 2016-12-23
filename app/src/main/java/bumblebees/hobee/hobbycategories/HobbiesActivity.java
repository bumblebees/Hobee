package bumblebees.hobee.hobbycategories;

import android.content.Intent;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.Gson;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.User;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SessionManager;
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
    ToggleButton checkBoxMonday;
    ToggleButton checkBoxTuesday;
    ToggleButton checkBoxWednesday;
    ToggleButton checkBoxThursday;
    ToggleButton checkBoxFriday;
    ToggleButton checkBoxSaturday;
    ToggleButton checkBoxSunday;

    Button submitBtn;
    private Gson gson = new Gson();

    //TODO: change the fields if the hobby already has values in the profile_img

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
                SessionManager sessionManager = new SessionManager(HobbiesActivity.this);
                sessionManager.saveUser(Profile.getInstance().getUser());
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
        checkBoxMonday = (ToggleButton)findViewById(R.id.checkBox_monday);
        checkBoxTuesday = (ToggleButton)findViewById(R.id.checkBox_tuesday);
        checkBoxWednesday = (ToggleButton)findViewById(R.id.checkBox_wednesday);
        checkBoxThursday = (ToggleButton)findViewById(R.id.checkBox_thursday);
        checkBoxFriday = (ToggleButton)findViewById(R.id.checkBox_friday);
        checkBoxSaturday = (ToggleButton)findViewById(R.id.checkBox_saturday);
        checkBoxSunday = (ToggleButton)findViewById(R.id.checkBox_sunday);

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
            hobby.setDatePreference(checkBoxMonday.getText().toString());
        }
        if (checkBoxTuesday.isChecked()){
            hobby.setDatePreference(checkBoxTuesday.getText().toString());
        }
        if (checkBoxWednesday.isChecked()){
            hobby.setDatePreference(checkBoxWednesday.getText().toString());
        }
        if (checkBoxThursday.isChecked()){
            hobby.setDatePreference(checkBoxThursday.getText().toString());
        }
        if (checkBoxFriday.isChecked()){
            hobby.setDatePreference(checkBoxFriday.getText().toString());
        }
        if (checkBoxSaturday.isChecked()){
            hobby.setDatePreference(checkBoxSaturday.getText().toString());
        }
        if (checkBoxSunday.isChecked()){
            hobby.setDatePreference(checkBoxSunday.getText().toString());
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
          //  System.out.println(">>>>> getHobbyFields "  );
        //System.out.println(hobby.getDifficultyLevel().toString());
            spinnerDifficultyLevel.setSelection(setSpinnerDifficultyLevel(hobby.getDifficultyLevel()));
            spinnerTimeFrom.setSelection(setSpinnerTime(hobby.getTimeFrom()));
            spinnerTimeTo.setSelection(setSpinnerTime(hobby.getTimeTo()));
    }

    /**
     * TODO: Write description
     * @param hobbyName
     */
    private void createHobby(String hobbyName){
        //if (hobbyExists(hobbyName)) {
        //    System.out.println(">>>>>>>>>>>>>>>>>>>>>>HOBBY EXISTS!!");
       //     getHobbyFields(hobby);
      //  } else {
            createHobbyInstanceFromNull(hobbyName);
       // }
    }

    private boolean hobbyExists(String hobbyName){
        for (Hobby hobby : Profile.getInstance().getUser().getHobbies()){
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>HOBB EXISTS!" + hobby.getName());
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
       // System.out.println(hobby.getDifficultyLevel().toString());
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

