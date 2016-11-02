package bumblebees.hobee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserProfileActivity extends AppCompatActivity {
    private TextView fbName;
    private TextView fbEmail;
    private TextView fbGender;
    private TextView fbAge;
    private ImageView fbImage;
    JSONObject profileData;
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: rename variables
        fbName   = (TextView) findViewById(R.id.userName);
        fbEmail  =  (TextView) findViewById(R.id.userEmail);
        fbGender = (TextView) findViewById(R.id.userGender);
        fbAge    = (TextView) findViewById(R.id.userAge);
        fbImage  = (ImageView) findViewById(R.id.fbImage);


        Intent intent = this.getIntent();
        final String userID = intent.getExtras().getString("user_ID");


        try {
            socket = IO.socket("http://129.16.155.22:3001");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //Retrieve user data from server
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("get_user", userID, new Ack() {
                    @Override
                    public void call(Object... objects) {
                        profileData = (JSONObject) objects[0];
                        //Add data to fields
                        //updating the UI has to happen on the main UI thread, otherwise it throws an exception
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    fbName.setText(profileData.getString("username"));
                                    fbEmail.setText(profileData.getString("email"));
                                    fbGender.setText(profileData.getString("gender"));
                                    fbAge.setText(profileData.getString("birthday"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });



                        socket.disconnect();

                    }
                });

            }
        });
        socket.connect();


    }





}