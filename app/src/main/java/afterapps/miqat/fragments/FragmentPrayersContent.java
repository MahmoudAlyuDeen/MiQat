package afterapps.miqat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import afterapps.miqat.R;
import afterapps.miqat.adapters.PrayersRecyclerAdapter;
import afterapps.miqat.datamodel.RealmObjectPrayer;
import afterapps.miqat.datamodel.RealmPlace;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentPrayersContent extends Fragment {

    @BindView(R.id.prayers_recycler_view)
    RecyclerView prayersRecycler;
    Realm realm;
    PrayersRecyclerAdapter prayersRecyclerAdapter;
    RealmPlace activePlace;

    private int method;
    private int school;
    private int latitudeMethod;

    public FragmentPrayersContent() {
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_prayers_content, container, false);
        ButterKnife.bind(this, rootView);
        realm = Realm.getDefaultInstance();
        return rootView;
    }

    private void populateSchedule() {
        RealmResults<RealmPlace> places = realm.where(RealmPlace.class).findAll();
        activePlace = places.where().equalTo("active", true).findFirst();

        long now = Calendar.getInstance().getTimeInMillis();
        RealmResults<RealmObjectPrayer> prayers = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId())
                .equalTo("method", method)
                .equalTo("school", school)
                .equalTo("latitudeMethod", latitudeMethod)
                .greaterThan("timestamp", now)
                .findAll()
                .sort("timestamp", Sort.ASCENDING);

        if (prayers.size() != 0) {
            long maxTimestamp = prayers.get(6).getTimestamp();
            prayers = prayers.where()
                    .lessThan("timestamp", maxTimestamp)
                    .findAll()
                    .sort("timestamp", Sort.ASCENDING);
        }

        prayersRecyclerAdapter = new PrayersRecyclerAdapter(getContext(), prayers,
                getNextPrayer(activePlace));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        prayersRecyclerAdapter.setHasStableIds(true);
        prayersRecycler.setLayoutManager(linearLayoutManager);
        prayersRecycler.setAdapter(prayersRecyclerAdapter);
    }

    private RealmObjectPrayer getNextPrayer(RealmPlace activePlace) {
        long now = Calendar.getInstance().getTimeInMillis();
        RealmResults<RealmObjectPrayer> prayerRealmResults = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId())
                .equalTo("method", method)
                .equalTo("school", school)
                .equalTo("latitudeMethod", latitudeMethod)
                .greaterThan("timestamp", now)
                .findAll()
                .sort("timestamp", Sort.ASCENDING);

        RealmObjectPrayer nextPrayer = null;
        if (prayerRealmResults.size() != 0) {
            nextPrayer = prayerRealmResults.first();
        }
        return nextPrayer;
    }

    public void displayPrayers() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        method = Integer.
                valueOf(sharedPref.getString(getString(R.string.preference_key_method), "5"));
        school = Integer.
                valueOf(sharedPref.getString(getString(R.string.preference_key_school), "0"));
        latitudeMethod = Integer.
                valueOf(sharedPref.getString(getString(R.string.preference_key_latitude), "3"));
        populateSchedule();
    }
}
