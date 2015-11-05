package tp.ve.com.tradingplatform.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.LoginActivity;
import tp.ve.com.tradingplatform.activity.SignupActivity;

/**
 * Created by Zeng on 2015/11/3.
 */
public class SignupPhone1Fragment extends Fragment {
    Spinner phoneAreaSP;
    TextView phoneCode;
    Button btnLinkToLogin;
    EditText edtPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signup_phone_1, container, false);
        final Button verifyBtn = (Button) rootView.findViewById(R.id.btn_verify);
        edtPhone = (EditText) rootView.findViewById(R.id.input_phone);
        phoneCode = (TextView) rootView.findViewById(R.id.phone_code);
        phoneAreaSP = (Spinner) rootView.findViewById(R.id.phone_area);
        btnLinkToLogin = (Button) rootView.findViewById(R.id.btnLinkToLoginScreen);

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

                LinearLayout verifyLayout = (LinearLayout) rootView.findViewById(R.id.verify_layout);
                EditText et = new EditText(getContext());
                et.setHint("Verification code");
                et.setSingleLine(true);
                et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));

                et.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                verifyLayout.addView(et);

                LinearLayout buttonLayout = (LinearLayout) rootView.findViewById(R.id.button_layout);

                Button confirmBtn = new Button(getContext());
                confirmBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                confirmBtn.setText("Verify");
                confirmBtn.setTextColor(Color.WHITE);

                confirmBtn.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                buttonLayout.addView(confirmBtn);
                buttonLayout.removeView(verifyBtn);

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = new SignupPhone2Fragment();

                        if (fragment != null) {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, fragment);
                            fragmentTransaction.commit();
                        }

                    }
                });


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
}
