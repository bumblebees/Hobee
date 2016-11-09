package bumblebees.hobee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

class SessionManager {

    private SharedPreferences preferences;
    private Editor editor;
    private Context context;

    private static final String PREFERENCE_NAME = "HobeeSessionPreferences";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String KEY_ID = "id";
    private static final String KEY_ORIGIN = "origin";

    /**
     * Constructor
     * @param context context
     */
    SessionManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        editor = preferences.edit();
    }

    /**
     * Create session
     * @param id facebook or google id
     * @param origin facebook or google
     */
    void createSession(String id, String origin){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_ORIGIN, origin);
        editor.commit();
    }

    /**
     * Check login status, if logged in, send user to HomeActivity
     */
    void checkLogin(){
        if (this.isLoggedIn()){
            Intent intent = new Intent(context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    String getId(){
        return preferences.getString(KEY_ID, null);
    }

    String getOrigin(){
        return preferences.getString(KEY_ORIGIN, null);
    }

    /**
     * Logout user and clear session data
     */
    void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.apply();
    }

    /**
     * Chech login state
     * @return true if logged in
     */
    private boolean isLoggedIn(){
        return preferences.getBoolean(IS_LOGIN, false);
    }

}
