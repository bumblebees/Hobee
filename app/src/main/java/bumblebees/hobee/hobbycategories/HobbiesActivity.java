package bumblebees.hobee.hobbycategories;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bumblebees.hobee.R;
import bumblebees.hobee.jsonparser.JSONParser;
import bumblebees.hobee.objects.Hobby;

public class HobbiesActivity extends AppCompatActivity {

    Hobby hobby;
    TextView textView;
    JSONParser jsonParser;

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

    private Hobby createHobbyInstance (String hobbyName) {
        Hobby hobby = new Hobby(hobbyName);
        return hobby;
    }

    private Hobby getBasicHobby(){

        hobby.setDifficultyLevel("userInput");
        hobby.setDatePreference("userInput");
        hobby.setTimePreference("userInput");
        return hobby;
    }

    private Hobby getBasketballHobby(){
        return hobby;
    }

    private Hobby getFootballHobby(){
        return hobby;
    }

    /**
    private JSONObject appendJSONDataToJSONFile(String jsonFile) throws Exception{
        jsonParser = new JSONParser();
        JSONObject newJSONDataObject = new JSONObject();
        JSONArray jsonArray = currentJSONFile.getJSONArray("sample");

        try {
            newJSONDataObject.put("someInfo", XX);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(newJSONDataObject);

        return newJSONDataObject;
    }
     */
}

