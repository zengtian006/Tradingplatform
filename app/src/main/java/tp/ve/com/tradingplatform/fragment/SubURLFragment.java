package tp.ve.com.tradingplatform.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.ShareActivity;

/**
 * Created by Zeng on 2015/11/17.
 */
public class SubURLFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sub_url, container, false);
        EditText edt_url = (EditText) rootView.findViewById(R.id.urlText);
        edt_url.setText(ShareActivity.urlString);
        Button ttbtn = (Button) rootView.findViewById(R.id.ttbutton);
        ttbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareItemFragment.viewPager.setCurrentItem(1);

            }
        });
        return rootView;
    }
}
