package tp.ve.com.tradingplatform.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.MainActivity;
import tp.ve.com.tradingplatform.activity.ShareActivity;

import android.view.inputmethod.InputMethodManager;

/**
 * Created by Zeng on 2015/11/17.
 */
public class SubURLFragment extends Fragment {
    private static final String TAG = SubURLFragment.class.getSimpleName();

    EditText edt_title, edt_url;
    ImageView view_image;
    String img;
    Bitmap bitmap;
    ProgressDialog pDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_sub_url, container, false);
        edt_url = (EditText) rootView.findViewById(R.id.urlText);
        edt_url.setText(ShareActivity.urlString);
        edt_title = (EditText) rootView.findViewById(R.id.text_title);
        view_image = (ImageView) rootView.findViewById(R.id.view_image);

        Button btn_fetch = (Button) rootView.findViewById(R.id.btn_fetch);
        btn_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShareItemFragment.viewPager.setCurrentItem(1);
                String url = edt_url.getText().toString();
                if (!url.isEmpty()) {
                    if (!url.contains("http://")) {
                        url = "http://" + url;
                    }
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(rootView.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    new DownloadTask().execute(url);
                }

            }
        });
        return rootView;
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
            for (Element el : imgs) {
                img = el.attr("abs:src");
                Log.v(TAG, img);
            }
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
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
