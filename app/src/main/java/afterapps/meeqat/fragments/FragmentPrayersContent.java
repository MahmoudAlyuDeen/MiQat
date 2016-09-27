package afterapps.meeqat.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import afterapps.meeqat.R;
import afterapps.meeqat.adapters.PrayersRecyclerAdapter;
import afterapps.meeqat.datamodel.RealmObjectPrayer;
import afterapps.meeqat.datamodel.RealmPlace;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentPrayersContent extends Fragment {

    private static final String INSTANCE_INDEX_KEY = "instanceIndexKey";

    private int mIndex;
    @BindView(R.id.prayers_recycler_view)
    RecyclerView prayersRecycler;
    Realm realm;
    private Handler mHandler;
    PrayersRecyclerAdapter prayersRecyclerAdapter;
    RealmPlace activePlace;

    public FragmentPrayersContent() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mIndex = getArguments().getInt(INSTANCE_INDEX_KEY);
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    public static FragmentPrayersContent newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(INSTANCE_INDEX_KEY, index);
        FragmentPrayersContent fragment = new FragmentPrayersContent();
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public void onResume() {
        mHandler = new Handler();
        startRepeatingTask();

        init();
        super.onResume();
    }

    @Override
    public void onPause() {
        stopRepeatingTask();
        super.onPause();
    }

    private void init() {
        RealmResults<RealmPlace> places = realm.where(RealmPlace.class).findAll();
        if (places.size() != 0) {
            activePlace = places.where().equalTo("active", true).findFirst();
            displayPrayers(activePlace);
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (prayersRecyclerAdapter != null) {
                    resendNextPrayer();
                }
            } finally

            {
                int mInterval = 1000;
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void resendNextPrayer() {
        if (activePlace != null) {
            RealmObjectPrayer nextPrayer = getNextPrayer(activePlace);
            if (nextPrayer != null)
                prayersRecyclerAdapter.setNextPrayer(nextPrayer);
        }
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    private void displayPrayers(RealmPlace activePlace) {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        String dayString;
        if (mIndex == 0) {
            dayString = getString(R.string.today_prayers_title);
        } else {
            dayString = getString(R.string.tomorrow_prayers_title);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        RealmResults<RealmObjectPrayer> prayers = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId())
                .equalTo("year", year)
                .equalTo("month", month)
                .equalTo("day", day).findAll();
        prayers = prayers.sort("timestamp", Sort.ASCENDING);

        prayersRecyclerAdapter = new PrayersRecyclerAdapter(getContext(), prayers, dayString, getNextPrayer(activePlace), now, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        prayersRecyclerAdapter.setHasStableIds(true);
        prayersRecycler.setLayoutManager(linearLayoutManager);
        prayersRecycler.setAdapter(prayersRecyclerAdapter);
    }

    private RealmObjectPrayer getNextPrayer(RealmPlace activePlace) {
        long now = Calendar.getInstance().getTimeInMillis();
        RealmResults<RealmObjectPrayer> prayerRealmResults = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId()).findAll();
        prayerRealmResults = prayerRealmResults.where().greaterThan("timestamp", now).findAll()
                .sort("timestamp", Sort.ASCENDING);

        RealmObjectPrayer nextPrayer = null;
        if (prayerRealmResults.size() != 0) {
            nextPrayer = prayerRealmResults.first();
        }
        return nextPrayer;
    }

    public void invalidatePrayers() {
        init();
    }
}
