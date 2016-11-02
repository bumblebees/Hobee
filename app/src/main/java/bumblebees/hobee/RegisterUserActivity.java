package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class RegisterUserActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText username;
    EditText birthdate;
    EditText email;
    Spinner gender;
    EditText info;
    ImageView pic;
    Button selectPicBtn;
    Button submitBtn;
    Socket socket;
    Bundle userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Intent intent = getIntent();
        userData = intent.getBundleExtra("userData");


        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        username = (EditText) findViewById(R.id.username);
        birthdate = (EditText) findViewById(R.id.birthdate);
        email = (EditText) findViewById(R.id.email);
        gender = (Spinner) findViewById(R.id.gender);
        info = (EditText) findViewById(R.id.info);
        pic = (ImageView) findViewById(R.id.pic);
        selectPicBtn = (Button) findViewById(R.id.selectPicBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);


        // set fields that were received from the login
        firstName.setText(userData.getString("first_name"));
        lastName.setText(userData.getString("last_name"));
        email.setText(userData.getString("email"));
        username.setText(userData.getString("username"));


        // set options for dropdown menu
        ArrayAdapter<String> genders = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"male", "female"});
        gender.setAdapter(genders);
        // set image on click - totally useless
        selectPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pic.setImageResource(R.drawable.sample);
            }
        });
        // submit button does magic?
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    socket = IO.socket("http://129.16.155.22:3001");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        socket.emit("test", createJSON());
                        socket.disconnect();
                    }

                });
                socket.connect();
                //TODO: it would probably be a better idea to use a callback here
                //TODO: also, this should lead to the main screen eventually, not the profile
                Intent profileIntent = new Intent(RegisterUserActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("user_ID", userData.getString("id"));
                RegisterUserActivity.this.startActivity(profileIntent);
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
            object.put("loginID", userData.getString("id"));
            object.put("firstName", firstName.getText().toString());
            object.put("lastName", lastName.getText().toString());
            object.put("username", username.getText().toString());
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
