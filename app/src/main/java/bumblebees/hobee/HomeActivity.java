package bumblebees.hobee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class HomeActivity extends AppCompatActivity {

    Button btnProfile;
    Button btnLogout;
    Button btnNewEvent;
    SharedPreferences preferences;
    String origin;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final SessionManager session = new SessionManager(getApplicationContext());

        // for testing
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(session.getId() + " " + session.getOrigin());

        btnNewEvent = (Button) findViewById(R.id.btnCreateEvent);

        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newEventIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                HomeActivity.this.startActivity(newEventIntent);
            }
        });

        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                HomeActivity.this.startActivity(profileIntent);
            }
        });

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (session.getOrigin().equals("facebook")){
                    LoginManager.getInstance().logOut();
                }
                else {

                }

                // clear user from preferences
                session.logoutUser();

                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                HomeActivity.this.startActivity(logoutIntent);

            }
        });





    }
}
