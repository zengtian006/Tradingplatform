package tp.ve.com.tradingplatform.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.component.CustomViewPager;
import tp.ve.com.tradingplatform.fragment.AccountSettingAdvancedFragment;
import tp.ve.com.tradingplatform.fragment.ShareBlogFragment;
import tp.ve.com.tradingplatform.fragment.ShareItemFragment;
import tp.ve.com.tradingplatform.fragment.ShareListFragment;
import tp.ve.com.tradingplatform.fragment.SubContentFragment;
import tp.ve.com.tradingplatform.fragment.SubListFragment;
import tp.ve.com.tradingplatform.fragment.SubURLFragment;
import tp.ve.com.tradingplatform.utils.RealPathUtil;

/**
 * Created by Zeng on 2015/11/17.
 */
public class ShareActivity extends AppCompatActivity implements PlatformActionListener {
    private final static String TAG = ShareActivity.class.getSimpleName();

    private final static int SCANNED_GREENEST_CODE = 1;
    public static Uri outputFileUri;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static CustomViewPager viewPager;
    public static String urlString;
    public static AlertDialog img_dialog;
    public static ProgressDialog pDialog;
    public static String loaclImgPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ShareSDK.initSDK(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        Log.v(TAG, "Page 2 selected");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        Intent intent = getIntent();
        if (intent.getStringExtra("URL") != null) {
            urlString = intent.getStringExtra("URL");
        }
        if (intent.getStringExtra("currentpage") != null) {
            viewPager.setCurrentItem(Integer.valueOf(intent.getStringExtra("currentpage")));
        }
        Log.v(TAG, "fetch URL: " + urlString);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("SHARE");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("HISTORY");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("BLOG");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    /**
     * Adding fragments to ViewPager
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ShareItemFragment(), "Share");
        adapter.addFrag(new ShareListFragment(), "History");
        adapter.addFrag(new ShareBlogFragment(), "Blog");
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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.ic_scan:
                intent.setClass(ShareActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNED_GREENEST_CODE);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 200) {
                Uri selectedImageUri;
                Bitmap bitmap;
                if (data.getData() == null) { //isCamera
                    selectedImageUri = outputFileUri;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                    Log.v(TAG, "url!!!!!:" + String.valueOf(selectedImageUri.getPath()));
                    loaclImgPath = String.valueOf(selectedImageUri.getPath());
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
                    loaclImgPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                    Log.v(TAG, "Real Path: " + RealPathUtil.getRealPathFromURI_API19(this, data.getData()));
                }
                SubURLFragment.view_image.setImageBitmap(bitmap);
                SubURLFragment.view_image.setTag("full");
                SubURLFragment.btn_img_del.setVisibility(Button.VISIBLE);
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
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        if (platform.getName().equals(SinaWeibo.NAME)) {// 判断成功的平台是不是新浪微博
            handler.sendEmptyMessage(1);
        } else if (platform.getName().equals(Wechat.NAME)) {
            handler.sendEmptyMessage(1);
        } else if (platform.getName().equals(WechatMoments.NAME)) {
            handler.sendEmptyMessage(1);
        } else if (platform.getName().equals(Facebook.NAME)) {
            handler.sendEmptyMessage(1);
        } else if (platform.getName().equals(Twitter.NAME)) {
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        throwable.printStackTrace();
        Message msg = new Message();
        msg.what = 6;
        msg.obj = throwable.getMessage();
        handler.sendMessage(msg);

    }

    @Override
    public void onCancel(Platform platform, int i) {
        handler.sendEmptyMessage(2);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pDialog.dismiss();
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "Share successfully", Toast.LENGTH_LONG).show();
                    break;

                case 2:
                    Toast.makeText(getApplicationContext(), "Share canceled", Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    Log.v(TAG, "Share Error: " + msg.obj);
                    Toast.makeText(getApplicationContext(), "Share failed " + msg.obj, Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }
}
