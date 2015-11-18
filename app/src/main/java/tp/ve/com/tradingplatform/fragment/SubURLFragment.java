package tp.ve.com.tradingplatform.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.activity.ShareActivity;

/**
 * Created by Zeng on 2015/11/17.
 */
public class SubURLFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sub_url, container, false);
        final EditText edt_url = (EditText) rootView.findViewById(R.id.urlText);
        edt_url.setText(ShareActivity.urlString);
        Button ttbtn = (Button) rootView.findViewById(R.id.ttbutton);
        ttbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShareItemFragment.viewPager.setCurrentItem(1);
                new DownloadTask().execute(edt_url.getText().toString());

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
            title = "Web Title: " + doc.title() + "\n";
            Elements imgs = doc.getElementsByTag("img");
            Log.v("test", title);
//            for (Element el : imgs) {
//                img = el.attr("abs:src");
//                ims.add(img);
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
        }

    }
}
