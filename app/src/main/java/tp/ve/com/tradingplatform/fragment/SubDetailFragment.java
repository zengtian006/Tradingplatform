package tp.ve.com.tradingplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tp.ve.com.tradingplatform.R;

/**
 * Created by Zeng on 2015/11/26.
 */
public class SubDetailFragment extends Fragment {
    Button btn_back;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_detail, container, false);
        findView(rootView);
        setListner();
        return rootView;
    }

    private void setListner() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareListFragment.viewPager.setCurrentItem(0);
            }
        });
    }

    private void findView(View rootView) {
        btn_back = (Button) rootView.findViewById(R.id.btn_back);
    }
}
