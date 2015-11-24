package tp.ve.com.tradingplatform.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tp.ve.com.tradingplatform.R;

public class ShareDialog {

    private AlertDialog dialog;
    private GridView gridView;
    private RelativeLayout cancelButton;
    private SimpleAdapter saImageItems;
    private int[] image = {R.mipmap.share_sina, R.mipmap.share_wechat, R.mipmap.share_wechat_moments, R.mipmap.share_facebook, R.mipmap.share_whatsapp, R.mipmap.share_twitter};
    private String[] name = {"Weibo", "Wechat friend", "Friend circle", "Facebook", "Whatsapp", "Twitter"};

    public ShareDialog(Context context) {

        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 非常重要：设置对话框弹出的位置

        window.setBackgroundDrawableResource(android.R.color.transparent);
//        window.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        window.setContentView(R.layout.share_dialog);
        gridView = (GridView) window.findViewById(R.id.share_gridView);
        cancelButton = (RelativeLayout) window.findViewById(R.id.share_cancel);
        List<HashMap<String, Object>> shareList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < image.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", image[i]);//添加图像资源的ID
            map.put("ItemText", name[i]);//按序号做ItemText
            shareList.add(map);
        }

        saImageItems = new SimpleAdapter(context, shareList, R.layout.share_item, new String[]{"ItemImage", "ItemText"}, new int[]{R.id.imageView1, R.id.textView1});
        gridView.setAdapter(saImageItems);
    }

    public void setCancelButtonOnClickListener(OnClickListener Listener) {
        cancelButton.setOnClickListener(Listener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        gridView.setOnItemClickListener(listener);
    }


    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }
}