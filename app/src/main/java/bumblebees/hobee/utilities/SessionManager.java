package bumblebees.hobee.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import bumblebees.hobee.objects.Event;
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

    private static final String EVENT_HOSTED = "eventHosted";
    private static final String EVENT_JOINED = "eventJoined";
    private static final String EVENT_PENDING = "eventPending";
    private static final String EVENT_BROWSE = "eventBrowse";
    private static final String EVENT_HISTORY_HOSTED = "eventHistoryHosted";
    private static final String EVENT_HISTORY_JOINED = "eventHistoryJoined";


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
        saveAllEvents(eventManager);
    }

    /**
     * Save the event data to the preferences.
     * @param eventManager
     */
    public void saveAllEvents(EventManager eventManager){
        saveCurrentEvents(eventManager.getHostedEvents(), eventManager.getAcceptedEvents(), eventManager.getPendingEvents());
        saveBrowseEvents(eventManager.getEligibleEventList());
        saveEventsHistory(eventManager.getHistoryJoinedEvents(), eventManager.getHistoryHostedEvents());
    }

    public void saveCurrentEvents(ArrayList<Event> hostedEvents, ArrayList<Event> joinedEvents, ArrayList<Event> pendingEvents){
        editor.putString(EVENT_HOSTED, gson.toJson(hostedEvents));
        editor.putString(EVENT_PENDING, gson.toJson(pendingEvents));
        editor.putString(EVENT_JOINED, gson.toJson(joinedEvents));
        editor.commit();
    }

    public void saveBrowseEvents(HashMap<String, ArrayList<Event>> eligibleList){
        editor.putString(EVENT_BROWSE, gson.toJson(eligibleList));
        editor.commit();
    }

    public void saveEventsHistory(ArrayList<Event> joinedEvents, ArrayList<Event> hostedEvents){
        editor.putString(EVENT_HISTORY_HOSTED, gson.toJson(hostedEvents));
        editor.putString(EVENT_HISTORY_JOINED, gson.toJson(joinedEvents));
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
        editor.commit();
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

    public ArrayList<Event> getHostedEvents(){
        return getEventListArray(EVENT_HOSTED);
    }

    public ArrayList<Event> getJoinedEvents(){
        return getEventListArray(EVENT_JOINED);
    }

    public ArrayList<Event> getPendingEvents(){
        return getEventListArray(EVENT_PENDING);
    }


    public ArrayList<Event> getHistoryJoined(){
        return getEventListArray(EVENT_HISTORY_JOINED);
    }

    public ArrayList<Event> getHistoryHosted(){
        return getEventListArray(EVENT_HISTORY_HOSTED);
    }

    public HashMap<String, ArrayList<Event>> getBrowseEvents(){
        String events = preferences.getString(EVENT_BROWSE, null);
        if(events == null){
            return null;
        }
        else{
            return gson.fromJson(events, new TypeToken<HashMap<String, ArrayList<Event>>>(){}.getType());
        }
    }

    private ArrayList<Event> getEventListArray(String type){
        String events = preferences.getString(type, null);
        if(events == null){
            return null;
        }
        else{
            return gson.fromJson(events, new TypeToken<ArrayList<Event>>(){}.getType());
        }

    }

    public EventManager getAllEvents(){
        return new EventManager(getJoinedEvents(), getBrowseEvents(), getHistoryHosted(), getHistoryJoined(), getHostedEvents(), getPendingEvents());
    }


}
