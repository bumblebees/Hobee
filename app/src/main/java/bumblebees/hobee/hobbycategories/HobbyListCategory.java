package bumblebees.hobee.hobbycategories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bumblebees.hobee.R;


public class HobbyListCategory extends AppCompatActivity {

    public void startIntent(Activity currentActivity, Class targetActivity, ArrayList <String> List){
        Intent intent = new Intent(currentActivity, targetActivity);
        intent.putStringArrayListExtra("List", (ArrayList<String>) List);
        currentActivity.startActivity(intent);
    }
}

