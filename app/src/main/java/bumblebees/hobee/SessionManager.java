package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    private SharedPreferences preferences;
    private Editor editor;
    private Context context;

    private static final String PREFERENCE_NAME = "HobeeSessionPreferences";
    private static final String IS_LOGIN = "isLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_ORIGIN = "origin";

    /**
     * Constructor
     * @param context context
     */
    public SessionManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        editor = preferences.edit();
    }

    /**
     * Create session
     * @param id facebook or google id
     * @param origin facebook or google
     */
    public void createSession(String id, String origin){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_ORIGIN, origin);
        editor.commit();
    }

    /**
     * Check login status, if logged in, send user to HomeActivity
     */
    public void checkLogin(){
        if (this.isLoggedIn()){
            Intent intent = new Intent(context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * Logout user and clear session data
     */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent intent = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Chech login state
     * @return true if logged in
     */
    public boolean isLoggedIn(){
        return preferences.getBoolean(IS_LOGIN, false);
    }

}
