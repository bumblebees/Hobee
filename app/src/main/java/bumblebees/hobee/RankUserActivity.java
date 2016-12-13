package bumblebees.hobee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

        Toolbar rankUserToolbar = (Toolbar) findViewById(R.id.rankUserToolbar);
        rankUserToolbar.setTitle(event.getEvent_details().getEvent_name());
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
                        if(i==0) child.put(i, ranks[j][i]);
                        if (i == 1){
                            child.put(i, Integer.parseInt(ranks[j][i]));
                            if (!hasranked)
                                if (Integer.parseInt(ranks[j][i]) != 0)
                                    hasranked = true;
                        }
                        if(i==2) child.put(i,Boolean.parseBoolean(ranks[j][i]));
                    }
                    parent.put(j, child);
                }


                rankingMessage.put("hasRanked", hasranked);
                rankingMessage.put("userID", Profile.getInstance().getUserID());
                rankingMessage.put("eventID", event.getEventID());
                rankingMessage.put("hostRep", Integer.parseInt(ranks[0][1]));
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
