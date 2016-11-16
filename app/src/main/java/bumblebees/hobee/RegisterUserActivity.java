package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import bumblebees.hobee.utilities.SessionManager;
import bumblebees.hobee.utilities.SocketIO;
import bumblebees.hobee.utilities.User;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUserActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText birthdate;
    EditText email;
    RadioGroup gender;
    RadioButton selectedGender;
    EditText info;
    ImageView pic;
    Button selectPicBtn;
    ImageButton submitBtn;
    Socket socket;
    Bundle userData;

    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();
        userData = intent.getBundleExtra("userData");

        // set options for dropdown menu
        ArrayAdapter<String> genders = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"male", "female"});

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);

        //TODO: turn this into a DatePicker, probably
        birthdate = (EditText) findViewById(R.id.birthdate);
        email = (EditText) findViewById(R.id.email);
        gender = (RadioGroup) findViewById(R.id.gender);
        //gender.setAdapter(genders);
        info = (EditText) findViewById(R.id.info);
        pic = (ImageView) findViewById(R.id.pic);
        selectPicBtn = (Button) findViewById(R.id.selectPicBtn);
        submitBtn = (ImageButton) findViewById(R.id.submitBtn);


        // Set fields with extracted user data
        firstName.setText(userData.getString("firstName"));
        lastName.setText(userData.getString("lastName"));
        birthdate.setText(userData.getString("birthday"));
        email.setText(userData.getString("email"));

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
    }

    public JSONObject createJSON() {
        JSONObject object = new JSONObject();
        selectedGender = (RadioButton) findViewById(gender.getCheckedRadioButtonId());
        try {
            object.put("loginId", userData.getString("loginId"));
            object.put("origin", userData.get("origin"));
            object.put("firstName", firstName.getText().toString());
            object.put("lastName", lastName.getText().toString());
            object.put("birthday", birthdate.getText().toString());
            object.put("email", email.getText().toString());
            object.put("gender", selectedGender.getText().toString());
            object.put("bio", info.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
