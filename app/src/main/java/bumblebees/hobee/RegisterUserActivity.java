package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class RegisterUserActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText birthdate;
    EditText email;
    Spinner gender;
    EditText info;
    ImageView pic;
    Button selectPicBtn;
    ImageButton submitBtn;
    Socket socket;
    Bundle userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Intent intent = getIntent();
        userData = intent.getBundleExtra("userData");

        // set options for dropdown menu
        ArrayAdapter<String> genders = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"male", "female"});

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);

        //TODO: turn this into a DatePicker, probably
        birthdate = (EditText) findViewById(R.id.birthdate);
        email = (EditText) findViewById(R.id.email);
        gender = (Spinner) findViewById(R.id.gender);
        gender.setAdapter(genders);
        info = (EditText) findViewById(R.id.info);
        pic = (ImageView) findViewById(R.id.pic);
        selectPicBtn = (Button) findViewById(R.id.selectPicBtn);
        submitBtn = (ImageButton) findViewById(R.id.submitBtn);


        // set fields that were received from the login
        if (getIntent().getStringExtra("login").equals("google")) {
            firstName.setText(userData.getString("first_name"));
            lastName.setText(userData.getString("last_name"));
            email.setText(userData.getString("email"));
        }
        else {
            GraphRequest request = GraphRequest.newMeRequest(LoginActivity.facebookToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        firstName.setText(object.getString("first_name"));
                        lastName.setText(object.getString("last_name"));
                        birthdate.setText(object.getString("birthday"));
                        email.setText(object.getString("email"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,gender,birthday,email");
            request.setParameters(parameters);
            request.executeAsync();
        }

        // submit button does magic?
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SocketIO.register(createJSON(), RegisterUserActivity.this);
            }
        });
    }

    public JSONObject createJSON() {
        JSONObject object = new JSONObject();
        JSONObject picture = new JSONObject();
        // put pic in picture object (to be nested)
        try {
            picture.put("profile", "lets say it's a pic");
            picture.put("photo1", "lets say it's a pic");
            picture.put("photo2", "lets say it's a pic");
            picture.put("photo3", "lets say it's a pic");
            picture.put("photo4", "lets say no pic");
            picture.put("photo5", "lets say no pic");
        } catch (JSONException e){
            e.printStackTrace();
        }
        // put everything in final object
        try {
            if (getIntent().getStringExtra("login").equals("google")) {
                object.put("loginID", userData.getString("id"));
            }
            else{
                object.put("loginID", LoginActivity.facebookToken.getUserId());
            }
            object.put("firstName", firstName.getText().toString());
            object.put("lastName", lastName.getText().toString());
            object.put("birthday", birthdate.getText().toString());
            object.put("email", email.getText().toString());
            object.put("gender", gender.getSelectedItem().toString());
            object.put("bio", info.getText().toString());
            object.put("picture", picture);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
