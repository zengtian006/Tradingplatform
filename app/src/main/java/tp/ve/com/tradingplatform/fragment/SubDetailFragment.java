package tp.ve.com.tradingplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tp.ve.com.tradingplatform.R;

/**
 * Created by Zeng on 2015/11/26.
 */
public class SubDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_detail, container, false);
        return rootView;
    }
}
