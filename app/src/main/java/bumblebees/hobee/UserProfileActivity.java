package bumblebees.hobee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import bumblebees.hobee.utilities.SessionManager;

import java.util.GregorianCalendar;


public class UserProfileActivity extends AppCompatActivity {
    private TextView fbName;
    private TextView fbEmail;
    private TextView fbGender;
    private TextView fbAge;
    private ImageView fbImage;
    private TextView hobbiesList;
    private TextView profileBio;
    int age = 0;

    /**
     * This method calculates the age of the user given the user's birthday as a String
     * in the format of dd/MM/yyyy.
     * @param dateText String
     */
    private int calculateAgeFromDates(String dateText) {
        try {
            Calendar birthday = new GregorianCalendar();
            Calendar today = new GregorianCalendar();
            int factor = 0; //to correctly calculate age when birthday has not been celebrated this year
            Date birthDate = new SimpleDateFormat("MM/dd/yyyy").parse(dateText);
            Date currentDate = new Date(); //today

            birthday.setTime(birthDate);
            today.setTime(currentDate);

            // check if birthday has been celebrated this year
            if (today.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
                factor = -1; //birthday not celebrated
            }
            age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR) + factor;
        } catch (ParseException e) {
                System.out.println("Given date not in expected format dd/MM/yyyy");
        }
        return age;
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //TODO: rename variables
        fbName   = (TextView) findViewById(R.id.userName);
        fbEmail  =  (TextView) findViewById(R.id.userEmail);
        fbGender = (TextView) findViewById(R.id.userGender);
        fbAge    = (TextView) findViewById(R.id.userAge);
        fbImage  = (ImageView) findViewById(R.id.fbImage);
        hobbiesList = (TextView) findViewById(R.id.listhobbies);
        profileBio = (TextView) findViewById(R.id.profileBio);


        SessionManager session = new SessionManager(getApplicationContext());

        fbName.setText(session.getfirstName() + " " + session.getLastName());
        fbEmail.setText(session.getEmail());
        fbGender.setText(session.getGender());
        //TODO: receive this as an UNIX date and calculate the age instead of the birthday
        fbAge.setText(Integer.toString(calculateAgeFromDates(session.getBirthday())));
        profileBio.setText(session.getBio());

    }





}