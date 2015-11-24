package tp.ve.com.tradingplatform.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tp.ve.com.tradingplatform.component.CustomViewPager;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.fragment.AccountSettingAdvancedFragment;
import tp.ve.com.tradingplatform.fragment.AccountSettingBuyerFragment;
import tp.ve.com.tradingplatform.fragment.AccountSettingRoleFragment;
import tp.ve.com.tradingplatform.utils.RealPathUtil;

/**
 * Created by Zeng on 2015/11/18.
 */
public class AccountSettingActivity extends AppCompatActivity {
    private final static String TAG = AccountSettingActivity.class.getSimpleName();

    private static final int CAPTURE_IMAGE_REQUEST_CODE_ID = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE_BIZ = 200;
    public static Uri outputFileUri;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static CustomViewPager viewPager;
    public static String from_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        //forbid gesture on tabcontrol
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
            if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_ID) {//Upload ID
                Uri selectedImageUri;
                Bitmap bitmap;
                if (data.getData() == null) { //isCamera
                    selectedImageUri = outputFileUri;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
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
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    Log.v(TAG, "Real Path: " + RealPathUtil.getRealPathFromURI_API19(this, data.getData()));
                }
                AccountSettingAdvancedFragment.idImgPreview.setVisibility(ImageView.VISIBLE);
                AccountSettingAdvancedFragment.idImgPreview.setImageBitmap(bitmap);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE_BIZ) {//Upload BL
                Uri selectedImageUri;
                Log.v(TAG, "getData" + String.valueOf(data.getData()));
                if (data.getData() == null) {
                    selectedImageUri = outputFileUri;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
//                    BizLicenseImgPreview.setVisibility(ImageView.VISIBLE);
//                    BizLicenseImgPreview.setImageBitmap(bitmap);
                } else {
                    selectedImageUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                    BizLicenseImgPreview.setVisibility(ImageView.VISIBLE);
//                    BizLicenseImgPreview.setImageBitmap(selectedImage);
                }
                Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
            }
        }
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
}
