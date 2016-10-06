package afterapps.miqat.activities;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

import java.util.Calendar;
import java.util.List;

import afterapps.miqat.R;
import afterapps.miqat.Utilities;
import afterapps.miqat.datamodel.Day;
import afterapps.miqat.datamodel.PrayersResponse;
import afterapps.miqat.datamodel.RealmObjectPrayer;
import afterapps.miqat.datamodel.RealmPlace;
import afterapps.miqat.fragments.FragmentPrayersContent;
import afterapps.miqat.helpers.RetrofitClients;
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

    @BindView(R.id.activity_prayers_toolbar)
    Toolbar toolbar;
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
    @BindView(R.id.mosque_image_view)
    ImageView mosqueImageView;

    private FragmentPrayersContent scheduleFragment;

    private boolean animated;
    private boolean reAnimated;

    private Handler mHandler;

    private Realm realm;
    private RealmObjectPrayer lastHighlightedPrayer;

    private int method;
    private int school;
    private int latitudeMethod;

    private Call<PrayersResponse> nextMonthPrayerCall;
    private Call<PrayersResponse> currentMonthPrayerCall;
    private int lastDisplayedIcon;


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
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        scheduleFragment = (FragmentPrayersContent) getSupportFragmentManager()
                .findFragmentById(R.id.prayers_schedule_fragment);
        animated = false;
        reAnimated = true;
        lastDisplayedIcon = 0;
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
    private void animateIcon(boolean repeat, boolean changed, IconicsDrawable newIcon) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final long lastAnimated = sharedPref.getLong(getString(R.string.last_animated_key), 0);
        long now = Calendar.getInstance().getTimeInMillis();
        long delta = now - lastAnimated;

        int mosqueWidth = mosqueImageView.getWidth();
        int mosqueHeight = mosqueImageView.getHeight();
        int iconSize = mosqueWidth / 7;
        int iconRightRightMargin = (int) (mosqueWidth / 3.5);

        int displacement = mosqueHeight / 2;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
        layoutParams.setMargins(0, 0, iconRightRightMargin, 0);

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        animatingImageView.setLayoutParams(layoutParams);

        if (repeat) {
            if (delta > 2000) {
                if (changed) {
                    setChangeRise(displacement, newIcon);
                } else {
                    rise(displacement);
                }
            } else {
                animatingImageView.setAlpha((float) 1);
            }
        } else
            rise(displacement);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(getString(R.string.last_animated_key), now);
        editor.commit();
    }

    private void setChangeRise(final int displacement, final IconicsDrawable newIcon) {
        animatingImageView.animate()
                .alpha(0)
                .translationYBy(displacement * 8)
                .setDuration(3000)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatingImageView.setImageDrawable(newIcon);
                rise(displacement);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void rise(int displacement) {
        animatingImageView.setTranslationY(0);
        animatingImageView.setAlpha((float) 0);
        animatingImageView.animate()
                .alpha(1)
                .translationYBy(-displacement)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onStart() {
        onStartPrayers();
        super.onStart();
    }

    @Override
    protected void onStop() {
        stopRepeatingTask();
        lastHighlightedPrayer = null;
        animatingImageView.setAlpha((float) 0);
        reAnimated = false;
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

    private void onStartPrayers() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        method = Integer.
                valueOf(sharedPref.getString(getString(R.string.preference_key_method), "5"));
        school = Integer.
                valueOf(sharedPref.getString(getString(R.string.preference_key_school), "0"));
        latitudeMethod = Integer.
                valueOf(sharedPref.getString(getString(R.string.preference_key_latitude), "3"));

        refreshPrayersForCurrentPlace();
        mHandler = new Handler();
        startRepeatingTask();
    }

    private void displayNextPrayer() {
        hideNextPrayer();
        hidePrayerSchedule();
        hideNoPlacesMessage();
        RealmResults<RealmPlace> places = realm.where(RealmPlace.class).findAll();
        if (places.size() != 0) {
            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis();
            long range = now + (1000 * 60 * 60 * 12);
            RealmPlace activePlace = places.where().equalTo("active", true).findFirst();
            RealmResults<RealmObjectPrayer> prayers = realm.where(RealmObjectPrayer.class)
                    .equalTo("place", activePlace.getId())
                    .equalTo("method", method)
                    .equalTo("school", school)
                    .equalTo("latitudeMethod", latitudeMethod)
                    .greaterThan("timestamp", now)
                    .lessThan("timestamp", range)
                    .findAll()
                    .sort("timestamp", Sort.ASCENDING);

            if (prayers.size() != 0) {
                if (mosqueImageView.getWidth() != 0)
                    displayAnimatingIcon(activePlace);
                showPrayerSchedule();
                showNextPrayer();
                RealmObjectPrayer nextPrayer = prayers.first();
                nextPrayerTimeTextView
                        .setText(Utilities.getFormattedTime(nextPrayer.getTimestamp() - now));
                nextPrayerSubtitleTextView
                        .setText(String.format(getString(R.string.next_prayer_subtitle_text_view), nextPrayer.getName()));
                displayPrayersSchedule(nextPrayer);
            }
        } else {
            showNoPlacesMessage();
        }

    }

    private void displayPrayersSchedule(RealmObjectPrayer nextPrayer) {
        if (lastHighlightedPrayer == null
                || lastHighlightedPrayer.getPrayerID() != nextPrayer.getPrayerID()) {
            lastHighlightedPrayer = nextPrayer;
            if (lastHighlightedPrayer != null) {
                scheduleFragment.displayPrayers();
            }
        }
    }

    private void showPrayerSchedule() {
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(scheduleFragment).commitAllowingStateLoss();
        hideProgress();
    }

    private void showNoPlacesMessage() {
        noPlacesMessage.setVisibility(View.VISIBLE);
    }

    private void hideNoPlacesMessage() {
        noPlacesMessage.setVisibility(View.GONE);
    }

    private void hidePrayerSchedule() {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(scheduleFragment).commitAllowingStateLoss();
    }

    private void showProgress() {
        prayersProgress.setVisibility(View.VISIBLE);
        hideConnectionError();
    }

    private void hideProgress() {
        prayersProgress.setVisibility(View.GONE);
    }

    private void showConnectionError() {
        hideProgress();
        noPrayersMessage.setVisibility(View.VISIBLE);
    }

    private void hideConnectionError() {
        noPrayersMessage.setVisibility(View.GONE);
    }

    private void showNextPrayer() {
        nextPrayer.setVisibility(View.VISIBLE);
    }

    private void hideNextPrayer() {
        nextPrayer.setVisibility(View.GONE);
    }


    @SuppressLint("CommitPrefEdits")
    private void displayAnimatingIcon(RealmPlace activePlace) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();

        RealmResults<RealmObjectPrayer> prayers = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId())
                .equalTo("method", method)
                .equalTo("school", school)
                .equalTo("latitudeMethod", latitudeMethod)
                .equalTo("year", year)
                .equalTo("month", month)
                .equalTo("day", day).findAll();
        RealmObjectPrayer shurouq = prayers.where().equalTo("name", getString(R.string.shurouq)).findFirst();
        RealmObjectPrayer maghrib = prayers.where().equalTo("name", getString(R.string.maghrib)).findFirst();

        if (shurouq != null && maghrib != null) {
            long sunrise = shurouq.getTimestamp();
            long sunset = maghrib.getTimestamp();

            IconicsDrawable timeIcon = new IconicsDrawable(this).sizeDp(48);
            int displayedIcon;
            if (now < sunrise) {
                //midnight to sunrise
                timeIcon.icon(WeatherIcons.Icon.wic_stars);
                displayedIcon = 1;
            } else if (now > sunset) {
                //sunset to midnight
                timeIcon.icon(FontAwesome.Icon.faw_moon_o);
                displayedIcon = 2;
            } else {
                //sunrise to sunset
                timeIcon.icon(WeatherIcons.Icon.wic_day_sunny);
                displayedIcon = 3;
            }
            timeIcon.color(Color.WHITE);

            boolean changed = false;
            if (lastDisplayedIcon != 0 && lastDisplayedIcon != displayedIcon) {
                changed = true;
                reAnimated = false;
            } else {
                animatingImageView.setImageDrawable(timeIcon);
            }
            lastDisplayedIcon = displayedIcon;

            if (!animated) {
                animated = true;
                animateIcon(false, changed, timeIcon);
            } else if (!reAnimated) {
                reAnimated = true;
                animateIcon(true, changed, timeIcon);
            }
        }
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
                .equalTo("method", method)
                .equalTo("school", school)
                .equalTo("latitudeMethod", latitudeMethod)
                .equalTo("year", currentYear)
                .equalTo("month", currentMonth)
                .findAll();

        RealmResults<RealmObjectPrayer> nextMonthPrayers = realm.where(RealmObjectPrayer.class)
                .equalTo("place", activePlace.getId())
                .equalTo("method", method)
                .equalTo("school", school)
                .equalTo("latitudeMethod", latitudeMethod)
                .equalTo("year", nextMonthYear)
                .equalTo("month", nextMonth)
                .findAll();

        if (prayers.size() == 0) {
            getPrayerTimes(activePlace, currentMonth, currentYear, true);
            showProgress();
        }

        if (nextMonthPrayers.size() == 0) {
            getPrayerTimes(activePlace, nextMonth, nextMonthYear, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_location:
                Intent intent = new Intent(this, ActivityLocations.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                Intent settings = new Intent(this, ActivityPreferences.class);
                startActivity(settings);
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

        RetrofitClients.PrayerTimesClient prayerTimesClient = retrofit
                .create(RetrofitClients.PrayerTimesClient.class);

        final double latitude = activePlace.getLatitude();
        final double longitude = activePlace.getLongitude();
        final String timezone = activePlace.getTimezone();

        if (currentMonth) {
            if (currentMonthPrayerCall != null) {
                currentMonthPrayerCall.cancel();
            }
            currentMonthPrayerCall = prayerTimesClient
                    .getPrayerTimes(latitude, longitude, timezone, month + 1, year,
                            method, school, latitudeMethod);
            currentMonthPrayerCall.enqueue(getPrayersCallBack(activePlace));
        } else {
            if (nextMonthPrayerCall != null) {
                nextMonthPrayerCall.cancel();
            }
            nextMonthPrayerCall = prayerTimesClient
                    .getPrayerTimes(latitude, longitude, timezone, month + 1, year,
                            method, school, latitudeMethod);
            nextMonthPrayerCall.enqueue(getPrayersCallBack(activePlace));
        }
    }

    private Callback<PrayersResponse> getPrayersCallBack(final RealmPlace activePlace) {
        return new Callback<PrayersResponse>() {
            @Override
            public void onResponse(Call<PrayersResponse> call, Response<PrayersResponse> response) {
                if (response.isSuccessful() && !call.isCanceled()) {
                    List<Day> days = response.body().getDays();
                    Utilities.addDataToRealm(days, activePlace.getId(),
                            method, school, latitudeMethod, ActivityPrayers.this);
                    hideConnectionError();
                }
            }

            @Override
            public void onFailure(Call<PrayersResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    showConnectionError();
                }
            }
        };
    }
}
