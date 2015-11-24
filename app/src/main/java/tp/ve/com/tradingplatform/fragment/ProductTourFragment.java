package tp.ve.com.tradingplatform.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.SignupActivity;

public class ProductTourFragment extends Fragment {

    final static String LAYOUT_ID = "layoutid";

    public static ProductTourFragment newInstance(int layoutId) {
        ProductTourFragment pane = new ProductTourFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("Tour", "No: " + getArguments().getInt(LAYOUT_ID, -1));
        ViewGroup rootView = (ViewGroup) inflater.inflate(getArguments().getInt(LAYOUT_ID, -1), container, false);
        if (getArguments().getInt(LAYOUT_ID, -1) == R.layout.welcome_fragment4) {
            Button btn_mobile = (Button) rootView.findViewById(R.id.Rgst_Mobile);
            btn_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SignupActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            Button btn_email = (Button) rootView.findViewById(R.id.Rgst_Email);
            btn_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignupActivity.initView = 2;
                    Intent intent = new Intent(getActivity(), SignupActivity.class);
                    startActivity(intent);

                    getActivity().finish();
                }
            });
        }
        return rootView;
    }
}