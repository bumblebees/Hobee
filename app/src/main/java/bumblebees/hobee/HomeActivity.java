package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //TODO: Delete this after testing
        final String userID = this.getIntent().getStringExtra("user_ID");
        TextView text = (TextView) findViewById(R.id.textView);

        Button btnProfile = (Button) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("user_ID", userID);
                HomeActivity.this.startActivity(profileIntent);
            }
        });





    }
}
