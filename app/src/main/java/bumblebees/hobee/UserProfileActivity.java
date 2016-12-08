package bumblebees.hobee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bumblebees.hobee.objects.User;
import bumblebees.hobee.utilities.CropSquareTransformation;
import bumblebees.hobee.utilities.Profile;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


public class UserProfileActivity extends AppCompatActivity {
    private Gson gson = new Gson();
    private ImageView userImage, userGender;
    private TextView userName, userAge, userDateSince, userBiography, globalRank, hostRank, noShows;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String extra = getIntent().getStringExtra("User");

        userName = (TextView) findViewById(R.id.userName);

        userAge = (TextView) findViewById(R.id.userAge);
        userGender = (ImageView) findViewById(R.id.userGender);
        //userDateSince = (TextView) findViewById(R.id.userDateSince);
        userBiography = (TextView) findViewById(R.id.userBiography);
        userImage = (ImageView) findViewById(R.id.userImage);
        globalRank = (TextView) findViewById(R.id.globalRank);
        hostRank = (TextView) findViewById(R.id.hostRank);
        noShows = (TextView) findViewById(R.id.noShows);


        // If user wants to see his own profile

        if (extra==null) {
            try {
                userName.setText(Profile.getInstance().getFirstName() + " " + Profile.getInstance().getLastName());
                userAge.setText("" + Profile.getInstance().getAge());
                if (Profile.getInstance().getGender().equals("male")) {
                    userGender.setImageResource(R.drawable.male);
                } else {
                    userGender.setImageResource(R.drawable.female);
                }
                //userDateSince.setText("Member since " + Profile.getInstance().userSince());
                globalRank.setText(reputationToRank(Profile.getInstance().getGlobalRep()));
                hostRank.setText(reputationToRank(Profile.getInstance().getHostlRep()));
                noShows.setText(Integer.toString(Profile.getInstance().getNoShows()));
                userBiography.setText(Profile.getInstance().getBio());
                Picasso.with(this).load(Profile.getInstance().getPicUrl()).transform(new CropSquareTransformation()).into(userImage);
            } catch (NullPointerException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error seeing profile", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }

        }


        else {
            user = gson.fromJson(getIntent().getStringExtra("User"), User.class);
            try {
                userName.setText(user.getFirstName() + " " + user.getLastName());
                userAge.setText("" + user.getAge());
                if (user.getGender().equals("male")) {
                    userGender.setImageResource(R.drawable.male);
                } else {
                    userGender.setImageResource(R.drawable.female);
                }
                //userDateSince.setText("Member since " + Profile.getInstance().userSince());
                globalRank.setText(reputationToRank(user.getRank().getGlobalRep()));
                hostRank.setText(reputationToRank(user.getRank().getHostRep()));
                noShows.setText(Integer.toString(user.getRank().getNoShows()));
                userBiography.setText(user.getBio());
                Picasso.with(this).load(user.getPicUrl()).transform(new CropSquareTransformation()).into(userImage);
            } catch (NullPointerException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error seeing profile", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }


        }
    }

    private String reputationToRank(int reputation){
        if (reputation < -7000){return "-3";}
        if (reputation > -7000 && reputation <= -2500){return "-2";}
        if (reputation > -2500 && reputation <= -1000){return "-1";}
        if (reputation > -1000 && reputation <= 1000){return "0";}
        if (reputation > 1000 && reputation <= 2500){return "+1";}
        if (reputation > 2500 && reputation <= 7000){return "+2";}
        if (reputation > 7000){return "+3";}
        else {return "err";}
    }


}



