package bumblebees.hobee.utilities;

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

}
