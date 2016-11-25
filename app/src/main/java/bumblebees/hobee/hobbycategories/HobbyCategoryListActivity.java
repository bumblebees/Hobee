package bumblebees.hobee.hobbycategories;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import bumblebees.hobee.R;

import static bumblebees.hobee.R.layout.activity_hobby_category_list;
import static bumblebees.hobee.R.layout.activity_sportshobby;

public class HobbyCategoryListActivity extends AppCompatActivity {


    ArrayList<String> hobbyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(activity_hobby_category_list);
        hobbyList = getIntent().getStringArrayListExtra("List");
        loopList(hobbyList);
    }

    public void loopList(ArrayList<String> L) {
            getView();

    }
    public void getView() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        for (String hobby : hobbyList){

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View hobbyView = inflater.inflate(R.layout.hobby_item,tableLayout, false);
            TextView hobbyItemView = (TextView) hobbyView.findViewById(R.id.hobbyItem);

            Typeface face= Typeface.createFromAsset(getAssets(), "font/Proxima.ttf");
            hobbyItemView.setTypeface(face);

            hobbyItemView.setText(hobby);
            tableLayout.addView(hobbyView);

        }
    }
}