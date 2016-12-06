package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.Profile;
import bumblebees.hobee.utilities.SocketIO;
import bumblebees.hobee.utilities.UserRankAdapter;

public class RankUserActivity extends AppCompatActivity {
    ListView usersList;
    ArrayList<String> usersID;
    ArrayList<String> users;
    private Button buttonDone;
    private UserRankAdapter adapter;
    private Event event;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_user);
        buttonDone = (Button) findViewById(R.id.bttnDone);
        usersList = (ListView) findViewById(R.id.usersList);
        users = getIntent().getStringArrayListExtra("userList");
        event = gson.fromJson(getIntent().getStringExtra("event"),Event.class);

        Boolean isHost = false;
        if(event.isCurrentUserHost()) isHost = true;

        usersList.setAdapter(new UserRankAdapter(this,users, event));
        adapter = (UserRankAdapter) usersList.getAdapter();

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRanks();
            }
        });
    }


    private void sendRanks()  {
        String[][] ranks = adapter.getRanks();
        Boolean hasranked = false;
        JSONObject rankingMessage = new JSONObject();
        JSONArray parent = new JSONArray();

        //Create two dimmentional array with JSON objects
            try {
                for (int j = 0; j < users.size(); j++) {
                    JSONArray child = new JSONArray();
                    for (int i = 0; i < 3; i++) {
                        if (!hasranked)
                            if (i > 0)
                                if (Integer.parseInt(ranks[j][i]) != 0)
                                    hasranked = true;
                        child.put(i, ranks[j][i]);
                    }
                    parent.put(j, child);
                }


                rankingMessage.put("hasRanked", hasranked);
                rankingMessage.put("userID", Profile.getInstance().getUserID());
                rankingMessage.put("eventID", event.getEventID());
                rankingMessage.put("hostRep", ranks[0][1]);
                rankingMessage.put("userReps", parent);
            }
            catch (JSONException e){
                Log.d("Json" , e.toString());}
        SocketIO.getInstance().sendRanking(rankingMessage);
        showHomeActivity();
    }

    private void showHomeActivity(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }


}
