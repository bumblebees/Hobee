package bumblebees.hobee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bumblebees.hobee.hobbycategories.HobbiesChoiceActivity;
import bumblebees.hobee.objects.User;
import bumblebees.hobee.utilities.CropSquareTransformation;
import bumblebees.hobee.utilities.Profile;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


public class UserProfileActivity extends AppCompatActivity {
    private Gson gson = new Gson();
    private ImageView userImage, userGender, editProfile, editHobbies;
    private TextView userName, userAge, userBiography, globalRank, hostRank, noShows;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String extra = getIntent().getStringExtra("User");

        userName = (TextView) findViewById(R.id.userName);

        userAge = (TextView) findViewById(R.id.userAge);
        userGender = (ImageView) findViewById(R.id.userGender);
        userBiography = (TextView) findViewById(R.id.userBiography);
        userImage = (ImageView) findViewById(R.id.userImage);
        globalRank = (TextView) findViewById(R.id.globalRank);
        hostRank = (TextView) findViewById(R.id.hostRank);
        noShows = (TextView) findViewById(R.id.noShows);
        editProfile = (ImageView) findViewById(R.id.editProfileBtn);
        editHobbies = (ImageView) findViewById(R.id.editHobbiesBtn);


        // If user wants to see his own profile

        if (extra == null) {
            try {
                userName.setText(Profile.getInstance().getFirstName() + " " + Profile.getInstance().getLastName());
                userAge.setText("" + Profile.getInstance().getAge());
                if (Profile.getInstance().getGender().equals("gender_male")) {
                    userGender.setImageResource(R.drawable.gender_male);
                } else {
                    userGender.setImageResource(R.drawable.gender_female);
                }
                globalRank.setText(reputationToRank(Profile.getInstance().getGlobalRep()));
                hostRank.setText(reputationToRank(Profile.getInstance().getHostlRep()));
                noShows.setText(Integer.toString(Profile.getInstance().getNoShows()));
                userBiography.setText(Profile.getInstance().getBio());
                Picasso.with(this).load(Profile.getInstance().getPicUrl()).transform(new CropSquareTransformation()).into(userImage);
            } catch (NullPointerException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error seeing profile_img", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }

        } else if (extra.equals("error")) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error seeing profile_img", Toast.LENGTH_LONG);
            toast.show();
            finish();

            // View other profile

        } else {
            editHobbies.setVisibility(View.INVISIBLE);
            editProfile.setVisibility(View.INVISIBLE);
            user = gson.fromJson(getIntent().getStringExtra("User"), User.class);
            try {
                userName.setText(user.getFirstName() + " " + user.getLastName());
                userAge.setText("" + user.getAge());
                if (user.getGender().equals("gender_male")) {
                    userGender.setImageResource(R.drawable.gender_male);
                } else {
                    userGender.setImageResource(R.drawable.gender_female);
                }
                globalRank.setText(reputationToRank(user.getRank().getGlobalRep()));
                hostRank.setText(reputationToRank(user.getRank().getHostRep()));
                noShows.setText(Integer.toString(user.getRank().getNoShows()));
                userBiography.setText(user.getBio());
                Picasso.with(this).load(user.getPicUrl()).transform(new CropSquareTransformation()).into(userImage);
            } catch (NullPointerException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error seeing profile_img", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }


        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfileIntent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                editProfileIntent.putExtra("Source", "UserProfileActivity");
                startActivity(editProfileIntent);
            }
        });

        editHobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editHobbiesintent = new Intent(getApplicationContext(), HobbiesChoiceActivity.class);
                editHobbiesintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(editHobbiesintent);
            }
        });
    }

    private String reputationToRank(int reputation) {
        if (reputation < -7000) {
            return "-3";
        }
        if (reputation > -7000 && reputation <= -2500) {
            return "-2";
        }
        if (reputation > -2500 && reputation <= -1000) {
            return "-1";
        }
        if (reputation > -1000 && reputation <= 1000) {
            return "0";
        }
        if (reputation > 1000 && reputation <= 2500) {
            return "+1";
        }
        if (reputation > 2500 && reputation <= 7000) {
            return "+2";
        }
        if (reputation > 7000) {
            return "+3";
        } else {
            return "err";
        }
    }

    @Override
    public void onBackPressed() {
        Intent backPressIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
        startActivity(backPressIntent);

    }


}



