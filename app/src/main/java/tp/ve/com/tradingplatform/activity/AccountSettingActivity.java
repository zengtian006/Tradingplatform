package tp.ve.com.tradingplatform.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.component.CustomViewPager;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.fragment.AccountSettingAdvancedFragment;
import tp.ve.com.tradingplatform.fragment.AccountSettingBuyerFragment;
import tp.ve.com.tradingplatform.fragment.AccountSettingRoleFragment;
import tp.ve.com.tradingplatform.helper.SessionManager;
import tp.ve.com.tradingplatform.utils.ImageUtil;

/**
 * Created by Zeng on 2015/11/18.
 */
public class AccountSettingActivity extends AppCompatActivity {
    private final static String TAG = AccountSettingActivity.class.getSimpleName();

    private static final int CAPTURE_IMAGE_REQUEST_CODE_ID = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_BIZ = 200;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_ADD = 300;
    public static Uri outputFileUri;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static CustomViewPager viewPager;
    public static String from_string;
    public static String encoded_ID_img, encoded_BL_img, encoded_ADD_img;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.v(TAG, "selected page:" + position);
                if (position == 2) {
                    setTitle(from_string + " Setting");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                if (position == 1) {
                    setTitle("Buyer Setting");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
                if (position == 0) {
                    setTitle("Account Setting");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(CustomViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AccountSettingRoleFragment(), "ROLE");
        adapter.addFragment(new AccountSettingBuyerFragment(), "BUYER");
        adapter.addFragment(new AccountSettingAdvancedFragment(), "ADVANCED");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_ID) {//Upload ID
                Uri selectedImageUri;
                Bitmap bitmap;

                if (data.getData() == null) { //isCamera
                    selectedImageUri = outputFileUri;
                    bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), opts);
                    Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
                } else {
                    selectedImageUri = data.getData();
                    outputFileUri = selectedImageUri;
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(imageStream, null, opts);
                    Log.v(TAG, "Real Path: " + ImageUtil.getRealPathFromURI_API19(this, data.getData()));
                }
//                AccountSettingAdvancedFragment.del_id.setVisibility(Button.VISIBLE);
                AccountSettingAdvancedFragment.idImgPreview.setImageBitmap(bitmap);
                encoded_ID_img = ImageUtil.bmpToBase64(bitmap);
                uploadImage("id", encoded_ID_img);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_BIZ) {//Upload BL
                Uri selectedImageUri;
                Log.v(TAG, "getData" + String.valueOf(data.getData()));
                Bitmap bitmap;
                if (data.getData() == null) {
                    selectedImageUri = outputFileUri;
                    bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), opts);
                    Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
                } else {
                    selectedImageUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(imageStream, null, opts);
                    Log.v(TAG, "Real Path: " + ImageUtil.getRealPathFromURI_API19(this, data.getData()));
                }
//                AccountSettingAdvancedFragment.del_biz.setVisibility(Button.VISIBLE);
                AccountSettingAdvancedFragment.BizLicenseImgPreview.setImageBitmap(bitmap);
                encoded_BL_img = ImageUtil.bmpToBase64(bitmap);
                uploadImage("biz", encoded_BL_img);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_ADD) {//Upload Address proof
                Uri selectedImageUri;
                Log.v(TAG, "getData" + String.valueOf(data.getData()));
                Bitmap bitmap;
                if (data.getData() == null) {
                    selectedImageUri = outputFileUri;
                    bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), opts);
                    Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
                } else {
                    selectedImageUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(imageStream, null, opts);
                    Log.v(TAG, "Real Path: " + ImageUtil.getRealPathFromURI_API19(this, data.getData()));
                }
//                AccountSettingAdvancedFragment.del_add.setVisibility(Button.VISIBLE);
                AccountSettingAdvancedFragment.addImgPreview.setImageBitmap(bitmap);
                encoded_ADD_img = ImageUtil.bmpToBase64(bitmap);
                uploadImage("address", encoded_ADD_img);
            }
            AccountSettingAdvancedFragment.img_gallery.setVisibility(LinearLayout.VISIBLE);
        }
    }

    private void uploadImage(final String tag, final String encodedString) {
        String tag_string_req = "req_update";
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Uploading ...");
        showDialog();

        String URL = AppConfig.URL_UPLOAD_IMG;
        Log.v(TAG, "URL: " + URL);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Upload Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = Boolean.valueOf(jObj.getString("success"));
                    if (success) {
                        Toast.makeText(AccountSettingActivity.this, "Upload successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(AccountSettingActivity.this,
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
                Toast.makeText(AccountSettingActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("member_id", SessionManager.currMember.getMember_id());
                params.put("tag", tag);
                params.put("image", encodedString);
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
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
        } else {
//            Intent intent = new Intent(AccountSettingActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
            super.onBackPressed();
        }
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
