package afterapps.meeqat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

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
    private static final int VIEW_TYPE_DEFAULT = 0;
    private static final int VIEW_TYPE_DEFAULT_WITH_TITLE = 1;
    private String dayString;
    private long now;
    private RealmObjectPrayer nextPrayer;
    private RecyclerView recyclerView;

    public PrayersRecyclerAdapter(Context context, OrderedRealmCollection<RealmObjectPrayer> data, String dayString, RealmObjectPrayer nextPrayer, long now, RecyclerView prayersRecycler) {
        super(context, data, true);
        this.dayString = dayString;
        this.now = now;
        this.nextPrayer = nextPrayer;
        this.recyclerView = prayersRecycler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_DEFAULT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prayer, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prayer_with_title, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_DEFAULT_WITH_TITLE;
        else return VIEW_TYPE_DEFAULT;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        boolean upNext = false;
        if (getData() != null) {
            RealmObjectPrayer prayer = getData().get(position);
            if (now > getData().get(position).getTimestamp()) {
                holder.itemParent.setAlpha((float) 0.54);
            } else if (nextPrayer != null && prayer.getPrayerID() == nextPrayer.getPrayerID()) {
                recyclerView.smoothScrollToPosition(position);
                holder.prayerNameTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.prayerEnglishNameTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.prayerTimeTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                holder.prayerTimeTextView.setTypeface(null, Typeface.BOLD);
                holder.prayerNameTextView.setTypeface(null, Typeface.BOLD);
                holder.prayerEnglishNameTextView.setTypeface(null, Typeface.BOLD);
                upNext = true;
            }
            if (holder.prayerDayTextView != null) {
                holder.prayerDayTextView.setText(dayString);
            }
            holder.prayerNameTextView.setText(prayer.getName());
            holder.prayerEnglishNameTextView.setText(prayer.getEnglishName());
            holder.prayerTimeTextView.setText(Utilities.getTimePretty(prayer.getTimestamp()));
            int index = prayer.getIndex();

            IconicsDrawable prayerIcon = new IconicsDrawable(context).sizeDp(36);
            switch (index) {
                case 0:
                    prayerIcon.icon(WeatherIcons.Icon.wic_stars);
                    break;
                case 1:
                    prayerIcon.icon(WeatherIcons.Icon.wic_sunrise);
                    break;
                case 2:
                    prayerIcon.icon(WeatherIcons.Icon.wic_day_sunny);
                    break;
                case 3:
                    prayerIcon.icon(WeatherIcons.Icon.wic_day_cloudy_high);
                    break;
                case 4:
                    prayerIcon.icon(WeatherIcons.Icon.wic_sunset);
                    break;
                case 5:
                    prayerIcon.icon(FontAwesome.Icon.faw_moon_o);
                    break;
            }
            if (upNext) {
                prayerIcon.color(ContextCompat.getColor(context, R.color.colorAccent));
            } else {
                prayerIcon.color(Color.BLACK);
            }
            holder.prayerIconImageView.setImageDrawable(prayerIcon);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_prayer_name_text_view)
        TextView prayerNameTextView;
        @BindView(R.id.item_prayer_english_name_text_view)
        TextView prayerEnglishNameTextView;
        @BindView(R.id.item_prayer_time_text_view)
        TextView prayerTimeTextView;
        @BindView(R.id.prayer_icon_image_view)
        ImageView prayerIconImageView;
        @Nullable
        @BindView(R.id.prayers_day_text_view)
        TextView prayerDayTextView;
        @BindView(R.id.transaction_item_parent)
        View itemParent;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
