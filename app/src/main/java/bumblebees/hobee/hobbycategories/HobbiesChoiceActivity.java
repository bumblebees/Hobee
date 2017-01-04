package bumblebees.hobee.hobbycategories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import bumblebees.hobee.HomeActivity;
import bumblebees.hobee.R;

public class HobbiesChoiceActivity extends AppCompatActivity {

    private Button showProfile;
    private ImageButton showSports;
    private ImageButton showOutdoors;
    private ImageButton showMusic;
    private ImageButton showCrafts;
    private ImageButton showCulinary;
    private ImageButton showBoardGames;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbieschoice);

        showSports = (ImageButton)findViewById(R.id.hobby1);
        showOutdoors = (ImageButton)findViewById(R.id.hobby2);
        showMusic = (ImageButton)findViewById(R.id.hobby3);
        showCrafts = (ImageButton)findViewById(R.id.hobby4);
        showCulinary = (ImageButton)findViewById(R.id.hobby5);
        showBoardGames = (ImageButton)findViewById(R.id.hobby6);
        showProfile = (Button)findViewById(R.id.showProfile);
        sportsList.add(football);
        sportsList.add(basketball);
        outdoorsList.add(camping);
        musicList.add(karaoke);
        musicList.add(ballroom);
        craftsList.add(museumTour);
        culinaryList.add(baking);
        culinaryList.add(cooking);
        boardGamesList.add(chess);
        boardGamesList.add(scrabble);
        boardGamesList.add(monopoly);

        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this way the stack of activities is cleared and if you resume the app from closing it on homepage
                // it will resume at homepage not here
                Intent intent = new Intent(HobbiesChoiceActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                HobbiesChoiceActivity.this.startActivity(intent);
                finish();
            }
        });

        showSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (HobbiesChoiceActivity.this, HobbyCategoryListActivity.class);
                System.out.println(HobbiesChoiceActivity.this);
                intent.putStringArrayListExtra("List", getSportsList());
                HobbiesChoiceActivity.this.startActivity(intent);
            }
        });

        showOutdoors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent (HobbiesChoiceActivity.this, HobbyCategoryListActivity.class);
                System.out.println(HobbiesChoiceActivity.this);
                intent.putStringArrayListExtra("List", getOutdoorsList());
                HobbiesChoiceActivity.this.startActivity(intent);
         //       startIntent(HobbiesChoiceActivity.this, HobbyCategoryListActivity.class, getOutdoorsList());
            }
        });

        showMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (HobbiesChoiceActivity.this, HobbyCategoryListActivity.class);
                System.out.println(HobbiesChoiceActivity.this);
                intent.putStringArrayListExtra("List", getMusicList());
                HobbiesChoiceActivity.this.startActivity(intent);
            //    startIntent(HobbiesChoiceActivity.this, HobbyCategoryListActivity.class, getMusicList());
            }
        });

        showCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (HobbiesChoiceActivity.this, HobbyCategoryListActivity.class);
                System.out.println(HobbiesChoiceActivity.this);
                intent.putStringArrayListExtra("List", getCraftsList());
                HobbiesChoiceActivity.this.startActivity(intent);
         //       startIntent(HobbiesChoiceActivity.this, HobbyCategoryListActivity.class, getCraftsList());
            }
        });

        showCulinary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (HobbiesChoiceActivity.this, HobbyCategoryListActivity.class);
                System.out.println(HobbiesChoiceActivity.this);
                intent.putStringArrayListExtra("List", getCulinaryList());
                HobbiesChoiceActivity.this.startActivity(intent);
          //      startIntent(HobbiesChoiceActivity.this, HobbyCategoryListActivity.class, getCulinaryList());
            }
        });

        showBoardGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (HobbiesChoiceActivity.this, HobbyCategoryListActivity.class);
                System.out.println(HobbiesChoiceActivity.this);
                intent.putStringArrayListExtra("List", getBoardGamesList());
                HobbiesChoiceActivity.this.startActivity(intent);
        //        startIntent(HobbiesChoiceActivity.this, HobbyCategoryListActivity.class, getBoardGamesList());
            }
        });
    }
/**
 * Declaration, inits, and getters of Lists
 */

    private ArrayList <String> sportsList = new ArrayList<>();
    private String football = "Football";
    private String basketball = "Basketball";

    private ArrayList<String> getSportsList() {
        return sportsList;
    }

    private ArrayList <String> outdoorsList = new ArrayList<>();
    private String camping = "Camping";

    private ArrayList<String> getOutdoorsList() {
        return outdoorsList;
    }

    private ArrayList <String> musicList = new ArrayList<>();
    private String karaoke = "Karaoke";
    private String ballroom = "Ballroom";

    private ArrayList<String> getMusicList() {
        return musicList;
    }

    private ArrayList <String> craftsList = new ArrayList<>();
    private String museumTour = "Museum Tour";

    private ArrayList<String> getCraftsList() {
        return craftsList;
    }

    private ArrayList <String>  culinaryList = new ArrayList<>();
    private String baking = "Baking";
    private String cooking = "Cooking";

    private ArrayList<String> getCulinaryList() {
        return culinaryList;
    }

    private ArrayList <String>  boardGamesList = new ArrayList<>();
    private String chess = "Chess";
    private String scrabble = "Scrabble";
    private String monopoly = "Monopoly";

    private ArrayList<String> getBoardGamesList() {
        return boardGamesList;
    }
}
