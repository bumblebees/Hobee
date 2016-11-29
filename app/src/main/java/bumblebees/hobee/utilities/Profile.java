package bumblebees.hobee.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.User;

public class Profile{

    static private Profile instance;
    private User user;

    private ArrayList<String> hostedEvents = new ArrayList<>();
    private ArrayList<String> acceptedEvents = new ArrayList<>();
    private ArrayList<String> pendingEvents = new ArrayList<>();


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


    /**
     * Check if the event matches the user.
     * @param event event to be checked
     * @return true if they match, false otherwise
     */
    public boolean matchesPreferences(Event event){
        //check if the user is already a member of the event or is the host
        if(event.getEvent_details().checkUser(user.getSimpleUser()) ||
                event.getEvent_details().getHost_id().equals(user.getUserID())){
            //user is in the event, does not need a notification
            return false;
        }

        //check if the age is larger than the max age, or smaller than the minimum age
        if(event.getEvent_details().getAge_max()<user.getAge() || event.getEvent_details().getAge_min() > user.getAge()){
            return false;
        }

        //check if there are gender restrictions to the event
        if(!event.getEvent_details().getGender().equals("everyone")){
            //check that the gender does not match the user's gender
            if(!event.getEvent_details().getGender().equals(user.getGender())){
                return false;
            }
        }

        //TODO: check hobbies as well

        return true;
    }


}
