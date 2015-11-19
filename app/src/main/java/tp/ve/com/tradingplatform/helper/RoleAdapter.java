package tp.ve.com.tradingplatform.helper;

import android.content.Context;

import java.util.List;

import tp.ve.com.tradingplatform.entity.Role;

public class RoleAdapter extends RadioAdapter<Role> {
    public RoleAdapter(Context context, List<Role> items) {
        super(context, items);
    }

    @Override
    public void onBindViewHolder(RadioAdapter.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        viewHolder.mText.setText(mItems.get(i).mName);
        viewHolder.mStatus.setText(mItems.get(i).mStatus);
        viewHolder.mImage.setImageResource(mItems.get(i).mImag_id);

    }
}
