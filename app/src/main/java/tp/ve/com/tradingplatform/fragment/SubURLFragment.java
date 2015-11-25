package tp.ve.com.tradingplatform.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.AccountSettingActivity;
import tp.ve.com.tradingplatform.activity.MainActivity;
import tp.ve.com.tradingplatform.activity.ShareActivity;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.utils.RealPathUtil;

import android.view.inputmethod.InputMethodManager;

/**
 * Created by Zeng on 2015/11/17.
 */
public class SubURLFragment extends Fragment {
    private static final String TAG = SubURLFragment.class.getSimpleName();

    private static final int CAPTURE_IMAGE_REQUEST_CODE = 200;
    public static EditText edt_title, edt_url;
    public static ImageView view_image;
    public static String img;
    Bitmap bitmap;
    ProgressDialog pDialog;
    public static Button btn_img_del, btn_fetch, btn_next, btn_clear;
    CardView cardView;
    LinearLayout layout_url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_sub_url, container, false);
        findView(rootView);
        setListener(rootView);
        setView();
        return rootView;
    }

    private void setView() {
        edt_url.setText(ShareActivity.iniURL);
        view_image.setTag("empty");
        btn_img_del.setVisibility(Button.GONE);
    }

    private void setListener(final View rootView) {
        view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (view_image.getTag().toString().equals("empty")) {
                    openImageIntent(CAPTURE_IMAGE_REQUEST_CODE);
                } else {
                    ShareActivity.img_dialog = new AlertDialog.Builder(getActivity()).create();
                    ShareActivity.img_dialog.show();
                    Window window = ShareActivity.img_dialog.getWindow();
                    window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置

                    window.setBackgroundDrawableResource(android.R.color.transparent);
                    window.setContentView(R.layout.image_dialog);
                    ImageView imageView = (ImageView) window.findViewById(R.id.dialog_imageView);

                    imageView.setImageDrawable(view_image.getDrawable());
                }
            }
        });

        btn_img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.icon_addpic_unfocused));
                view_image.setTag("empty");
                btn_img_del.setVisibility(Button.GONE);
                ShareActivity.loaclImgPath = "";
//                btn_img_del.setAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            }
        });

        btn_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShareItemFragment.viewPager.setCurrentItem(1);
                String url = edt_url.getText().toString();
                if (!url.isEmpty()) {
                    if (!url.contains("http://")) {
                        url = "http://" + url;
                    }
                    edt_url.setText(url);
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(rootView.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    new DownloadTask().execute(url);
                }

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubContentFragment.custom_text.setText("Check this link for more information " + edt_url.getText().toString());
                Linkify.addLinks(SubContentFragment.custom_text, Linkify.ALL);
                SubContentFragment.s_img.setImageDrawable(view_image.getDrawable());
                SubContentFragment.s_img.setTag(view_image.getTag());
                SubContentFragment.tv_title.setText(edt_title.getText());
                ShareItemFragment.viewPager.setCurrentItem(1);
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_url.setText("");
                edt_title.setText("");
                btn_img_del.performClick();
                edt_url.requestFocus();

            }
        });
    }

    private void findView(View rootView) {
        edt_url = (EditText) rootView.findViewById(R.id.urlText);
        edt_url.setText(ShareActivity.urlString);
        edt_title = (EditText) rootView.findViewById(R.id.text_title);
        view_image = (ImageView) rootView.findViewById(R.id.view_image);
        btn_img_del = (Button) rootView.findViewById(R.id.btn_img_del);
        btn_fetch = (Button) rootView.findViewById(R.id.btn_fetch);
        cardView = (CardView) rootView.findViewById(R.id.card_view_share_url);
        layout_url = (LinearLayout) rootView.findViewById(R.id.share_url);
        btn_next = (Button) rootView.findViewById(R.id.btn_next);
        btn_clear = (Button) rootView.findViewById(R.id.btn_clear);
    }

    /**
     * Initiates the fetch operation.
     */
    public String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String title;
        try { // Use Jsoup to Obtain HTML text from URL, Search for Title and IMG TAGS
            Document doc = Jsoup.connect(urlString).get();
            title = doc.title();
            Elements imgs = doc.getElementsByTag("img");
            for (int i = 0; i < 2; i++) {
                Element el = imgs.get(i);
                img = el.attr("abs:src");
            }
//            for (Element el : imgs) {
//                img = el.attr("abs:src");
//                Log.v(TAG, "image address: " + img);
//            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return title;
        // return img;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching URL ....");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                return "Wrong";
            }
        }

        //Do Update of TextView using and Show Images ONLY WHEN PARSING  FINISH
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("URLFragment", result);
            if (result.equals("Wrong")) {
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Can not fetch this URL", Toast.LENGTH_SHORT).show();
                edt_url.requestFocus();
                edt_url.selectAll();
            } else {
                edt_title.setText(result);
                new LoadImage().execute(img);
            }
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                view_image.setImageBitmap(image);
                view_image.setTag("full");
                btn_img_del.setVisibility(Button.VISIBLE);
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
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
                + "IMG_" + timeStamp + ".png");


        ShareActivity.outputFileUri = Uri.fromFile(imageFile);

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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ShareActivity.outputFileUri);
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

}
