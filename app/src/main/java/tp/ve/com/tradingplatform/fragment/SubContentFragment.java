package tp.ve.com.tradingplatform.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.HashMap;

import tp.ve.com.tradingplatform.R;
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
                        shareDialog.dismiss();
                        HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
                        if (item.get("ItemText").equals("Whatsapp")) {
                            Uri bmpUri = RealPathUtil.getLocalBitmapUri(s_img);
                            Log.v(TAG, "URL:" + bmpUri);
                            if (bmpUri != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);

                                shareIntent.setPackage("com.whatsapp");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, tv_title.getText().toString() + "\n" + custom_text.getText().toString());
                                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                shareIntent.setType("image/*");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                try {

                                    getActivity().startActivity(shareIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
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
