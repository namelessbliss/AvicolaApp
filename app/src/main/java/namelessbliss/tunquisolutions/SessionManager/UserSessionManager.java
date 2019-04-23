package namelessbliss.tunquisolutions.SessionManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import namelessbliss.tunquisolutions.MainActivity;

public class UserSessionManager {

    //Estado de session
    boolean session = false;

    //Shared Preferences reference
    SharedPreferences pref;

    //Editor reference for shared preference
    SharedPreferences.Editor editor;

    //Contexto
    Context context;


    // Shared preferences file name
    private static final String PREFER_NAME = "session";

    // All Shared Preferences  Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // User name (variable public to access form outside)
    public static final String KEY_NAME = "nombre";

    // ID
    public static final String KEY_ID = "id";

    public UserSessionManager(Context _context) {
        this.context = _context;
        pref = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String id, String nombre) {

        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        //Storin name in pref
        editor.putString(KEY_NAME, nombre);

        //Storin id in pref
        editor.putString(KEY_ID, id);

        //commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * if else it will redirect user to login page
     * Else do anything
     */

    public boolean isNotLogin() {
        //Check login status
        if (!this.isUserLoggedIn()) {

            //user not logged in redirect him to Login Activity
            Intent intent = new Intent(context, MainActivity.class);

            //closing all the activities from stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //add new flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Starting Login Activity
            context.startActivity(intent);

            return true;
        }
        return false;
    }

    public boolean isLogin() {
        //Check login status
        if (this.isUserLoggedIn()) {

            return true;
        }
        return false;
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {

        // use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<>();

        //user id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        //user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        return user;
    }

    /**
     * Clear session details
     */

    public void logoutUser() {
        //Clearing all user data from shared preferences
        editor.clear();
        editor.commit();

        //After logout redirect user to login Activity
        Intent intent = new Intent(context, MainActivity.class);

        //closing all activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //add new flag to start new activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //start login activity
        context.startActivity(intent);
    }
    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public boolean isSession() {
        return session;
    }

    public void setSession(boolean session) {
        this.session = session;
    }
}
