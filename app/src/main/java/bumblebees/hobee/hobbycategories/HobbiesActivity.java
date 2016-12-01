package bumblebees.hobee.hobbycategories;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import bumblebees.hobee.R;
import bumblebees.hobee.objects.Hobby;

public class HobbiesActivity extends AppCompatActivity {

    Hobby hobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);
        hobby = getIntent().getParcelableExtra("Hobby");
        System.out.println(hobby.getName());
    }


}
