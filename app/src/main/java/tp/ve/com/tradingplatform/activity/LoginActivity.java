package tp.ve.com.tradingplatform.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.helper.SQLiteHandler;
import tp.ve.com.tradingplatform.helper.SessionManager;

/**
 * Created by Zeng on 2015/11/4.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;

    private EditText edt_username;
    private EditText edt_password;
    private TextInputLayout inputLayoutName, inputLayoutPassword;
    Button btn_login, btnLinkToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        edt_username = (EditText) findViewById(R.id.input_name);
        edt_password = (EditText) findViewById(R.id.input_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());
        edt_username.setText(session.getUserName());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                if (submitForm()) {
                    checkLogin(username, password);
                }

            }
        });
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        SignupActivity.class);
                startActivity(i);
                finish();
            }
        });

        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btn_login.performClick();
                }
                return false;
            }
        });

    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean submitForm() {
        if (!validateName()) {
            return false;
        }
        if (!validatePassword()) {
            return false;
        }
        return true;
    }

    private boolean validateName() {
        if (edt_username.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(edt_username);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validatePassword() {
        if (edt_password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(edt_password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String name, final String password) {
        // Tag used to cancel the request
        pDialog.setMessage("Logging in ...");
        showDialog();

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Boolean success = Boolean.valueOf(jObj.getString("success"));
//                    Toast.makeText(getApplicationContext(), success.toString(), Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (success) {
                        String member_id = jObj.getString("id");
                        JSONObject jObjData = new JSONObject(jObj.getString("data"));
                        String tokenId = jObjData.getString("token");
                        Log.d(TAG, "token:" + jObjData.getString("token"));
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true, tokenId, name, member_id);

//                     Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = "Invalid username or password";
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Login Error98888888: " + error.getMessage());
                String errorMsg = "Invalid username or password";
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
                hideDialog();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                headers.put("Accept-Type", "application/json");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}


