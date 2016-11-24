package bumblebees.hobee.hobbycategories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import bumblebees.hobee.HomeActivity;
import bumblebees.hobee.R;

public class HobbiesChoiceActivity extends HobbyCategory {

    ImageButton showProfile;
    ImageButton showSports;
    ImageButton showOutdoors;
    ImageButton showMusic;
    ImageButton showCrafts;
    ImageButton showCulinary;
    ImageButton showBoardGames;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbieschoice);

        showProfile = (ImageButton)findViewById(R.id.showProfile);
        showSports = (ImageButton)findViewById(R.id.hobby1);
        showOutdoors = (ImageButton)findViewById(R.id.hobby2);
        showMusic = (ImageButton)findViewById(R.id.hobby3);
        showCrafts = (ImageButton)findViewById(R.id.hobby4);
        showCulinary = (ImageButton)findViewById(R.id.hobby5);
        showBoardGames = (ImageButton)findViewById(R.id.hobby6);

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
                sportsList.add(football);
                sportsList.add(basketball);
                startIntent(HobbiesChoiceActivity.this, SportsActivity.class, getSportsList());
            }
        });

        showOutdoors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outdoorsList.add(camping);
                startIntent(HobbiesChoiceActivity.this, SportsActivity.class, getOutdoorsList());
            }
        });

        showMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicList.add(karaoke);
                musicList.add(ballroom);
                startIntent(HobbiesChoiceActivity.this, SportsActivity.class, getMusicList());
            }
        });

        showCrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                craftsList.add(museumTour);
                startIntent(HobbiesChoiceActivity.this, SportsActivity.class, getCraftsList());
            }
        });

        showCulinary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                culinaryList.add(baking);
                culinaryList.add(cooking);
                startIntent(HobbiesChoiceActivity.this, SportsActivity.class, getCulinaryList());
            }
        });

        showBoardGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boardGamesList.add(chess);
                boardGamesList.add(scrabble);
                boardGamesList.add(monopoly);
                startIntent(HobbiesChoiceActivity.this, SportsActivity.class, getBoardGamesList());
            }
        });
    }
/**
 * Declaration, inits, and getters of Lists
 */

    private ArrayList <String> sportsList = new ArrayList<>();
    String football = "Football";
    String basketball = "Basketball";

    public ArrayList<String> getSportsList() {
        return sportsList;
    }

    private ArrayList <String> outdoorsList = new ArrayList<>();
    String camping = "Camping";

    public ArrayList<String> getOutdoorsList() {
        return outdoorsList;
    }

    private ArrayList <String> musicList = new ArrayList<>();
    String karaoke = "Karaoke";
    String ballroom = "Ballroom";

    public ArrayList<String> getMusicList() {
        return musicList;
    }

    private ArrayList <String> craftsList = new ArrayList<>();
    String museumTour = "Museum Tour";

    public ArrayList<String> getCraftsList() {
        return craftsList;
    }

    private ArrayList <String>  culinaryList = new ArrayList<>();
    String baking = "Baking";
    String cooking = "Cooking";

    public ArrayList<String> getCulinaryList() {
        return culinaryList;
    }

    private ArrayList <String>  boardGamesList = new ArrayList<>();
    String chess = "Chess";
    String scrabble = "Scrabble";
    String monopoly = "Monopoly";

    public ArrayList<String> getBoardGamesList() {
        return boardGamesList;
    }
}
