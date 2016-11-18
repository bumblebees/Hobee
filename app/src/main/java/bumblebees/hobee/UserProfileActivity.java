package bumblebees.hobee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import bumblebees.hobee.utilities.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class UserProfileActivity extends AppCompatActivity {

    TextView userName;
    TextView userEmail;
    TextView userGender;
    TextView userAge;
    ImageView userImage;
    TextView hobbiesList;
    TextView userBio;
    int age = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userName = (TextView) findViewById(R.id.userName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userGender = (TextView) findViewById(R.id.userGender);
        userAge = (TextView) findViewById(R.id.userAge);
        userImage = (ImageView) findViewById(R.id.userImage);
        hobbiesList = (TextView) findViewById(R.id.listhobbies);
        userBio = (TextView) findViewById(R.id.userBio);

        userName.setText(User.getInstance().getFirstName() + " " + User.getInstance().getLastName());
        userEmail.setText(User.getInstance().getEmail());
        userGender.setText(User.getInstance().getGender());
        userAge.setText(Integer.toString(calculateAgeFromDates(User.getInstance().getBirthday())));
        userBio.setText(User.getInstance().getBio());

    }

    /**
     * This method calculates the age of the user given the user's birthday as a String
     * in the format of dd/MM/yyyy.
     *
     * @param dateText String
     */
    private int calculateAgeFromDates(String dateText) {
        try {
            Calendar birthday = new GregorianCalendar();
            Calendar today = new GregorianCalendar();
            int factor = 0; //to correctly calculate age when birthday has not been celebrated this year
            Date birthDate = new SimpleDateFormat("yyyy/MM/dd").parse(dateText);
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


}