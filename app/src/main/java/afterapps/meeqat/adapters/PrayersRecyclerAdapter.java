package afterapps.meeqat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import afterapps.meeqat.R;
import afterapps.meeqat.Utilities;
import afterapps.meeqat.datamodel.RealmObjectPrayer;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/*
 * Created by Mahmoud on 9/25/2016.
 */

public class PrayersRecyclerAdapter extends RealmRecyclerViewAdapter<RealmObjectPrayer, RecyclerView.ViewHolder> {

    public PrayersRecyclerAdapter(Context context, OrderedRealmCollection<RealmObjectPrayer> data) {
        super(context, data, true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prayer, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        if (getData() != null) {
            holder.prayerName.setText(getData().get(position).getName());
            holder.prayerTime.setText(Utilities.getTimePretty(getData().get(position).getTimestamp()));
            int index = getData().get(position).getIndex();
            switch (index) {
                case 0:
                    holder.prayerIconImageView.setImageResource(R.drawable.stars);
                    break;
                case 1:
                    holder.prayerIconImageView.setImageResource(R.drawable.sun_rising);
                    break;
                case 2:
                    holder.prayerIconImageView.setImageResource(R.drawable.sun_mid_day);
                    break;
                case 3:
                    holder.prayerIconImageView.setImageResource(R.drawable.sun_asr);
                    break;
                case 4:
                    holder.prayerIconImageView.setImageResource(R.drawable.sun_setting);
                    break;
                case 5:
                    holder.prayerIconImageView.setImageResource(R.drawable.moon_and_clouds);
                    break;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_prayer_name_text_view)
        TextView prayerName;
        @BindView(R.id.item_prayer_time_text_view)
        TextView prayerTime;
        @BindView(R.id.prayer_icon_image_view)
        ImageView prayerIconImageView;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
