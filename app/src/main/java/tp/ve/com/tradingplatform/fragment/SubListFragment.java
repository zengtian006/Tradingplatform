package tp.ve.com.tradingplatform.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swipemenulistview.BaseSwipListAdapter;
import swipemenulistview.SwipeMenu;
import swipemenulistview.SwipeMenuCreator;
import swipemenulistview.SwipeMenuItem;
import swipemenulistview.SwipeMenuListView;
import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.app.AppConfig;
import tp.ve.com.tradingplatform.app.AppController;
import tp.ve.com.tradingplatform.entity.ShareContent;
import tp.ve.com.tradingplatform.helper.SessionManager;

/**
 * Created by Zeng on 2015/11/26.
 */
public class SubListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG = SubListFragment.class.getSimpleName();

    private List<ShareContent> mShareList;
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    EditText searchText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;
    public static int startTimes;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        startTimes = 1;
        View rootView = inflater.inflate(R.layout.fragment_sub_list, container, false);

        findView(rootView);
        setListner();
        setView();
        return rootView;
    }

    private void setListner() {
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                mAdapter.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setView() {

        new refreshShareList().execute();
//
//        mShareList = new ArrayList<ShareContent>();
//        for (int i = 0; i < 10; i++) {
//            ShareContent shareContent = new ShareContent();
//            shareContent.setsTitle("Title " + i);
//            shareContent.setsDate("2015.12.25");
//            shareContent.setsImg_path("http://10.0.3.91/webroot/img/bk.jpg");
//            mShareList.add(shareContent);
//        }
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(R.color.colorAccent);
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ShareContent item = mShareList.get(position);
                switch (index) {
                    case 0:
                        mShareList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareContent item = mShareList.get(position);
//                Toast.makeText(getActivity(), item.getsId() + ": " + item.getsContent(), Toast.LENGTH_SHORT).show();
                SubDetailFragment.edt_url.setText(item.getsURL());
                SubDetailFragment.edt_title.setText(item.getsTitle());
                SubDetailFragment.edt_content.setText(item.getsContent());
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                SubDetailFragment.imageView.setImageUrl(item.getsImg_path(), imageLoader);
                SubDetailFragment.img_url = item.getsImg_path();
                SubDetailFragment.edt_id.setText(item.getsId());
                ShareListFragment.viewPager.setCurrentItem(1);
            }
        });
        // set creator
        mListView.setMenuCreator(creator);
    }

    private void findView(View rootView) {
        mListView = (SwipeMenuListView) rootView.findViewById(R.id.listView);
        searchText = (EditText) rootView.findViewById(R.id.inputSearch);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new refreshShareList().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private class refreshShareList extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            loadShareContent();
            return null;
        }
    }

    public void loadShareContent() {
//        pDialog.setMessage("Loading...");
//        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.SHARE_INDEX, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response.toString());
//                hideDialog();
                try {
                    // Parsing json
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jsonArray = jObj.getJSONArray("shares");
                    mShareList = new ArrayList<ShareContent>();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject shareObj = new JSONObject(jsonArray.get(i).toString());

                        ShareContent shareContent = new ShareContent();
                        shareContent.setsId(shareObj.getString("id"));
                        shareContent.setsURL(shareObj.getString("url"));
                        shareContent.setsTitle(shareObj.getString("title"));
                        shareContent.setsContent(shareObj.getString("content"));

                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                        String dateFormatted = f.format(f.parse(shareObj.getString("created")));
                        shareContent.setsDate(dateFormatted);

                        if (!shareObj.getString("img_path").contains("http://")) {
                            shareContent.setsImg_path("http://" + getString(R.string.app_url) + shareObj.getString("img_path"));
                        } else {
                            shareContent.setsImg_path(shareObj.getString("img_path"));
                        }
                        shareContent.setsId(shareObj.getString("id"));
                        mShareList.add(shareContent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mAdapter = new AppAdapter(mShareList);
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("member_id", SessionManager.currMember.getMember_id());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + SessionManager.temptoken);
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    class AppAdapter extends BaseSwipListAdapter implements Filterable {
        private ItemFilter mFilter = new ItemFilter();
        private List<ShareContent> originalData = null;
        //        private List<ShareContent> filteredData = null;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        public AppAdapter(List<ShareContent> mShareList) {
            this.originalData = mShareList;
//            this.filteredData = mShareList;
        }

        @Override
        public int getCount() {
//            return filteredData.size();
            return mShareList.size();
        }

        @Override
        public ShareContent getItem(int position) {
            return mShareList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity().getApplicationContext(),
                        R.layout.share_item_view, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ShareContent item = getItem(position);

//            Bitmap bmImg = BitmapFactory.decodeFile(item.getsImg_path());
            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

            holder.iv_icon.setImageUrl(item.getsImg_path(), imageLoader);
            holder.tv_name.setText(item.getsTitle());
            holder.s_date.setText(item.getsDate());

//            View.OnClickListener itemClick = new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ShareListFragment.viewPager.setCurrentItem(1)
//                }
//            };
//
//            holder.iv_icon.setOnClickListener(itemClick);
//            holder.tv_name.setOnClickListener(itemClick);
            return convertView;
        }


        class ViewHolder {
            NetworkImageView iv_icon;
            TextView tv_name;
            TextView s_date;

            public ViewHolder(View view) {
                iv_icon = (NetworkImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                s_date = (TextView) view.findViewById(R.id.s_date);
                view.setTag(this);
            }
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<ShareContent> list = originalData;

                int count = list.size();
                final ArrayList<ShareContent> nlist = new ArrayList<ShareContent>(count);

                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getsTitle();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
                Log.v(TAG, "LIST: " + nlist.toString());

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mShareList = (List<ShareContent>) results.values;
                mAdapter.notifyDataSetChanged();
            }

        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
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
