package tp.ve.com.tradingplatform.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import tp.ve.com.tradingplatform.activity.MainActivity;
import tp.ve.com.tradingplatform.activity.SignupActivity;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.helper.SessionManager;

/**
 * Created by Zeng on 2015/11/18.
 */
public class AccountSettingBuyerFragment extends Fragment {
    private final static String TAG = AccountSettingBuyerFragment.class.getSimpleName();

    String member_id, member_name, member_mobile, member_gender, member_lang, member_email;
    EditText edt_name, edt_phone, edt_email, edt_language;
    RadioGroup gender_group;
    RadioButton rb_male, rb_female;
    Button btn_done;
    private ProgressDialog pDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_buyer, container, false);
        findView(rootView);
        setListener();
        setView();
        return rootView;
    }

    private void setView() {
        member_id = SessionManager.currMember.getMember_id();
        edt_name.setText(SessionManager.currMember.getMember_name());
        edt_phone.setText(SessionManager.currMember.getMember_mobile());
        if (!SessionManager.currMember.getMember_email().equals("null")) {
            edt_email.setText(SessionManager.currMember.getMember_email());
        }
        if (!SessionManager.currMember.getMember_language().equals("null")) {
            edt_language.setText(SessionManager.currMember.getMember_language());
        }

        member_gender = SessionManager.currMember.getMember_gender().trim();
        Log.v(TAG, "Member_gender:" + member_gender);
        if (member_gender.equals("M")) {
            rb_male.setChecked(true);
        } else if (member_gender.equals("F")) {
            rb_female.setChecked(true);
        } else {
            rb_male.setChecked(true);//default
            member_gender = "M";
        }
    }

    private void setListener() {
        gender_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rb_male:
                        member_gender = "M";
                        break;
                    case R.id.rb_female:
                        member_gender = "F";
                        break;
                }
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMember();

            }
        });
    }

    private void updateMember() {
        String tag_string_req = "req_update";

        pDialog.setMessage("Updating ...");
        showDialog();

        member_email = edt_email.getText().toString();
        member_lang = edt_language.getText().toString();
        member_name = edt_name.getText().toString();

        Log.v(TAG, "name: " + member_name);
        Log.v(TAG, "email: " + member_email);
        Log.v(TAG, "lang: " + member_lang);
        Log.v(TAG, "gender: " + member_gender);

        String URL = AppConfig.URL_UPDATE_MEMBER + member_id;
        Log.v(TAG, "URL: " + URL);
        ;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {

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
                        Toast.makeText(getActivity(), "Your profile has been updated successfully!", Toast.LENGTH_LONG).show();

                        // Launch Login activity
                        Intent intent = new Intent(
                                getActivity(), MainActivity.class);
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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", member_name);
                params.put("gender", member_gender);
                if (!member_lang.equals("")) {
                    params.put("language", member_lang);
                }
                if (!member_email.equals("")) {
                    params.put("email", member_email);
                }
//                params.put("name", "test");
//                params.put("gender", "M");
//                params.put("language", "chinese");
//                params.put("email", "zengtian006@gmail.com");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + SessionManager.temptoken);
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void findView(View view) {
        edt_name = (EditText) view.findViewById(R.id.input_name_setting);
        edt_phone = (EditText) view.findViewById(R.id.input_mobile_setting);
        edt_email = (EditText) view.findViewById(R.id.input_email_setting);
        edt_language = (EditText) view.findViewById(R.id.input_lang_setting);
        gender_group = (RadioGroup) view.findViewById((R.id.gender_group));
        rb_male = (RadioButton) view.findViewById(R.id.rb_male);
        rb_female = (RadioButton) view.findViewById(R.id.rb_female);
        btn_done = (Button) view.findViewById(R.id.btn_done);
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
