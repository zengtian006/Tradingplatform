package tp.ve.com.tradingplatform.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import junit.framework.Test;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.LoginActivity;
import tp.ve.com.tradingplatform.activity.SignupActivity;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;

/**
 * Created by Zeng on 2015/11/4.
 */
public class SignupEmailFragment extends Fragment {
    private static final String TAG = SignupActivity.class.getSimpleName();
    EditText edt_username, edt_email, edt_password, edt_confirm_password;
    TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, inputLayoutConfirmPassword;
    Button btn_signup;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup_email, container, false);

        edt_email = (EditText) rootView.findViewById(R.id.input_email);
        edt_username = (EditText) rootView.findViewById(R.id.input_name);
        edt_password = (EditText) rootView.findViewById(R.id.input_password);
        edt_confirm_password = (EditText) rootView.findViewById(R.id.input_confirm_password);
        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) rootView.findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = (TextInputLayout) rootView.findViewById(R.id.confirm_layout_password);
        btn_signup = (Button) rootView.findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitForm()) {
                    String username = edt_username.getText().toString();
                    String password = edt_password.getText().toString();
                    String useremail = edt_email.getText().toString();
                    registerUser(username, useremail, password);
                }

            }
        });

        return rootView;
    }

    private void registerUser(final String name, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
//                    response = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                    JSONObject jObj = new JSONObject(response);
                    boolean success = Boolean.valueOf(jObj.getString("success"));
                    if (success) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        Toast.makeText(SignupActivity.context, "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                getContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SignupActivity.context,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(SignupActivity.context,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "email");
                params.put("name", name);
                params.put("password", password);
                params.put("email", email);
                params.put("status", "A");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private boolean submitForm() {
        if (!validateEmail()) {
            return false;
        }
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
            inputLayoutName.setError(getString(R.string.err_msg_name_only));
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = edt_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (edt_password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            return false;
        } else if (edt_confirm_password.getText().toString().trim().isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password));
            return false;
        } else if (!edt_password.getText().toString().trim().equals(edt_confirm_password.getText().toString().trim())) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password_mismatch));
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
