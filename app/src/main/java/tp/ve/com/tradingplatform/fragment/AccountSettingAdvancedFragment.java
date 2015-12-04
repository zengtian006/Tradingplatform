package tp.ve.com.tradingplatform.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.helper.SessionManager;

/**
 * Created by Zeng on 2015/11/18.
 */
public class AccountSettingAdvancedFragment extends Fragment {
    private static final String TAG = AccountSettingAdvancedFragment.class.getSimpleName();

    private static final int CAPTURE_IMAGE_REQUEST_CODE_ID = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_BIZ = 200;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_ADD = 300;
    Button btnUploadID, btnUploadBizLicense, btnUploadAd, btnDone;
    public static ImageView idImgPreview, BizLicenseImgPreview, addImgPreview;
    public static RelativeLayout idImgPreview_layout, BizLicenseImgPreview_layout, addImgPreview_layout;
    public static Button del_id, del_add, del_biz;
    private ProgressDialog pDialog;
    private String member_id;
    LinearLayout layout_compay, layout_individual;
    RadioGroup type_group;
    EditText edt_name, edt_phone, edt_email, edt_com_name, edt_com_desc, edt_com_website, edt_office_phome, edt_com_product;
    public static LinearLayout img_gallery;


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
        Log.v(TAG, "From String: " + AccountSettingActivity.from_string);
        layout_individual.setVisibility(LinearLayout.GONE);
        if (AccountSettingActivity.from_string.equals("Supplier")) {
            type_group.setVisibility(RadioGroup.GONE);
        } else if (AccountSettingActivity.from_string.equals("ITP")) {
            edt_name.setText(SessionManager.currMember.getMember_name());
            edt_phone.setText(SessionManager.currMember.getMember_mobile());
            if (!SessionManager.currMember.getMember_email().equals("null")) {
                edt_email.setText(SessionManager.currMember.getMember_email());
            }
        }
        idImgPreview.setTag("empty");
        addImgPreview.setTag("empty");
        BizLicenseImgPreview.setTag("empty");
        del_biz.setVisibility(Button.GONE);
        del_id.setVisibility(Button.GONE);
        del_add.setVisibility(Button.GONE);
    }

    private void setListener() {
        idImgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idImgPreview.getTag().toString().equals("empty")) {
                    openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_ID);
                }
            }
        });

        BizLicenseImgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BizLicenseImgPreview.getTag().toString().equals("empty")) {
                    openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_BIZ);
                }
            }
        });

        addImgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addImgPreview.getTag().toString().equals("empty")) {
                    openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_ADD);
                }
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
                        BizLicenseImgPreview_layout.setVisibility(RelativeLayout.VISIBLE);
                        break;
                    case R.id.rb_individual:
                        layout_individual.setVisibility(LinearLayout.VISIBLE);
                        layout_compay.setVisibility(LinearLayout.GONE);
                        BizLicenseImgPreview_layout.setVisibility(RelativeLayout.GONE);
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
        addImgPreview = (ImageView) view.findViewById(R.id.addImgPreview);
        idImgPreview_layout = (RelativeLayout) view.findViewById(R.id.idImgPreview_layout);
        BizLicenseImgPreview_layout = (RelativeLayout) view.findViewById(R.id.BizLicenseImgPreview_layout);
        addImgPreview_layout = (RelativeLayout) view.findViewById(R.id.addImgPreview_layout);
        layout_compay = (LinearLayout) view.findViewById(R.id.layout_company);
        layout_individual = (LinearLayout) view.findViewById(R.id.layout_individual);
        type_group = (RadioGroup) view.findViewById((R.id.type_group));
        edt_name = (EditText) view.findViewById(R.id.input_name_setting);
        edt_phone = (EditText) view.findViewById(R.id.input_mobile_setting);
        edt_email = (EditText) view.findViewById(R.id.input_email_setting);
        img_gallery = (LinearLayout) view.findViewById(R.id.img_gallery);
        del_id = (Button) view.findViewById(R.id.del_id);
        del_add = (Button) view.findViewById(R.id.del_add);
        del_biz = (Button) view.findViewById(R.id.del_biz);
        edt_com_name = (EditText) view.findViewById(R.id.input_comName);
        edt_com_desc = (EditText) view.findViewById(R.id.input_comProfile);
        edt_com_website = (EditText) view.findViewById(R.id.input_comWebsite);
        edt_office_phome = (EditText) view.findViewById(R.id.input_comPhone);
        edt_com_product = (EditText) view.findViewById(R.id.input_comProduct);
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
                Log.d(TAG, "Update Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = Boolean.valueOf(jObj.getString("success"));
                    if (success) {
                        Toast.makeText(getActivity(), "Your application has been received and is being processed!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(
                                getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
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
                Map<String, String> params = new HashMap<String, String>();
                String role = AccountSettingActivity.from_string;
                params.put(role.toLowerCase() + "_verified", "A");
                params.put("role", role);
                if (role.equals("ITP")) {
                    if (type_group.getCheckedRadioButtonId() == R.id.rb_company) {
                        params.put("type", "C");
                        params.put("company_name", edt_com_name.getText().toString());
                        params.put("company_desc", edt_com_desc.getText().toString());
                        params.put("company_website", edt_com_website.getText().toString());
                        params.put("office_phone", edt_office_phome.getText().toString());
                        params.put("company_product", edt_com_product.getText().toString());
                    } else if (type_group.getCheckedRadioButtonId() == R.id.rb_individual) {
                        params.put("type", "I");
                        params.put("full_name", edt_name.getText().toString());
                        params.put("mobile", edt_phone.getText().toString());
                        params.put("email", edt_email.getText().toString());
                    }
                } else if (role.equals("Supplier")) {
                    params.put("company_name", edt_com_name.getText().toString());
                    params.put("company_desc", edt_com_desc.getText().toString());
                    params.put("company_website", edt_com_website.getText().toString());
                    params.put("office_phone", edt_office_phome.getText().toString());
                    params.put("company_product", edt_com_product.getText().toString());
                }
                Log.v(TAG, "Paras: " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + SessionManager.temptoken);
                return headers;
            }
        };
        AppController.getInstance().

                addToRequestQueue(strReq, tag_string_req);
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
