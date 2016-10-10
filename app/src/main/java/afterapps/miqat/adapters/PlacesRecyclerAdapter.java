package afterapps.miqat.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import afterapps.miqat.R;
import afterapps.miqat.activities.ActivityLocations;
import afterapps.miqat.datamodel.RealmPlace;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/*
 * Created by Mahmoud on 9/14/2016.
 */
public class PlacesRecyclerAdapter extends RealmRecyclerViewAdapter<RealmPlace, RecyclerView.ViewHolder> {

    public PlacesRecyclerAdapter(Context context, OrderedRealmCollection<RealmPlace> data) {
        super(context, data, true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_places, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        if (getData() != null) {
            RealmPlace place = getData().get(position);
            holder.nameTextView.setText(place.getName());
            holder.activeRadioButton.setChecked(place.isActive());
            holder.nameTextView.setTextColor(ContextCompat.getColor(context, place.isActive()? R.color.colorAccent : R.color.primary_text));
            if (place.isActive()) {
                holder.nameTextView.setTypeface(null, Typeface.BOLD);
            }
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.places_item_name_text_view)
        TextView nameTextView;
        @BindView(R.id.item_places_item_delete_icon)
        ImageView deleteIconImageView;
        @BindView(R.id.item_places_item_parent)
        View parent;
        @BindView(R.id.item_places_radio_button)
        RadioButton activeRadioButton;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            parent.setOnClickListener(this);
            deleteIconImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_places_item_delete_icon:
                    if (getData() != null) {
                        ((ActivityLocations) context).handleDeletionClick(getData().get(getLayoutPosition()).getId());
                    }
                    break;

                case R.id.item_places_item_parent:
                    if (getData() != null) {
                        ((ActivityLocations) context).handleRecyclerClick(getData().get(getLayoutPosition()).getId());
                    }
                    break;
            }

        }
    }
}
