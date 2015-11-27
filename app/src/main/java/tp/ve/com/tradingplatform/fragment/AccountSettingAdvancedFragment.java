package tp.ve.com.tradingplatform.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.AccountSettingActivity;
import tp.ve.com.tradingplatform.activity.MainActivity;
import tp.ve.com.tradingplatform.activity.SignupActivity;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.helper.SessionManager;
import tp.ve.com.tradingplatform.utils.RealPathUtil;

/**
 * Created by Zeng on 2015/11/18.
 */
public class AccountSettingAdvancedFragment extends Fragment {
    private static final String TAG = AccountSettingAdvancedFragment.class.getSimpleName();

    private static final int CAPTURE_IMAGE_REQUEST_CODE_ID = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_BIZ = 200;
    Button btnUploadID, btnUploadBizLicense, btnUploadAd, btnDone;
    public static ImageView idImgPreview, BizLicenseImgPreview;
    private ProgressDialog pDialog;
    private String member_id;
    LinearLayout layout_compay, layout_individual;
    RadioGroup type_group;
    EditText edt_name, edt_phone, edt_email;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_advanced, container, false);
        findView(rootView);
        setListener();
        setView();
        return rootView;
    }

    private void setView() {
        layout_individual.setVisibility(LinearLayout.GONE);
        if (AccountSettingActivity.from_string.equals("Supplier")) {
            type_group.setVisibility(RadioGroup.GONE);
        } else {

            edt_name.setText(SessionManager.currMember.getMember_name());
            edt_phone.setText(SessionManager.currMember.getMember_mobile());
            if (!SessionManager.currMember.getMember_email().equals("null")) {
                edt_email.setText(SessionManager.currMember.getMember_email());
            }
        }
    }

    private void setListener() {
        btnUploadID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_ID);
            }
        });

        btnUploadBizLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_BIZ);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMember();
            }
        });

        type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rb_company:
                        layout_individual.setVisibility(LinearLayout.GONE);
                        layout_compay.setVisibility(LinearLayout.VISIBLE);
                        break;
                    case R.id.rb_individual:
                        layout_individual.setVisibility(LinearLayout.VISIBLE);
                        layout_compay.setVisibility(LinearLayout.GONE);
                        break;
                }
            }
        });

    }

    private void findView(View view) {
        member_id = SessionManager.currMember.getMember_id();
        btnUploadID = (Button) view.findViewById(R.id.btn_uploadID);
        btnUploadBizLicense = (Button) view.findViewById(R.id.btn_uploadBizLicense);
        btnUploadAd = (Button) view.findViewById(R.id.btn_uploadAd);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        idImgPreview = (ImageView) view.findViewById(R.id.idImgPreview);
        BizLicenseImgPreview = (ImageView) view.findViewById(R.id.BizLicenseImgPreview);
        layout_compay = (LinearLayout) view.findViewById(R.id.layout_company);
        layout_individual = (LinearLayout) view.findViewById(R.id.layout_individual);
        type_group = (RadioGroup) view.findViewById((R.id.type_group));
        edt_name = (EditText) view.findViewById(R.id.input_name_setting);
        edt_phone = (EditText) view.findViewById(R.id.input_mobile_setting);
        edt_email = (EditText) view.findViewById(R.id.input_email_setting);
    }

    private void openImageIntent(int requestcode) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AppConfig.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + AppConfig.IMAGE_DIRECTORY_NAME + " directory");
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File imageFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        AccountSettingActivity.outputFileUri = Uri.fromFile(imageFile);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, AccountSettingActivity.outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        getActivity().startActivityForResult(chooserIntent, requestcode);
    }

    private void updateMember() {
        String tag_string_req = "req_update";

        pDialog.setMessage("Updating ...");
        showDialog();

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
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
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
                params.put(AccountSettingActivity.from_string.toLowerCase() + "_verified", "A");
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
