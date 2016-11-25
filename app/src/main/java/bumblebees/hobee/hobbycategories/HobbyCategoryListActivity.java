package bumblebees.hobee.hobbycategories;

import android.content.Context;
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

    int padding = 0;
    int elementNumber = 0;
    ArrayList<String> hobbyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(activity_hobby_category_list);
        hobbyList = getIntent().getStringArrayListExtra("List");
        System.out.println(hobbyList);
        loopList(hobbyList);

    }

    public void loopList(ArrayList<String> L) {
        for (String element : L) {
            getView();
            System.out.println(elementNumber);
        }
    }
    public void getView() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        LayoutInflater Li = LayoutInflater.from(getApplicationContext());
        View view = Li.inflate(R.layout.hobby_item,null);
        tableLayout.addView(view);

        for (int i = 0; i < hobbyList.size()-1; i++){
            TableRow row = new TableRow(this);
            tableLayout.addView(row);

            for (int j = 0; j < hobbyList.size()-1; j++){
                TextView hobbyItemView = (TextView) findViewById(R.id.hobbyItem);
                LayoutInflater Li2 = LayoutInflater.from(getApplicationContext());
                View view2 = Li2.inflate(R.layout.hobby_item,null);
                System.out.println(">>>>>>>>>>>>>>>>>>>>"+elementNumber);
                System.out.println(">>>>>>>>>>" + hobbyList.size());
                String string = hobbyList.get(elementNumber);
                hobbyItemView.setText(string);
                row.addView(view2);
                elementNumber ++;

            }
        }
    }
}