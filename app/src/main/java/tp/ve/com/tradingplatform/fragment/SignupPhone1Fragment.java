package tp.ve.com.tradingplatform.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.LoginActivity;
import tp.ve.com.tradingplatform.activity.SignupActivity;

import static cn.smssdk.framework.utils.R.getRawRes;
import static cn.smssdk.framework.utils.R.getStringRes;

/**
 * Created by Zeng on 2015/11/3.
 */
public class SignupPhone1Fragment extends Fragment {
    Spinner phoneAreaSP;
    TextView phoneCode;
    Button btnLinkToLogin;
    EditText edt_Phone;
    TextInputLayout inputLayoutPhone;

    String phCode;
    String phString;
    //    SMS information
    private static String APPKEY = "92fe0af33ee2";
    private static String APPSECRET = "025d167cf82e598ab470fe65acda95ea";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SMS start
        SMSSDK.initSDK(getContext(), APPKEY, APPSECRET);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
        //SMS end

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signup_phone_1, container, false);
        final Button verifyBtn = (Button) rootView.findViewById(R.id.btn_verify);
        edt_Phone = (EditText) rootView.findViewById(R.id.input_phone);
        phoneCode = (TextView) rootView.findViewById(R.id.phone_code);
        phoneAreaSP = (Spinner) rootView.findViewById(R.id.phone_area);
        btnLinkToLogin = (Button) rootView.findViewById(R.id.btnLinkToLoginScreen);
        inputLayoutPhone = (TextInputLayout) rootView.findViewById(R.id.input_layout_phone);

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(),
                        LoginActivity.class);
                startActivity(i);
            }
        });


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                check phone number

                phString = edt_Phone.getText().toString();
                if (Patterns.PHONE.matcher(phString).matches() && !phString.isEmpty()) {
                    //SMS start
                    phCode = phoneCode.getText().toString().substring(1);
                    SMSSDK.getVerificationCode(phCode, phString);
                    //SMS end

                    LinearLayout verifyLayout = (LinearLayout) rootView.findViewById(R.id.verify_layout);
                    LinearLayout buttonLayout = (LinearLayout) rootView.findViewById(R.id.button_layout);
                    Button confirmBtn = (Button) rootView.findViewById(R.id.btn_confirm);
                    final EditText edt_verifycode = (EditText) rootView.findViewById(R.id.input_verify_code);
                    verifyLayout.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                    verifyLayout.setVisibility(LinearLayout.VISIBLE);
                    buttonLayout.setVisibility(LinearLayout.GONE);

//                    final EditText edt_verifycode = new EditText(getContext());
//                    edt_verifycode.setHint("Verification code");
//                    edt_verifycode.setInputType(InputType.TYPE_CLASS_NUMBER);
//                    edt_verifycode.setSingleLine(true);
//                    edt_verifycode.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                            LayoutParams.WRAP_CONTENT));
//
//                    edt_verifycode.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
//                    verifyLayout.addView(edt_verifycode);
//
//                    Button confirmBtn = new Button(getContext());
//                    confirmBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
//                    confirmBtn.setText("Verify");
//                    confirmBtn.setTextColor(Color.WHITE);
//
//                    confirmBtn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                            LayoutParams.WRAP_CONTENT));
//                    verifyLayout.addView(confirmBtn);
////                    buttonLayout.addView(confirmBtn);
//                    buttonLayout.removeView(verifyBtn);

                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!edt_verifycode.getText().toString().isEmpty()) {
                                SMSSDK.submitVerificationCode(phCode, phString, edt_verifycode.getText().toString());
                            }
                        }
                    });

                } else {
                    inputLayoutPhone.setError(getString(R.string.err_msg_phone));
                }
            }
        });

        phoneAreaSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                phoneCode.setText("+" + SignupPhone1Fragment.this.getResources().getStringArray(R.array.phone_codes)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    //SMS start
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
                    SignupActivity.phoneNo = phString;
                    Fragment fragment = new SignupPhone2Fragment();

                    if (fragment != null) {
                        FragmentManager fragmentManager = SignupActivity.fragmentManager;
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.commit();
                    }


                    Toast.makeText(SignupActivity.context, "Success", Toast.LENGTH_SHORT).show();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(SignupActivity.context, "Verification code has been sent", Toast.LENGTH_SHORT).show();

//                    Snackbar snackbar = Snackbar
//                            .make(SignupActivity.coordinatorLayout, "Verification code has been sent", Snackbar.LENGTH_LONG);
//
//                    snackbar.show();
                }
            } else {
                ((Throwable) data).printStackTrace();
                int resId = getStringRes(SignupActivity.context, "smssdk_network_error");
                Toast.makeText(SignupActivity.context, "Wrong verification code", Toast.LENGTH_SHORT).show();
                if (resId > 0) {
                    Toast.makeText(SignupActivity.context, resId, Toast.LENGTH_SHORT).show();
                }
            }

        }

    };
//SMS end
}
