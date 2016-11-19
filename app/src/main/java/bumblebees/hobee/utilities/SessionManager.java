package bumblebees.hobee.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    private SharedPreferences preferences;
    private Editor editor;
    private Context context;

    private static final String PREFERENCE_NAME = "HobeeSessionPreferences";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String KEY_LOGIN_ID = "loginId";
    private static final String KEY_ORIGIN = "origin";


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

}
