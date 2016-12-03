package bumblebees.hobee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import bumblebees.hobee.utilities.userRankAdapter;

public class RankUserActivity extends AppCompatActivity {
    ListView usersList;
    ArrayList<String> usersID;
    ArrayList<String> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_user);
        usersList = (ListView) findViewById(R.id.usersList);
        //ArrayList<String> users = getIntent().getStringArrayListExtra("userList");
        usersID = new ArrayList<>();
        usersID.add("3b6bbb11-dd2f-4cf3-a8b5-fee9a9f61b9c");
        usersID.add("b866f37c-632a-4452-a6a8-a618b8aca485");
        usersID.add("2579a3a9-a356-49d2-9a8d-ba759ceb8598");
        usersID.add("8505ab72-9ad1-4d9c-bee8-b79fb426c0f5");
        usersID.add("a43cde30-a0ee-459d-b1dc-0b88e3b744a2");


        ////TODO Method that sends a list of users to the server and retrieves userIDS
        //SocketIO.getInstance().getUsers(users);
        users = new ArrayList<>();
       // users.add("{ \"_id\" : ObjectId(\"58235ea7bb74fa0bac1bef59\"), \"userID\" : \"3b6bbb11-dd2f-4cf3-a8b5-fee9a9f61b9c\", \"loginId\" : \"116857775793190818159\", \"origin\" : \"google\", \"firstName\" : \"Mantas\", \"lastName\" : \"N\", \"birthday\" : \"\", \"email\" : \"namgaudis@gmail.com\", \"gender\" : \"male\", \"bio\" : \"\", \"rank\" : { \"globalRank\" : 0, \"hostRank\": 0, \"noShows\" : 0 }, \"hobbies\" : [ ], \"created\" : ISODate(\"2016-11-09T17:36:39.370Z\") }");
       // users.add("{ \"_id\" : ObjectId(\"58239e38f4cbd91f1e86e769\"), \"userID\" : \"b866f37c-632a-4452-a6a8-a618b8aca485\", \"loginId\" : \"100541255965769310753\", \"origin\" : \"google\", \"firstName\" : \"Jarno\", \"lastName\" : \"Kytö\", \"birthday\" : \"1995/07/01\", \"email\" : \"anton.kyto@gmail.com\", \"gender\" : \"male\", \"bio\" : \"Hey I like flowers\\n\", \"rank\" : { \"globalRank\" : 0, \"hostRank\" : 0, \"noShows\" : 0 }, \"hobbies\" : [ ], \"created\" : ISODate(\"2016-11-09T22:07:52.890Z\") }");
        users.add("{ \"_id\" : ObjectId(\"5838361c11ee6d66b211f551\"), \"bio\" : \"hi\", \"birthday\" : \"1990/1/1\", \"created\" : \"1480078884\", \"email\" : \"testatestydottir@gmail.com\", \"firstName\" : \"Testa\", \"gender\" : \"female\", \"hobbies\" : [ ], \"lastName\" : \"Testydottir\", \"loginId\" : \"114229479860648387567\", \"origin\" : \"google\", \"rank\" : { \"globalRank\" : 0, \"hostRank\" : 0, \"noShows\" : 0, \"reputation\" : 0 }, \"userID\" : \"2579a3a9-a356-49d2-9a8d-ba759ceb8598\" }\n");
        users.add("{ \"_id\" : ObjectId(\"5837469dd2812a0a9f444f58\"), \"bio\" : \"\", \"birthday\" : \"1990/03/11\", \"created\" : \"Nov 24, 2016 8:59:24 PM\", \"email\" : \"gusnamgma@student.gu.se\", \"firstName\" : \"Testy\", \"gender\" : \"male\", \"hobbies\" : [ ],\"lastName\" : \"Testisson\", \"loginId\" : \"133571510450954\", \"origin\" : \"facebook\", \"rank\" : { \"globalRank\" : 0, \"hostRank\" : 0, \"noShows\" : 0 }, \"userID\" : \"8505ab72-9ad1-4d9c-bee8-b79fb426c0f5\" }\n");
        users.add("{ \"_id\" : ObjectId(\"5837445ad2812a0a9f444f57\"), \"bio\" : \"nope\", \"birthday\" : \"1990/2/1\", \"created\" : \"1480016984\", \"email\" : \"haha\", \"firstName\" : \"Some\", \"gender\" : \"male\", \"hobbies\" : [ ], \"lastName\" : \"Body\", \"loginId\" : \"109673242154927933461\", \"origin\" : \"google\", \"rank\" : { \"globalRank\" : 0, \"hostRank\" : 0, \"noShows\" : 0, \"reputation\" : 0 }, \"userID\" : \"a43cde30-a0ee-459d-b1dc-0b88e3b744a2\" }\n");

        usersList.setAdapter(new userRankAdapter(this,users));
    }


}
