package tp.ve.com.tradingplatform.activity;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.component.MultiSelectionSpinner;
import tp.ve.com.tradingplatform.utils.RealPathUtil;

/**
 * Created by Zeng on 2015/11/10.
 */
public class AccountSettingActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE_REQUEST_CODE_ID = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_BIZ = 200;
    private static final String TAG = AccountSettingActivity.class.getSimpleName();
    private MultiSelectionSpinner multiSelectionSpinner;
    public static LinearLayout linearLayout_advanced;
    public static ScrollView scrollView;
    private ProgressDialog pDialog;
    Button btnUploadID, btnUploadeBizLicense, btnUpload;
    ImageView idImgPreview, BizLicenseImgPreview;
    String member_id, member_name, member_mobile, member_gender, member_lang, member_email;
    EditText edt_name, edt_phone, edt_email, edt_language;
    RadioGroup gender_group;
    RadioButton rb_male;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);

        pDialog = new ProgressDialog(AccountSettingActivity.this);
        pDialog.setCancelable(false);

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.roleSpinner);
        linearLayout_advanced = (LinearLayout) findViewById(R.id.advanced_view);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        btnUploadID = (Button) findViewById(R.id.btn_uploadID);
        btnUploadeBizLicense = (Button) findViewById(R.id.btn_uploadBizLicense);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        idImgPreview = (ImageView) findViewById(R.id.idImgPreview);
        BizLicenseImgPreview = (ImageView) findViewById(R.id.BizLicenseImgPreview);
        edt_name = (EditText) findViewById(R.id.input_name_setting);
        edt_phone = (EditText) findViewById(R.id.input_mobile_setting);
        edt_email = (EditText) findViewById(R.id.input_email_setting);
        edt_language = (EditText) findViewById(R.id.input_lang_setting);
        gender_group = (RadioGroup) findViewById((R.id.gender_group));
        rb_male = (RadioButton) findViewById(R.id.rb_male);

        rb_male.setChecked(true);
        member_gender = "M";

        gender_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int p = group.indexOfChild((RadioButton) findViewById(checkedId));
                int count = group.getChildCount();
                switch (checkedId) {
                    case R.id.rb_male:
                        member_gender = "M";
                        break;
                    case R.id.rb_female:
                        member_gender = "F";
                        break;
                }
            }
        });


        Intent intent = getIntent();
        member_id = intent.getStringExtra("id");
        member_name = intent.getStringExtra("name");
        member_mobile = intent.getStringExtra("contact_no");

        Log.v(TAG, "id: " + member_id);
        Log.v(TAG, "name: " + member_name);
        Log.v(TAG, "contact_no: " + member_mobile);

        edt_name.setText(member_name.toString());
        edt_phone.setText(member_mobile.toString());

        btnUploadID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_ID);
            }
        });

        btnUploadeBizLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(CAPTURE_IMAGE_REQUEST_CODE_BIZ);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        String[] array = {"Buyer", "ITP", "Supplier"};
        multiSelectionSpinner.setItems(array);
        multiSelectionSpinner.setSelection(new int[]{0});

        linearLayout_advanced.setVisibility(LinearLayout.GONE);
        Log.v(TAG, "allChechedPositon:" + multiSelectionSpinner.checkItemPostion.toString());
    }

    public void uploadImage() {
        Log.v(TAG, "String is Here: " + outputFileUri.getPath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        Bitmap myImg = BitmapFactory.decodeFile(outputFileUri.getPath());
        myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        final String encodedString = Base64.encodeToString(byte_arr, 0);


        RequestQueue rq = Volley.newRequestQueue(this);
        String url = "http:/192.168.0.4/upload_image.php";
        Log.d("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.e("RESPONSE", response);
                    JSONObject json = new JSONObject(response);

                    Toast.makeText(getBaseContext(),
                            "The image is upload", Toast.LENGTH_SHORT)
                            .show();

                } catch (JSONException e) {
                    Log.d("JSON Exception", e.toString());
                    Toast.makeText(getBaseContext(),
                            "Error while loadin data!",
                            Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Error [" + error + "]");
                Toast.makeText(getBaseContext(),
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("image", encodedString);
                return params;
            }
        };
        rq.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_account_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.ic_account_update:
                updateMember();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMember() {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        pDialog.setMessage("Updating ...");
        showDialog();

        member_email = edt_email.getText().toString();
        member_lang = edt_language.getText().toString();
        member_name = edt_name.getText().toString();

        Log.v(TAG, "name: " + member_name);
        Log.v(TAG, "email: " + member_email);
        Log.v(TAG, "lang: " + member_lang);
        Log.v(TAG, "gender: " + member_gender);

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
                        Toast.makeText(AccountSettingActivity.this, "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch Login activity
                        Intent intent = new Intent(
                                AccountSettingActivity.this, LoginActivity.class);
                        startActivity(intent);
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
                Toast.makeText(SignupActivity.context,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", member_name);
                params.put("gender", member_gender);
                params.put("language", member_lang);
                params.put("email", member_email);
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
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_ID) {//Upload ID
                Uri selectedImageUri;
                if (data.getData() == null) { //isCamera
                    selectedImageUri = outputFileUri;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                    idImgPreview.setVisibility(ImageView.VISIBLE);
                    idImgPreview.setImageBitmap(bitmap);
                } else {
                    selectedImageUri = data.getData();
                    outputFileUri = selectedImageUri;
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    idImgPreview.setVisibility(ImageView.VISIBLE);
                    idImgPreview.setImageBitmap(selectedImage);
                }
                Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
                Log.v(TAG, "Real Path: " + RealPathUtil.getRealPathFromURI_API19(this, data.getData()));
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_BIZ) {//Upload BL
                Uri selectedImageUri;
                Log.v(TAG, "getData" + String.valueOf(data.getData()));
                if (data.getData() == null) {
                    selectedImageUri = outputFileUri;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                    BizLicenseImgPreview.setVisibility(ImageView.VISIBLE);
                    BizLicenseImgPreview.setImageBitmap(bitmap);
                } else {
                    selectedImageUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    BizLicenseImgPreview.setVisibility(ImageView.VISIBLE);
                    BizLicenseImgPreview.setImageBitmap(selectedImage);
                }
                Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
            }
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    AccountSettingActivity.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    private Uri outputFileUri;

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


//        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
//        root.mkdirs();
//        final String fname = new SimpleDateFormat("yyyyMMdd_HHmmss",
//                Locale.getDefault()).format(new Date());
//        final File sdImageMainDirectory = new File(root, fname + ".jpg");
        outputFileUri = Uri.fromFile(imageFile);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
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

        startActivityForResult(chooserIntent, requestcode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", outputFileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        outputFileUri = savedInstanceState.getParcelable("file_uri");
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
