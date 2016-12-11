package bumblebees.hobee.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import bumblebees.hobee.objects.User;

public class SessionManager {

    private SharedPreferences preferences;
    private Editor editor;
    private Context context;
    private Gson gson = new Gson();

    private static final String PREFERENCE_NAME = "HobeeSessionPreferences";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String KEY_LOGIN_ID = "loginId";
    private static final String KEY_ORIGIN = "origin";

    private static final String USER = "user";
    private static final String USERID = "userID";
    private static final String EVENT_MANAGER = "eventManager";


    /**
     * Constructor
     *
     * @param context context
     */
    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        editor = preferences.edit();
    }

    /**
     * Create session
     *
     * @param id     facebook/google id
     * @param origin facebook/google
     */
    public void setPreferences(String id, String origin) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_LOGIN_ID, id);
        editor.putString(KEY_ORIGIN, origin);
        editor.commit();
    }

    /**
     * Save the user and event data in the preferences.
     * @param user - User object
     * @param eventManager - event manager associated with the user
     */
    public void saveDataAndEvents(User user, EventManager eventManager){
        saveUser(user);
        saveEvents(eventManager);
    }

    /**
     * Save the event data to the preferences.
     * @param eventManager
     */
    public void saveEvents(EventManager eventManager){
        editor.putString(EVENT_MANAGER, gson.toJson(eventManager));
        editor.commit();
    }

    /**
     * Save the user data to the preferences
     * @param user
     */
    public void saveUser(User user){
        editor.putString(USER, gson.toJson(user));
        editor.putString(USERID, user.getUserID());
        editor.commit();
    }

    /**
     * Logout user and clear session data
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Chech login state
     *
     * @return true if logged in
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }

    /**
     * Getters
     */


    public String getId() {
        return preferences.getString(KEY_LOGIN_ID, null);
    }

    public String getOrigin() {
        return preferences.getString(KEY_ORIGIN, null);
    }

    public EventManager getEventManager(){
        String eventManager = preferences.getString(EVENT_MANAGER, null);
        if(eventManager == null){
            return null;
        }
        else{
            return gson.fromJson(eventManager, EventManager.class);
        }
    }

    public User getUser(){
        String user = preferences.getString(USER, null);
        if(user == null){
            return null;
        }
        else{
            return gson.fromJson(user, User.class);
        }
    }

    public String getUserID() {
        return preferences.getString(USERID, null);
    }
}
