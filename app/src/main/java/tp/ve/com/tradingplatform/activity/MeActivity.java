package tp.ve.com.tradingplatform.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tp.ve.com.tradingplatform.R;
import tp.ve.com.tradingplatform.helper.SessionManager;

/**
 * Created by Zeng on 2015/12/4.
 */
public class MeActivity extends AppCompatActivity {
    private final static String TAG = MeActivity.class.getSimpleName();
    RecyclerView rListView;
    FloatingActionButton fab_change_role;

    String[] item_name = {
            "Address",
            "My message",
            "My order",
            "My wallet",
            "My watch list"
    };
    Integer[] item_icon = {
            R.drawable.me_address,
            R.drawable.me_message,
            R.drawable.me_order,
            R.drawable.me_wallet,
            R.drawable.me_watchlist,
    };

    String[] item_count = {
            "",
            "9",
            "",
            "",
            ""
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(SessionManager.currMember.getMember_name());

        rListView = (RecyclerView) findViewById(R.id.me_list);
        fab_change_role = (FloatingActionButton) findViewById(R.id.fab);
        fab_change_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        MeActivity.this, AccountSettingActivity.class);
                startActivity(intent);
            }
        });

        rListView.setLayoutManager(new LinearLayoutManager(this));
        rListView.setAdapter(new customAdapter(this));
    }

    private class customAdapter extends RecyclerView.Adapter<customAdapter.viewHolder> {

        private final LayoutInflater inflater;
        private final Context mContext;
        private final String[] item_name;
        private final String[] item_count;
        private final Integer[] img_id;

        public customAdapter(Context context) {
            // TODO Auto-generated constructor stub
            inflater = LayoutInflater.from(context);
            this.item_name = MeActivity.this.item_name;
            this.item_count = MeActivity.this.item_count;
            this.img_id = item_icon;
            this.mContext = context;
        }

        @Override
        public customAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new viewHolder(inflater.inflate(R.layout.me_item, parent, false));
        }

        @Override
        public void onBindViewHolder(customAdapter.viewHolder holder, int position) {
            holder.mName.setText(item_name[position]);
            holder.mImage.setImageResource(img_id[position]);
            holder.mCount.setText(item_count[position]);
        }

        @Override
        public int getItemCount() {
            return MeActivity.this.item_name == null ? 0 : MeActivity.this.item_name.length;
        }

        public class viewHolder extends RecyclerView.ViewHolder {
            public TextView mName;
            public ImageView mImage;
            public TextView mCount;

            public viewHolder(View itemView) {
                super(itemView);
                mName = (TextView) itemView.findViewById(R.id.me_name);
                mImage = (ImageView) itemView.findViewById(R.id.me_icon);
                mCount = (TextView) itemView.findViewById(R.id.me_count);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(TAG, "position: " + item_name[getAdapterPosition()]);
                        Toast.makeText(MeActivity.this, "click: " + item_name[getAdapterPosition()], Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

}
