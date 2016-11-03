package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class HobbyActivity extends AppCompatActivity {

    ImageButton showProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbieschoice);

        final String userID = this.getIntent().getStringExtra("user_ID");

        showProfile = (ImageButton)findViewById(R.id.showProfile);

        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(HobbyActivity.this, HomeActivity.class);
                homeIntent.putExtra("user_ID", userID);
                HobbyActivity.this.startActivity(homeIntent);
            }
        });
    }
}
