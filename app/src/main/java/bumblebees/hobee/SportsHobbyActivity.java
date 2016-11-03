package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;

import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Class to represent a selected hobby category's specific hobbies
 * TODO: Create superclass for all specific hobby activities
 * Created by amandahoffstrom on 2016-11-03.
 */

public class SportsHobbyActivity extends AppCompatActivity {

    Button backToHobbies;

    Socket socket;

    CheckBox sprt1;
    CheckBox sprt2;
    CheckBox sprt3;
    CheckBox sprt4;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportshobby);

        userID = this.getIntent().getStringExtra("user_ID");

        backToHobbies = (Button) findViewById(R.id.button);

        sprt1 = (CheckBox) findViewById(R.id.sport1);
        sprt2 = (CheckBox) findViewById(R.id.sport2);
        sprt3 = (CheckBox) findViewById(R.id.sport3);
        sprt4 = (CheckBox) findViewById(R.id.sport4);

        // When "Back" button is selected, connection with socket is established and data is sent through JSON to server
        backToHobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    socket = IO.socket("http://129.16.155.22:3001");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        socket.emit("update_hobbies", createJsonObject());
                        socket.disconnect();
                    }

                });
                socket.connect();

                Intent hobbyIntent = new Intent(SportsHobbyActivity.this, HobbyActivity.class);
              // Not sure if we need to send it back to HobbyActivity again? Will it remember it anyway?
                hobbyIntent.putExtra("user_ID", userID);
                SportsHobbyActivity.this.startActivity(hobbyIntent);
            }
        });

    }

    public JSONObject createJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", userID);

            JSONArray jsonArray = new JSONArray();

            if (sprt1.isChecked()) {
                jsonArray.put(sprt1.getText().toString());
            }
            if (sprt2.isChecked()) {
                jsonArray.put(sprt2.getText().toString());
            }
            if (sprt3.isChecked()) {
                jsonArray.put(sprt3.getText().toString());
            }
            if (sprt4.isChecked()) {
                jsonArray.put(sprt4.getText().toString());
            }
            jsonObject.put("hobbies", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
