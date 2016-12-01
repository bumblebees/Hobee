package bumblebees.hobee.hobbycategories;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Hobby;

public class HobbiesActivity extends AppCompatActivity {

    Hobby hobby;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);
        String hobbyName = getIntent().getExtras().getString("HobbyName");
        hobby = createHobbyInstance(hobbyName);

        textView = (TextView) findViewById(R.id.name);
        textView.setText(hobby.getName());

        Typeface face= Typeface.createFromAsset(getAssets(), "font/Proxima.ttf");
        textView.setTypeface(face);

    }

    public Hobby createHobbyInstance (String hobbyName) {
        Hobby hobby = new Hobby(hobbyName);
        return hobby;
    }
}

