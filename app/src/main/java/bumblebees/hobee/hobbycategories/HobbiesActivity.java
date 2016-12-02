package bumblebees.hobee.hobbycategories;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView.OnItemSelectedListener;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Hobby;

import static android.R.id.list;

public class HobbiesActivity extends AppCompatActivity implements OnItemSelectedListener {

    // Checkbox list
    List<String> checkList = new ArrayList<String>();

    Hobby hobby;
    TextView textView;
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

        // Spinner element
        Spinner spinnerDifficultyLevel = (Spinner) findViewById(R.id.spinner_difficulty_level);
        //spinnerDifficultyLevel.setPrompt("Title");

        Spinner spinnerTimeFrom = (Spinner) findViewById(R.id.spinner_time_from);
        Spinner spinnerTimeTo = (Spinner) findViewById(R.id.spinner_time_to);


        // Spinner click listener
        spinnerDifficultyLevel.setOnItemSelectedListener(this);
        spinnerTimeFrom.setOnItemSelectedListener(this);
        spinnerTimeTo.setOnItemSelectedListener(this);


        // Spinner Drop down elements
        List<String> difficultyList = new ArrayList<String>();
        difficultyList.add("Beginner");
        difficultyList.add("Intermediate");
        difficultyList.add("Expert");

        List<String> timeListTo = new ArrayList<String>();
        timeListTo.add("8:00");
        timeListTo.add("10:00");
        timeListTo.add("12:00");
        timeListTo.add("14:00");
        timeListTo.add("16:00");
        timeListTo.add("18:00");
        timeListTo.add("20:00");
        timeListTo.add("22:00");
        timeListTo.add("24:00");

        List<String> timeListFrom = new ArrayList<String>();
        timeListFrom.add("8:00");
        timeListFrom.add("10:00");
        timeListFrom.add("12:00");
        timeListFrom.add("14:00");
        timeListFrom.add("16:00");
        timeListFrom.add("18:00");
        timeListFrom.add("20:00");
        timeListFrom.add("22:00");
        timeListFrom.add("24:00");

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

        // Checkbox element
        checkBoxMonday = (CheckBox)findViewById(R.id.checkBox_monday);
        checkBoxTuesday = (CheckBox)findViewById(R.id.checkBox_tuesday);
        checkBoxWednesday = (CheckBox)findViewById(R.id.checkBox_wednesday);
        checkBoxThursday = (CheckBox)findViewById(R.id.checkBox_thursday);
        checkBoxFriday = (CheckBox)findViewById(R.id.checkBox_friday);
        checkBoxSaturday = (CheckBox)findViewById(R.id.checkBox_saturday);
        checkBoxSunday = (CheckBox)findViewById(R.id.checkBox_sunday);
    }

    public void onCheckboxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkBox_monday:
                checkList.add(checkBoxMonday.getTag().toString());
                break;

            case R.id.checkBox_tuesday:
                checkList.add(checkBoxTuesday.getTag().toString());
                break;

            case R.id.checkBox_wednesday:
                checkList.add(checkBoxWednesday.getTag().toString());
                break;

            case R.id.checkBox_thursday:
                checkList.add(checkBoxThursday.getTag().toString());
                break;

            case R.id.checkBox_friday:
                checkList.add(checkBoxFriday.getTag().toString());
                break;

            case R.id.checkBox_saturday:
                checkList.add(checkBoxSaturday.getTag().toString());
                break;

            case R.id.checkBox_sunday:
                checkList.add(checkBoxSunday.getTag().toString());
                break;
        }
    }


    public Hobby createHobbyInstance (String hobbyName) {
        Hobby hobby = new Hobby(hobbyName);
        return hobby;
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
                for (String str : checkList) {
                    //here goes the saving to JSON
                }
            }
        });
    }
}

