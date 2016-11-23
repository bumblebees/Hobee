package bumblebees.hobee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import bumblebees.hobee.objects.User;


public class UserProfileActivity extends AppCompatActivity {
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

        user = getIntent().getParcelableExtra("User");

        System.out.println(user);
        
        userName.setText(user.getFirstName() + " " + user.getLastName());
        userAge.setText(""+ user.getAge());
        userGender.setText(user.getGender());
        userDateSince.setText(user.getDateCreated().toString());
        userBiography.setText(user.getBio());
        
        
        //// TODO: 2016-11-23 Add the hobbies and add the userImage 



    }
}
