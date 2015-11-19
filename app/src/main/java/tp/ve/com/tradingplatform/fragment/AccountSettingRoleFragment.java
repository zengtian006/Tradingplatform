package tp.ve.com.tradingplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.entity.Role;
import tp.ve.com.tradingplatform.helper.RoleAdapter;
import tp.ve.com.tradingplatform.helper.SessionManager;

/**
 * Created by Zeng on 2015/11/18.
 */
public class AccountSettingRoleFragment extends Fragment {
    private final static String TAG = AccountSettingRoleFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    List<Role> roles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_role, container, false);
        findViews(rootView);
        setRecyclerView();
        Log.v("AccountSetting Role: ", "onCreateView");
        return rootView;
    }

    private void setRecyclerView() {
        String itp_status = SessionManager.currMember.getMember_itp();
        String supplier_status = SessionManager.currMember.getMember_supplier();
        if (itp_status.equals("I")) {
            itp_status = "Applied";
        } else if (itp_status.equals("A")) {
            itp_status = "Verified";
        } else {
            itp_status = "Not Applied";
        }
        if (supplier_status.equals("I")) {
            supplier_status = "Applied";
        } else if (supplier_status.equals("A")) {
            supplier_status = "Verified";
        } else {
            supplier_status = "Not Applied";
        }


        roles = Arrays.asList(
                new Role("Buyer", "Verified", R.mipmap.buyer),
                new Role("ITP", itp_status, R.mipmap.itp),
                new Role("Supplier", supplier_status, R.mipmap.supplier));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new RoleAdapter(getActivity(), roles) {
        });
    }

    private void findViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
    }

    public static void setDefaultRole() {

    }


}
