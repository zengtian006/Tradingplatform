package tp.ve.com.tradingplatform.helper;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.AccountSettingActivity;
import tp.ve.com.tradingplatform.activity.MainActivity;
import tp.ve.com.tradingplatform.activity.SignupActivity;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.fragment.AccountSettingRoleFragment;

public abstract class RadioAdapter<T> extends RecyclerView.Adapter<RadioAdapter.ViewHolder> {

    private final static String TAG = "RadioAdapter.class";
    public int mSelectedItem = -1;
    public String mSelectedStatus = "Verified";
    public List<T> mItems;
    private Context mContext;
    private ProgressDialog pDialog;

    public RadioAdapter(Context context, List<T> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public void onBindViewHolder(RadioAdapter.ViewHolder viewHolder, final int i) {

        Log.v("TEST", "haha: " + viewHolder.mStatus.getText().toString().trim() + "   " + i);
        if (i == mSelectedItem && mSelectedStatus.equals("Verified")) {
            viewHolder.mRadio.setChecked(true);
        } else {
            viewHolder.mRadio.setChecked(false);
        }
        if (i == MainActivity.currentRole) {
            viewHolder.mRadio.setChecked(true);
        }
//        viewHolder.mRadio.setChecked(i == mSelectedItem);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.card_item, viewGroup, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public RadioButton mRadio;
        public TextView mText;
        public TextView mStatus;
        public ImageView mImage;

        public ViewHolder(final View inflate) {
            super(inflate);
            mText = (TextView) inflate.findViewById(R.id.text);
            mRadio = (RadioButton) inflate.findViewById(R.id.radio);
            mStatus = (TextView) inflate.findViewById(R.id.status);
            mImage = (ImageView) inflate.findViewById(R.id.img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    mSelectedStatus = mStatus.getText().toString().trim();

                    Log.v("test", "tititit:" + mText.getText().toString() + " " + mStatus.getText().toString() + " " + mSelectedItem);
                    String mRole = mText.getText().toString().trim();
//                    if (mSelectedStatus.equals("Not Applied")) {
                    if (mRole.equals("Buyer")) {
                        AccountSettingActivity.viewPager.setCurrentItem(1);
                    } else {
                        if (mSelectedStatus.equals("Not Applied")) {
                            AccountSettingActivity.from_string = mRole;
                            AccountSettingActivity.viewPager.setCurrentItem(2);
                        }
                    }
//                    }
                }
            });
            mRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    mSelectedStatus = mStatus.getText().toString().trim();
                    if (mSelectedStatus.equals("Verified")) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Do you want to change your role?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                Log.v("test", "YES");
                                                setMemberRole(mText.getText().toString());
                                                MainActivity.currentRole = mSelectedItem;
                                                notifyItemRangeChanged(0, mItems.size());
                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v("test", "No");
                                mRadio.setChecked(false);
                            }
                        }).create().show();
                    } else {
                        mRadio.setChecked(false);
                    }
                }
            });
        }
    }

    private void setMemberRole(String role) {
        String tag_string_req = "req_update";
        pDialog = new ProgressDialog(mContext);
        pDialog.setCancelable(false);
        pDialog.setMessage("Updating ...");
        showDialog();
        String member_id = SessionManager.currMember.getMember_id();
        String URL = AppConfig.URL_UPDATE_MEMBER + member_id;
        Log.v(TAG, "URL: " + URL);
        ;
        final String finalRole = role.substring(0, 1);
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
                        Toast.makeText(mContext, "Your role has been changed successfully!", Toast.LENGTH_LONG).show();

                        // Launch Login activity
                        Intent intent = new Intent(
                                mContext, MainActivity.class);
                        mContext.startActivity(intent);
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
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("default_role", finalRole);
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


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
