package afterapps.meeqat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import afterapps.meeqat.R;
import afterapps.meeqat.activities.ActivityLocations;
import afterapps.meeqat.datamodel.RealmPlace;
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
            holder.placesItemTextView.setText(getData().get(position).getName());
            if (getData().get(position).isActive())
                holder.placesItemTextView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            else
                holder.placesItemTextView.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.places_item_name_text_view)
        TextView placesItemTextView;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            placesItemTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getData() != null) {
                ((ActivityLocations) context).handleRecyclerClick(getData().get(getLayoutPosition()).getId());
            }
        }
    }
}
