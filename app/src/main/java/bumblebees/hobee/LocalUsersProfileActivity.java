package bumblebees.hobee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import bumblebees.hobee.utilities.Profile;
import com.squareup.picasso.Picasso;


public class LocalUsersProfileActivity extends AppCompatActivity {

    TextView userName;
    TextView userEmail;
    TextView userGender;
    TextView userAge;
    ImageView userImage;
    TextView hobbiesList;
    TextView userBio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_user_profile);

        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userGender = (TextView) findViewById(R.id.userGender);
        userAge = (TextView) findViewById(R.id.userAge);
        userImage = (ImageView) findViewById(R.id.userImage);
        hobbiesList = (TextView) findViewById(R.id.listhobbies);
        userBio = (TextView) findViewById(R.id.userBio);

        userName.setText(Profile.getInstance().getFirstName() + " " + Profile.getInstance().getLastName());
        userEmail.setText(Profile.getInstance().getEmail());
        userGender.setText(Profile.getInstance().getGender());
        userAge.setText(Integer.toString(Profile.getInstance().getAge()));
        userBio.setText(Profile.getInstance().getBio());
        Picasso.with(this).load(Profile.getInstance().getPicUrl()).into(userImage);

    }

}