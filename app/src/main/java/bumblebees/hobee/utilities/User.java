package bumblebees.hobee.utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    static private User instance;
    private JSONObject user;


    public static User getInstance(){
        if (instance == null){
            synchronized (SocketIO.class){
                if(instance == null){
                    instance = new User();
                }
            }
        }
        return instance;
    }

    /**
     *  Empty constructor
     */
    private User(){

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

}
