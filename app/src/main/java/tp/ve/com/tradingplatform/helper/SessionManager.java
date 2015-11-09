package tp.ve.com.tradingplatform.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import tp.ve.com.tradingplatform.activity.MainActivity;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "TPLogin";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_TOKEN_ID = "tokenId";
    private static final String KEY_IS_USER_NAME = "userName";
    private String temptoken, tempusername;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String token, String username) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_IS_TOKEN_ID, token);
        editor.putString(KEY_IS_USER_NAME, username);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        Log.d(TAG, "777777777777777:" + pref.getString(KEY_IS_USER_NAME, "GUEST"));
        return pref.getString(KEY_IS_USER_NAME, "GUEST");
    }

    public void verifyToken() {
        String tag_string_req = "test_token";
        temptoken = pref.getString(KEY_IS_TOKEN_ID, "");
        tempusername = pref.getString(KEY_IS_USER_NAME, "GUEST");
//        Log.d(TAG, "dsfsdfsd" + temptoken);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.TEST_TOKEN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                MainActivity.showLogoutMenu();
//            Toast.makeText(MainActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar
                        .make(MainActivity.drawer, "You have logged in", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                MainActivity.logoutUser();
                MainActivity.showSignupMenu();
                Snackbar snackbar = Snackbar
                        .make(MainActivity.drawer, "Your session has expired, Login again!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + temptoken);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
