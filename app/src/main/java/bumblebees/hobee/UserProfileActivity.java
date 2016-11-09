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

import org.json.JSONArray;
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
    private TextView hobbiesList;
    private TextView profileBio;
    JSONObject profileData;
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //TODO: rename variables
        fbName   = (TextView) findViewById(R.id.userName);
        fbEmail  =  (TextView) findViewById(R.id.userEmail);
        fbGender = (TextView) findViewById(R.id.userGender);
        fbAge    = (TextView) findViewById(R.id.userAge);
        fbImage  = (ImageView) findViewById(R.id.fbImage);
        hobbiesList = (TextView) findViewById(R.id.listhobbies);
        profileBio = (TextView) findViewById(R.id.profileBio);


        final SessionManager session = new SessionManager(getApplicationContext());


        try {
            socket = IO.socket("http://129.16.155.22:3001");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //Retrieve user data from server
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("get_user", session.getId(), new Ack() {
                    @Override
                    public void call(Object... objects) {
                        profileData = (JSONObject) objects[0];
                        //Add data to fields
                        //updating the UI has to happen on the main UI thread, otherwise it throws an exception
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    fbName.setText(profileData.getString("firstName")+" "+profileData.getString("lastName"));
                                    fbEmail.setText(profileData.getString("email"));
                                    fbGender.setText(profileData.getString("gender"));
                                    //TODO: receive this as an UNIX date and calculate the age instead of the birthday
                                    fbAge.setText(profileData.getString("birthday"));
                                    profileBio.setText(profileData.getString("bio"));
                                    JSONArray arr = profileData.getJSONArray("hobbies");
                                    String hobbies="";
                                    for (int i=0; i<arr.length(); i++){
                                        hobbies+=arr.get(i)+" ";
                                    }
                                    hobbiesList.setText(hobbies);


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