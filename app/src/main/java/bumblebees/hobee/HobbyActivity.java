package bumblebees.hobee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

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
        showSports = (ImageButton)findViewById(R.id.hobby1);

        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this way the stack of activities is cleared and if you resume the app from closing it on homepage
                // it will resume at homepage not here
                Intent homeIntent = new Intent(HobbyActivity.this, HomeActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                HobbyActivity.this.startActivity(homeIntent);
                finish();
            }
        });

       /* // for demo: Only sports can be selected!
        showSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(HobbyActivity.this, SportsHobbyActivity.class);
                HobbyActivity.this.startActivity(profileIntent);

            }
        });
        */
    }



}
