package tp.ve.com.tradingplatform.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.MainActivity;
import tp.ve.com.tradingplatform.activity.ShareActivity;
import tp.ve.com.tradingplatform.activity.ShareDialog;
import tp.ve.com.tradingplatform.utils.RealPathUtil;

/**
 * Created by Zeng on 2015/11/17.
 */
public class SubContentFragment extends Fragment {
    private final static String TAG = SubContentFragment.class.getSimpleName();
    Button btn_back, btn_share;
    public static TextView tv_title;
    public static ImageView s_img;
    public static EditText custom_text;
    ShareDialog shareDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareActivity.pDialog = new ProgressDialog(getActivity());
        ShareActivity.pDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sub_content, container, false);
        findView(rootView);
        setListener();
        return rootView;
    }

    private void setListener() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareItemFragment.viewPager.setCurrentItem(0);
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "local image path: " + ShareActivity.loaclImgPath);
                shareDialog = new ShareDialog(getActivity());
                shareDialog.setCancelButtonOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        shareDialog.dismiss();

                    }
                });
                shareDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Boolean hasImg = false;
                        if (s_img.getTag().toString().equals("full")) {
                            hasImg = true;
                        }

                        ShareActivity.pDialog.setMessage("Please wait ...");
                        ShareActivity.pDialog.show();
                        shareDialog.dismiss();
                        HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
                        Uri bmpUri = RealPathUtil.getLocalBitmapUri(getActivity(), s_img);
                        Log.v(TAG, "URL:" + bmpUri);
                        Log.v(TAG, "Path: " + bmpUri.getPath());
//                        Uri bmpUri = null;
                        if (item.get("ItemText").equals("Whatsapp")) {
                            ShareActivity.pDialog.dismiss();

                            if (bmpUri != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);

                                shareIntent.setPackage("com.whatsapp");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, tv_title.getText().toString() + "\n" + custom_text.getText().toString());
                                if (hasImg) {
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                }
                                shareIntent.setType("image/*");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                try {
                                    getActivity().startActivity(shareIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (item.get("ItemText").equals("Weibo")) {

                            //2、设置分享内容
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setText(tv_title.getText().toString() + "\n" + custom_text.getText().toString()); //分享文本
                            if (hasImg) {
//                            sp.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul
                            }
                            //3、非常重要：获取平台对象
                            Platform sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                            sinaWeibo.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            sinaWeibo.share(sp);
                        } else if (item.get("ItemText").equals("Wechat friend")) {
                            ShareActivity.pDialog.dismiss();
                            //2、设置分享内容
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE);//非常重要：一定要设置分享属性
                            sp.setTitle(tv_title.getText().toString());  //分享标题
                            sp.setText(custom_text.getText().toString());   //分享文本
                            if (hasImg) {
                                if (ShareActivity.loaclImgPath != "") {
                                    sp.setImagePath(ShareActivity.loaclImgPath);
                                } else {
                                    sp.setImageUrl(SubURLFragment.img);
                                }
                            }
//                            try {
//                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), bmpUri);
//                                sp.setImageData(bitmap);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }

//                            sp.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul
                            sp.setUrl(SubURLFragment.edt_url.getText().toString());   //网友点进链接后，可以看到分享的详情

                            //3、非常重要：获取平台对象
                            Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                            wechat.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            wechat.share(sp);

                        } else if (item.get("ItemText").equals("Friend circle")) {
                            ShareActivity.pDialog.dismiss();
                            //2、设置分享内容
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
                            sp.setTitle(tv_title.getText().toString());  //分享标题
                            sp.setText(custom_text.getText().toString());   //分享文本
                            if (hasImg) {
                                if (ShareActivity.loaclImgPath != "") {
                                    sp.setImagePath(ShareActivity.loaclImgPath);
                                } else {
                                    sp.setImageUrl(SubURLFragment.img);
                                }
                            }
//                            sp.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul
                            sp.setUrl(SubURLFragment.edt_url.getText().toString());   //网友点进链接后，可以看到分享的详情
                            //3、非常重要：获取平台对象
                            Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
                            wechatMoments.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            wechatMoments.share(sp);
                        } else if (item.get("ItemText").equals("Facebook")) {
                            //2、设置分享内容
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
                            sp.setText(tv_title.getText().toString() + "\n" + custom_text.getText().toString());   //分享文本
                            if (hasImg) {
                                if (ShareActivity.loaclImgPath != "") {
                                    sp.setImagePath(ShareActivity.loaclImgPath);
                                } else {
                                    sp.setImageUrl(SubURLFragment.img);
                                }
                            }
//                            sp.setImageUrl("http://wiki.mob.com/wp-content/uploads/2014/09/ssdk_qig_qi_win.png");//网络图片rul
//                            sp.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul
                            //3、非常重要：获取平台对象
                            Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
                            facebook.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            facebook.share(sp);
                        } else if (item.get("ItemText").equals("Twitter")) {
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
                            sp.setText(tv_title.getText().toString() + "\n" + custom_text.getText().toString());   //分享文本
                            if (hasImg) {
                                if (ShareActivity.loaclImgPath != "") {
                                    sp.setImagePath(ShareActivity.loaclImgPath);
                                } else {
                                    sp.setImageUrl(SubURLFragment.img);
                                }
                            }
//                            sp.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul
                            //3、非常重要：获取平台对象
                            Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
                            twitter.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            twitter.share(sp);
                        }

                    }
                });
            }
        });
    }

    private void findView(View rootView) {
        btn_back = (Button) rootView.findViewById(R.id.btn_back);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        s_img = (ImageView) rootView.findViewById(R.id.share_img);
        btn_share = (Button) rootView.findViewById(R.id.btn_share);
        custom_text = (EditText) rootView.findViewById(R.id.edit_custom_content);
    }
}
