package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;

import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by amandahoffstrom on 2016-11-03.
 */

public class SportsHobbyActivity extends AppCompatActivity {

    Button backToHobbies;
    ImageButton button;

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

        sprt4.isActivated()

        backToHobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(SportsHobbyActivity.this, HobbyActivity.class);
                profileIntent.putExtra("user_ID", userID);
                SportsHobbyActivity.this.startActivity(profileIntent);
            }
        });

        // When "Back" button is selected, connection with socket is established and data is sent through JSON to server
        button.setOnClickListener(new View.OnClickListener() {
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
                        socket.emit("test", createJsonObject());
                        socket.disconnect();
                    }

                });
                socket.connect();

                Intent hobbyIntent = new Intent(SportsHobbyActivity.this, HobbyActivity.class);
             //   hobbyIntent.putExtra("user_ID", userData.getString("id"));
                SportsHobbyActivity.this.startActivity(hobbyIntent);


            }
        });

    }

    public JSONObject createJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userID", userID);
            jsonObject.put("hobby1", sprt1.getText().toString());
            jsonObject.put("hobby2", sprt2.getText().toString());
            jsonObject.put("hobby3", sprt3.getText().toString());
            jsonObject.put("hobby4", sprt4.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
