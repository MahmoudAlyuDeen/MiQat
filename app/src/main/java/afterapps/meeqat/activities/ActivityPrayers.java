package afterapps.meeqat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import java.util.Calendar;
import java.util.List;

import afterapps.meeqat.R;
import afterapps.meeqat.Utilities;
import afterapps.meeqat.adapters.PrayersPagerAdapter;
import afterapps.meeqat.datamodel.Day;
import afterapps.meeqat.datamodel.PrayersResponse;
import afterapps.meeqat.datamodel.RealmObjectPrayer;
import afterapps.meeqat.datamodel.RealmPlace;
import afterapps.meeqat.helpers.RetrofitClients;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityPrayers extends AppCompatActivity {

    @BindView(R.id.prayers_view_pager)
    ViewPager prayersViewPager;
    @BindView(R.id.activity_prayers_toolbar)
    Toolbar toolbar;
    @BindView(R.id.prayers_dot_indicator)
    DotIndicator dotIndicator;
    @BindView(R.id.prayers_progress)
    View prayersProgress;
    @BindView(R.id.prayers_error)
    View prayersError;
    @BindView(R.id.next_prayer_time_text_view)
    TextView nextPrayerTimeTextView;
    @BindView(R.id.next_prayer_subtitle_text_view)
    TextView nextPrayerSubtitleTextView;
    @BindView(R.id.next_prayer_view_group)
    View nextPrayer;

    private Handler mHandler;

    Realm realm;

    Call<PrayersResponse> nextMonthPrayerCall;
    Call<PrayersResponse> currentMonthPrayerCall;
    RetrofitClients.PrayerTimesClient prayerTimesClient;


    @Override
    public void onDestroy() {
        realm.close();
        if (currentMonthPrayerCall != null) {
            currentMonthPrayerCall.cancel();
        }
        if (nextMonthPrayerCall != null) {
            nextMonthPrayerCall.cancel();
        }
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayers);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);
        realm = Realm.getDefaultInstance();
        init();
    }

    private void init() {
        prayersViewPager.setAdapter(new PrayersPagerAdapter(getSupportFragmentManager()));
        prayersViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dotIndicator.setSelectedItem(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onStart() {
        refreshPrayersForCurrentPlace();
        onResumePrayers();
        super.onStart();
    }

    @Override
    protected void onStop() {
        stopRepeatingTask();
        super.onStop();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                displayNextPrayer();
            } finally {
                int mInterval = 1000;
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void onResumePrayers() {
        mHandler = new Handler();
        startRepeatingTask();
    }

    private void displayNextPrayer() {
        RealmResults<RealmPlace> places = realm.where(RealmPlace.class).findAll();
        if (places.size() != 0) {
            RealmPlace activePlace = places.where().equalTo("active", true).findFirst();
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            RealmResults<RealmObjectPrayer> prayers = realm.where(RealmObjectPrayer.class)
                    .equalTo("place", activePlace.getId())
                    .equalTo("year", year).equalTo("month", month).findAll();
            if (prayers.size() == 0) {
                hideNextPrayer();
                showConnectionError();
            } else {
                showNextPrayer();
                hideConnectionError();
                long now = calendar.getTimeInMillis();
                prayers = prayers.where().greaterThan("timestamp", now).findAll().sort("timestamp", Sort.ASCENDING);
                if (prayers.size() != 0) {
                    RealmObjectPrayer nextPrayer = prayers.first();
                    nextPrayerTimeTextView.setText(Utilities.getFormattedTime(nextPrayer.getTimestamp() - now));
                    nextPrayerSubtitleTextView.setText(String.format(getString(R.string.next_prayer_subtitle_text_view),
                            nextPrayer.getName()));
                }
            }
        } else {
            hideNextPrayer();
            showConnectionError();
        }
    }

    private void showNextPrayer() {
        nextPrayer.setVisibility(View.VISIBLE);
    }

    private void hideNextPrayer() {
        nextPrayer.setVisibility(View.GONE);
    }

    private void refreshPrayersForCurrentPlace() {
        RealmResults<RealmPlace> places = realm.where(RealmPlace.class).findAll();
        if (places.size() != 0) {
            RealmPlace activePlace = places.where().equalTo("active", true).findFirst();
            checkIfDataExistsAndFetch(activePlace);
        }
    }

    private void checkIfDataExistsAndFetch(RealmPlace activePlace) {
        Calendar currentMonthCal = Calendar.getInstance();
        Calendar nextMonthCal = Calendar.getInstance();
        nextMonthCal.add(Calendar.MONTH, 1);

        int currentMonth = currentMonthCal.get(Calendar.MONTH);
        int currentYear = currentMonthCal.get(Calendar.YEAR);
        int nextMonth = nextMonthCal.get(Calendar.MONTH);
        int nextMonthYear = nextMonthCal.get(Calendar.YEAR);

        RealmResults<RealmObjectPrayer> prayers = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId())
                .equalTo("year", currentYear).equalTo("month", currentMonth).findAll();

        RealmResults<RealmObjectPrayer> nextMonthPrayers = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId())
                .equalTo("year", nextMonthYear).equalTo("month", nextMonth).findAll();

        if (prayers.size() == 0) {
            getPrayerTimes(activePlace, currentMonth, currentYear, true);
            showProgress();
        }

        if (nextMonthPrayers.size() == 0) {
            getPrayerTimes(activePlace, nextMonth, nextMonthYear, false);
            showProgress();
        }
    }

    private void showProgress() {
        prayersProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        prayersProgress.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_location:
                Intent intent = new Intent(this, ActivityLocations.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_prayers_menu, menu);
        return true;
    }


    private void getPrayerTimes(RealmPlace activePlace, int month, int year, boolean currentMonth) {
        final String BASE_URL = "http://api.aladhan.com//";
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        prayerTimesClient = retrofit.create(RetrofitClients.PrayerTimesClient.class);

        final double latitude = activePlace.getLatitude();
        final double longitude = activePlace.getLongitude();
        final String timezone = activePlace.getTimezone();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final int method = sharedPref.getInt(getString(R.string.perf_prayers_method_key), 5);

        if (currentMonth) {
            if (currentMonthPrayerCall != null) {
                currentMonthPrayerCall.cancel();
            }
            currentMonthPrayerCall = prayerTimesClient.getPrayerTimes(latitude, longitude, timezone, method, month + 1, year);
            currentMonthPrayerCall.enqueue(getPrayersCallBack(activePlace));
        } else {
            if (nextMonthPrayerCall != null) {
                nextMonthPrayerCall.cancel();
            }
            nextMonthPrayerCall = prayerTimesClient.getPrayerTimes(latitude, longitude, timezone, method, month + 1, year);
            nextMonthPrayerCall.enqueue(getPrayersCallBack(activePlace));
        }
    }

    private Callback<PrayersResponse> getPrayersCallBack(final RealmPlace activePlace) {
        return new Callback<PrayersResponse>() {
            @Override
            public void onResponse(Call<PrayersResponse> call, Response<PrayersResponse> response) {
                if (response.isSuccessful() && !call.isCanceled()) {
                    List<Day> days = response.body().getDays();
                    AsyncTaskParams params = new AsyncTaskParams(days, activePlace.getId(), ActivityPrayers.this);
                    new Utilities.AddDataToRealm().execute(params);
                    hideProgress();
                    hideConnectionError();
                    onResumePrayers();
                }
            }

            @Override
            public void onFailure(Call<PrayersResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    hideProgress();
                    showConnectionError();
                }
            }
        };
    }

    private void showConnectionError() {
        prayersError.setVisibility(View.VISIBLE);
    }

    private void hideConnectionError() {
        prayersError.setVisibility(View.GONE);
    }


    public static class AsyncTaskParams {
        public List<Day> days;
        public String activePlace;
        public Context context;

        AsyncTaskParams(List<Day> days, String activePlace, Context context) {
            this.activePlace = activePlace;
            this.days = days;
            this.context = context;
        }
    }
}
