package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to represent the "head" hobby page, with categories
 */
public class HobbyActivity extends AppCompatActivity {

    ImageButton showProfile;
    ImageButton showSports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbieschoice);

        final String userID = this.getIntent().getStringExtra("user_ID");

        showProfile = (ImageButton)findViewById(R.id.showProfile);
        showSports = (ImageButton)findViewById(R.id.hobby5);

        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(HobbyActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("user_ID", userID);
                HobbyActivity.this.startActivity(profileIntent);
            }
        });

        // for demo: Only sports can be selected!
        showSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(HobbyActivity.this, SportsHobbyActivity.class);
                profileIntent.putExtra("user_ID", userID);
                HobbyActivity.this.startActivity(profileIntent);
            }
        });
    }



}
