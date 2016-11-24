package bumblebees.hobee.hobbycategories;

import android.os.Bundle;

import java.util.ArrayList;

import bumblebees.hobee.R;

public class SportsActivity extends HobbyCategory {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);
        ArrayList<String> sportList = getIntent().getStringArrayListExtra("sportList");
        loopList(sportList);
    }


}
