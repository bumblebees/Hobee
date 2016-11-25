package bumblebees.hobee.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bumblebees.hobee.objects.User;

public class Profile{

    static private Profile instance;
    private User user;


    public static Profile getInstance(){
        if (instance == null){
            synchronized (Profile.class){
                if(instance == null){
                    instance = new Profile();
                }
            }
        }
        return instance;
    }

    /**
     *  Empty constructor
     */
    private Profile(){

    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

    public String getUserID(){
      return user.getUserID();
    }

    public String getLoginId(){
        return user.getLoginId();
    }

    public String getOrigin(){
       return user.getOrigin();
    }

    public String getFirstName(){
        return user.getFirstName();
    }

    public String getLastName(){
       return user.getLastName();
    }

    public String getBirthday(){
        return user.getBirthday();
    }

    public int getAge(){
            return calculateAgeFromDates(user.getBirthday());
    }

    public String getEmail(){
       return user.getEmail();
    }

    public String getGender(){
       return user.getGender();
    }

    public String getBio(){
        return user.getBio();
    }

    public String getPicUrl(){
        return "http://gunray.skip.chalmers.se:3003/api/containers/userImages/download/" + getUserID() + ".png";
    }

    /**
     * This method calculates the age of the user given the user's birthday as a String
     * in the format of dd/MM/yyyy.
     * @param dateText String
     * @return int
     */
    private int calculateAgeFromDates(String dateText) {
        int age = 0;
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
