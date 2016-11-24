package bumblebees.hobee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import bumblebees.hobee.objects.User;


public class UserProfileActivity extends AppCompatActivity {
    private Gson gson= new Gson();
    private ImageView userImage;
    private TextView userName,userAge,userGender,userDateSince,userBiography;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userName = (TextView) findViewById(R.id.userName);
        userAge = (TextView) findViewById(R.id.userAge);
        userGender = (TextView) findViewById(R.id.userGender);
        userDateSince = (TextView) findViewById(R.id.userDateSince);
        userBiography = (TextView) findViewById(R.id.userBiography);

        user = gson.fromJson(getIntent().getStringExtra("User"),User.class);

        try {
            userName.setText(user.getFirstName() + " " + user.getLastName());
            userAge.setText("" + user.getAge());
            userGender.setText(user.getGender());
            userDateSince.setText("Member since " + user.userSince());
            userBiography.setText(user.getBio());
        } catch(NullPointerException e){
            Log.d("Error creating user", e.toString());
            Toast toast = Toast.makeText(getApplicationContext(),"Error seeing profile",Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        
        //// TODO: 2016-11-23 Add the hobbies and add the userImage 



    }
}
