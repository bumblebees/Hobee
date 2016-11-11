package bumblebees.hobee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import bumblebees.hobee.utilities.SessionManager;


public class UserProfileActivity extends AppCompatActivity {
    private TextView fbName;
    private TextView fbEmail;
    private TextView fbGender;
    private TextView fbAge;
    private ImageView fbImage;
    private TextView hobbiesList;
    private TextView profileBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //TODO: rename variables
        fbName   = (TextView) findViewById(R.id.userName);
        fbEmail  =  (TextView) findViewById(R.id.userEmail);
        fbGender = (TextView) findViewById(R.id.userGender);
        fbAge    = (TextView) findViewById(R.id.userAge);
        fbImage  = (ImageView) findViewById(R.id.fbImage);
        hobbiesList = (TextView) findViewById(R.id.listhobbies);
        profileBio = (TextView) findViewById(R.id.profileBio);


        SessionManager session = new SessionManager(getApplicationContext());

        fbName.setText(session.getfirstName() + " " + session.getLastName());
        fbEmail.setText(session.getEmail());
        fbGender.setText(session.getGender());
        //TODO: receive this as an UNIX date and calculate the age instead of the birthday
        fbAge.setText(session.getBirthday());
        profileBio.setText(session.getBio());

    }





}