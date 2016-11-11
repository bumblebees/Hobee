package bumblebees.hobee.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import bumblebees.hobee.HomeActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionManager {

    private SharedPreferences preferences;
    private Editor editor;
    private Context context;

    private static final String PREFERENCE_NAME = "HobeeSessionPreferences";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String KEY_LOGIN_ID = "loginId";
    private static final String KEY_ORIGIN = "origin";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_BIO = "bio";

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
     * @param object contains all user data
     */
    public void createSession(JSONObject object){
        try {
            editor.putBoolean(IS_LOGIN, true);
            editor.putString(KEY_LOGIN_ID, object.getString("loginId"));
            editor.putString(KEY_ORIGIN, object.getString("origin"));
            editor.putString(KEY_FIRST_NAME, object.getString("firstName"));
            editor.putString(KEY_LAST_NAME, object.getString("lastName"));
            editor.putString(KEY_BIRTHDAY, object.getString("birthday"));
            editor.putString(KEY_EMAIL, object.getString("email"));
            editor.putString(KEY_GENDER, object.getString("gender"));
            editor.putString(KEY_BIO, object.getString("bio"));
            editor.commit();
        }
        catch (JSONException e){
            Log.d("SESSIONS", e.toString());
        }
    }

    /**
     * Check login status, if logged in, send user to HomeActivity
     */
    public void checkLogin(){
        if (this.isLoggedIn()){
            Intent intent = new Intent(context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.d("SESSION", "User is logged in");
        }
    }

    /**
     * Logout user and clear session data
     */
    public void logoutUser(){
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

    /**
     *  Getters
     */
    public String getId(){
        return preferences.getString(KEY_LOGIN_ID, null);
    }
    public String getOrigin(){
        return preferences.getString(KEY_ORIGIN, null);
    }
    public String getfirstName(){
        return preferences.getString(KEY_FIRST_NAME, null);
    }
    public String getLastName(){
        return preferences.getString(KEY_LAST_NAME, null);
    }
    public String getBirthday(){
        return preferences.getString(KEY_BIRTHDAY, null);
    }
    public String getEmail(){
        return preferences.getString(KEY_EMAIL, null);
    }
    public String getGender(){
        return preferences.getString(KEY_GENDER, null);
    }
    public String getBio(){
        return preferences.getString(KEY_BIO, null);
    }

}
