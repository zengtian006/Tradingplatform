package tp.ve.com.tradingplatform.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.ShareDialog;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.entity.ShareContent;
import tp.ve.com.tradingplatform.helper.SessionManager;
import tp.ve.com.tradingplatform.utils.ImageUtil;

/**
 * Created by Zeng on 2015/11/26.
 */
public class SubDetailFragment extends Fragment {
    private final static String TAG = SubDetailFragment.class.getSimpleName();

    Button btn_back, btn_reshare;
    public static EditText edt_url, edt_title, edt_content, edt_id;
    public static NetworkImageView imageView;
    ShareDialog shareDialog;
    public static String img_url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_detail, container, false);
        findView(rootView);
        setListner();
        return rootView;
    }

    private void setListner() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareListFragment.viewPager.setCurrentItem(0);
            }
        });
        btn_reshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        ShareContent updateContent = new ShareContent();
                        updateContent.setsURL(edt_url.getText().toString());
                        updateContent.setsTitle(edt_title.getText().toString());
                        updateContent.setsContent(edt_content.getText().toString());
                        updateContent.setsId(edt_id.getText().toString());
//                        updateContent.setsImg_path(img_url);

                        HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
                        if (item.get("ItemText").equals("Whatsapp")) {
                            Uri bmpUri = ImageUtil.getLocalBitmapUri(getActivity(), imageView);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setPackage("com.whatsapp");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, edt_title.getText().toString() + "\n" + edt_content.getText().toString());
                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/*");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            try {
                                getActivity().startActivity(shareIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (item.get("ItemText").equals("Weibo")) {
                            //2、设置分享内容
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setText(edt_title.getText().toString() + "\n" + edt_content.getText().toString()); //分享文本

                            //3、非常重要：获取平台对象
                            Platform sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                            sinaWeibo.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            sinaWeibo.share(sp);
                        } else if (item.get("ItemText").equals("Wechat friend")) {
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE);//非常重要：一定要设置分享属性
                            sp.setTitle(edt_title.getText().toString());  //分享标题
                            sp.setText(edt_content.getText().toString());   //分享文本
                            sp.setImageUrl(img_url);
                            sp.setUrl(edt_url.getText().toString());   //点进链接后，可以看到分享的详情
                            //3、非常重要：获取平台对象
                            Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                            wechat.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            wechat.share(sp);
                        } else if (item.get("ItemText").equals("Friend circle")) {
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
                            sp.setTitle(edt_title.getText().toString());  //分享标题
                            sp.setText(edt_content.getText().toString());   //分享文本
                            sp.setImageUrl(img_url);
//                            sp.setImageUrl("http://7sby7r.com1.z0.glb.clouddn.com/CYSJ_02.jpg");//网络图片rul
                            sp.setUrl(edt_url.getText().toString());   //网友点进链接后，可以看到分享的详情
                            //3、非常重要：获取平台对象
                            Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
                            wechatMoments.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            wechatMoments.share(sp);
                        } else if (item.get("ItemText").equals("Facebook")) {
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
                            sp.setText(edt_title.getText().toString() + "\n" + edt_content.getText().toString());   //分享文本
                            sp.setImageUrl(img_url);
                            //3、非常重要：获取平台对象
                            Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
                            facebook.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            facebook.share(sp);
                        } else if (item.get("ItemText").equals("Twitter")) {
                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
                            sp.setText(edt_title.getText().toString() + "\n" + edt_content.getText().toString());   //分享文本
                            sp.setImageUrl(img_url);
                            //3、非常重要：获取平台对象
                            Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
                            twitter.setPlatformActionListener((PlatformActionListener) getActivity()); // 设置分享事件回调
                            // 执行分享
                            twitter.share(sp);
                        }
                        updateShareContent(updateContent);
                        shareDialog.dismiss();
                    }
                });
            }
        });

    }

    private void updateShareContent(final ShareContent updateContent) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.SHARE_UPDATE + updateContent.getsId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", updateContent.getsTitle());
                params.put("url", updateContent.getsURL());
                params.put("content", updateContent.getsContent());
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
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void findView(View rootView) {
        btn_back = (Button) rootView.findViewById(R.id.btn_back);
        edt_url = (EditText) rootView.findViewById(R.id.text_url);
        edt_title = (EditText) rootView.findViewById(R.id.text_title);
        edt_content = (EditText) rootView.findViewById(R.id.text_content);
        edt_id = (EditText) rootView.findViewById(R.id.text_id);
        imageView = (NetworkImageView) rootView.findViewById(R.id.share_img);
        btn_reshare = (Button) rootView.findViewById(R.id.btn_reshare);
    }
}
