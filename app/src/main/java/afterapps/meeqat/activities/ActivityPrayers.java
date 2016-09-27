package afterapps.meeqat.activities;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

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
    @BindView(R.id.no_places_message)
    View noPlacesMessage;
    @BindView(R.id.no_prayers_message)
    View noPrayersMessage;
    @BindView(R.id.next_prayer_time_text_view)
    TextView nextPrayerTimeTextView;
    @BindView(R.id.next_prayer_subtitle_text_view)
    TextView nextPrayerSubtitleTextView;
    @BindView(R.id.next_prayer_view_group)
    View nextPrayer;
    @BindView(R.id.animating_image_view)
    ImageView animatingImageView;

    private boolean animated;
    private boolean animatedAgain;


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
        animated = false;
        animatedAgain = true;
        realm = Realm.getDefaultInstance();
        setupRetryListener();
    }

    private void setupRetryListener() {
        noPrayersMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPrayersForCurrentPlace();
            }
        });
    }

    @SuppressLint("CommitPrefEdits")
    private void animateIcon(boolean repeat) {
        Log.d("j", "animateIcon: " + repeat);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final long lastAnimated = sharedPref.getLong(getString(R.string.last_animated_key), 0);
        long now = Calendar.getInstance().getTimeInMillis();
        long delta = now - lastAnimated;


        if (repeat) {
            if (delta > 2000) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong(getString(R.string.last_animated_key), now);
                editor.commit();

                animatingImageView.animate()
                        .alpha(0)
                        .translationYBy(150)
                        .setDuration(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animatingImageView.animate()
                                .alpha(1)
                                .translationYBy(-150)
                                .setDuration(1000)
                                .setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                animator.cancel();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        } else {
            animatingImageView.animate()
                    .alpha(1)
                    .translationYBy(-150)
                    .setDuration(1000)
                    .setInterpolator(new AccelerateDecelerateInterpolator());

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(getString(R.string.last_animated_key), now);
            editor.commit();
        }
    }

    @Override
    protected void onStart() {
        refreshPrayersForCurrentPlace();
        onStartPrayers();
        super.onStart();
    }

    @Override
    protected void onStop() {
        stopRepeatingTask();
        animatedAgain = false;
        super.onStop();
    }

    @Override
    protected void onRestart() {
        animatedAgain = false;
        super.onRestart();
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

    private void onStartPrayers() {
        mHandler = new Handler();
        startRepeatingTask();
    }

    private void displayNextPrayer() {
        RealmResults<RealmPlace> places = realm.where(RealmPlace.class).findAll();
        if (places.size() != 0) {
            RealmPlace activePlace = places.where().equalTo("active", true).findFirst();
            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis();
            long range = now + (1000 * 60 * 60 * 24 * 2);
            RealmResults<RealmObjectPrayer> prayers = realm.where(RealmObjectPrayer.class)
                    .equalTo("place", activePlace.getId())
                    .lessThan("timestamp", range)
                    .findAll();
            if (prayers.size() == 0) {
                hideNextPrayer();
                hidePrayers();
            } else {
                displayProperIcon(prayers);
                showPrayers();
                showNextPrayer();
                hideNoPlacesMessage();
                hideConnectionError();
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
            hidePrayers();
            hideConnectionError();
            showNoPlacesMessage();
        }
    }

    private void showPrayers() {
        prayersViewPager.setVisibility(View.VISIBLE);
        dotIndicator.setVisibility(View.VISIBLE);
    }

    private void showNoPlacesMessage() {
        noPlacesMessage.setVisibility(View.VISIBLE);
    }

    private void hideNoPlacesMessage() {
        noPlacesMessage.setVisibility(View.GONE);
    }

    private void hidePrayers() {
        prayersViewPager.setVisibility(View.GONE);
        dotIndicator.setVisibility(View.GONE);
    }

    private void displayProperIcon(RealmResults<RealmObjectPrayer> prayers) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();

        prayers = prayers.where()
                .equalTo("year", year)
                .equalTo("month", month)
                .equalTo("day", day).findAll();
        RealmObjectPrayer fajr = prayers.where().equalTo("name", getString(R.string.shurouq)).findFirst();
        RealmObjectPrayer maghrib = prayers.where().equalTo("name", getString(R.string.maghrib)).findFirst();
        if (fajr != null && maghrib != null) {
            long sunrise = fajr.getTimestamp();
            long sunset = maghrib.getTimestamp();

            IconicsDrawable timeIcon = new IconicsDrawable(this).sizeDp(48);
            if (now < sunrise) {
                //midnight to sunrise
                timeIcon.icon(WeatherIcons.Icon.wic_stars);
            } else if (now > sunset) {
                //sunset to midnight
                timeIcon.icon(FontAwesome.Icon.faw_moon_o);
            } else {
                //sunrise to sunset
                timeIcon.icon(WeatherIcons.Icon.wic_day_sunny);
            }
            timeIcon.color(Color.WHITE);
            animatingImageView.setImageDrawable(timeIcon);

            if (!animated) {
                animated = true;
                animateIcon(false);
            } else if (!animatedAgain) {
                animatedAgain = true;
                animateIcon(true);
            }
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

    private void showConnectionError() {
        noPrayersMessage.setVisibility(View.VISIBLE);
    }

    private void hideConnectionError() {
        noPrayersMessage.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_location:
                Intent intent = new Intent(this, ActivityLocations.class);
                startActivity(intent);
                return true;

            case R.id.action_libraries:
                new LibsBuilder().withActivityTheme(R.style.AppTheme)
                        .withAutoDetect(true)
                        .withFields(R.string.class.getFields())
                        .start(this);
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
                    Utilities.addDataToRealm(days, activePlace.getId(), ActivityPrayers.this);
                    hideProgress();
                    hideConnectionError();
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
}
