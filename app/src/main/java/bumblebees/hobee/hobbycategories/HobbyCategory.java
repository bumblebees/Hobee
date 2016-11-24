package bumblebees.hobee.hobbycategories;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bumblebees.hobee.R;


public class HobbyCategory extends AppCompatActivity {

    public void startIntent(Activity currentActivity, Class targetActivity, ArrayList <String> List){
        Intent intent = new Intent(currentActivity, targetActivity);
        intent.putStringArrayListExtra("List", (ArrayList<String>) List);
        currentActivity.startActivity(intent);
    }

    public void loopList (ArrayList <String> L) {
        for (String element : L) {
     //       TextView t = (TextView)findViewById(R.id.textView);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    (RelativeLayout.LayoutParams.WRAP_CONTENT), (RelativeLayout.LayoutParams.WRAP_CONTENT));

            RelativeLayout relative = new RelativeLayout(getApplicationContext());
            relative.setLayoutParams(lp);

            TextView tv = new TextView(getApplicationContext());
            tv.setLayoutParams(lp);

            EditText edittv = new EditText(getApplicationContext());
            edittv.setLayoutParams(lp);

            relative.addView(tv);
            relative.addView(edittv);

        }
    }
}

