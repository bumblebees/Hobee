package bumblebees.hobee.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Profile {

    static private Profile instance;
    private JSONObject user;


    public static Profile getInstance(){
        if (instance == null){
            synchronized (SocketIO.class){
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

    public void setUser(JSONObject jsonObject){
        this.user = jsonObject;
    }

    public JSONObject getUser(){
        return this.user;
    }

    public String getUserId(){
        try {
            return user.getString("loginId");
        } catch (JSONException e) {
            return null;
        }
    }

    public String getOrigin(){
        try {
            return user.getString("origin");
        } catch (JSONException e) {
            return null;
        }
    }

    public String getFirstName(){
        try {
            return user.getString("firstName");
        } catch (JSONException e) {
            return null;
        }
    }

    public String getLastName(){
        try {
            return user.getString("lastName");
        } catch (JSONException e) {
            return null;
        }
    }

    public String getBirthday(){
        try {
            return user.getString("birthday");
        } catch (JSONException e) {
            return null;
        }
    }

    public int getAge(){
        try {
            return calculateAgeFromDates(user.getString("birthday"));
        } catch (JSONException e) {
            return 0;
        }
    }

    public String getEmail(){
        try {
            return user.getString("email");
        } catch (JSONException e) {
            return null;
        }
    }

    public String getGender(){
        try {
            return user.getString("gender");
        } catch (JSONException e) {
            return null;
        }
    }

    public String getBio(){
        try {
            return user.getString("bio");
        } catch (JSONException e) {
            return null;
        }
    }

    public String getPicUrl(){
        if (getOrigin().equals("facebook")){
            return "https://graph.facebook.com/" + getUserId() + "/picture?width=200&height=200";
        }
        return null;
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
