package tp.ve.com.tradingplatform.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tp.ve.com.tradingplatform.R;

/**
 * Created by Zeng on 2015/11/3.
 */
public class SignupPhone2Fragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup_phone_2, container, false);


        // Inflate the layout for this fragment
        return rootView;
    }
}
