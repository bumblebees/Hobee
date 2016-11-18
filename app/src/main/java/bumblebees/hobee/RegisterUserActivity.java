package bumblebees.hobee;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import bumblebees.hobee.utilities.SessionManager;
import bumblebees.hobee.utilities.SocketIO;
import bumblebees.hobee.utilities.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterUserActivity extends AppCompatActivity {

    String userGender;
    String userBirthday;

    EditText firstName;
    EditText lastName;
    TextView birthday;
    EditText email;
    RadioGroup gender;
    RadioButton genderMale;
    RadioButton genderFemale;
    RadioButton selectedGender;
    EditText bio;
    ImageView pic;
    ImageButton submitBtn;
    Button setBirthdayBtn;
    Bundle userData;

    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();

        userData = intent.getBundleExtra("userData");

        try {
            userGender = userData.getString("gender");
        } catch (NullPointerException e) {
            userGender = null;
        }
        try {
            userBirthday = userData.getString("birthday");
        } catch (NullPointerException e) {
            userBirthday = null;
        }


        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        birthday = (TextView) findViewById(R.id.birthday);
        email = (EditText) findViewById(R.id.email);
        gender = (RadioGroup) findViewById(R.id.gender);
        genderMale = (RadioButton) findViewById(R.id.radioMale);
        genderFemale = (RadioButton) findViewById(R.id.radioFemale);
        bio = (EditText) findViewById(R.id.info);
        pic = (ImageView) findViewById(R.id.pic);
        submitBtn = (ImageButton) findViewById(R.id.submitBtn);
        setBirthdayBtn = (Button) findViewById(R.id.setBirthdayBtn);


        // Set fields with extracted user data
        firstName.setText(userData.getString("firstName"));
        lastName.setText(userData.getString("lastName"));
        email.setText(userData.getString("email"));
        if (userGender.equals("male")) {
            genderMale.setChecked(true);
        } else if (userGender.equals("female")) {
            genderFemale.setChecked(true);
        }
        if (userBirthday != null) {
            try {
                Date date = new SimpleDateFormat("MM/dd/yyyy").parse(userBirthday);
                String formattedDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
                birthday.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // submit button does magic?
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject userJSON = createJSON();
                // Set shared preferences
                session.setPreferences(userData.getString("loginId"), userData.getString("origin"));
                // Set user instance
                User.getInstance().setUser(userJSON);
                // Save user in database
                SocketIO.getInstance().register(userJSON, RegisterUserActivity.this);
            }
        });

        setBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogfragment = new DatePickerDialogFragment();
                dialogfragment.show(getFragmentManager(), "dialog");

            }
        });
    }

    /**
     * Collects data from the screen (modified or not) and put it into JSON object
     *
     * @return JSON object
     */
    public JSONObject createJSON() {
        JSONObject object = new JSONObject();
        selectedGender = (RadioButton) findViewById(gender.getCheckedRadioButtonId());
        try {
            object.put("loginId", userData.getString("loginId"));
            object.put("origin", userData.get("origin"));
            object.put("firstName", firstName.getText().toString());
            object.put("lastName", lastName.getText().toString());
            object.put("birthday", birthday.getText().toString());
            object.put("email", email.getText().toString());
            object.put("gender", selectedGender.getText().toString());
            object.put("bio", bio.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * Class to create a dialog for picking a date. The AlertDialog theme is deprecated but it still looks much more
     * comfortable than the default one.
     */
    public static class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = 1990;
            int month = 0;
            int day = 1;

            return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            TextView birthday = (TextView) getActivity().findViewById(R.id.birthday);
            birthday.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        }
    }


}
