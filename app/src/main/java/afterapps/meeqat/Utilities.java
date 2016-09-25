package afterapps.meeqat;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import afterapps.meeqat.activities.ActivityPrayers;
import afterapps.meeqat.datamodel.Day;
import afterapps.meeqat.datamodel.RealmObjectPrayer;
import io.realm.Realm;

import static com.google.android.gms.internal.zzs.TAG;

public class Utilities {
    private static long getPrayerNextID(Realm realm) {
        Number number = realm.where(RealmObjectPrayer.class).max("prayerID");
        if (number == null) return 1;
        else return (long) number + 1;
    }

    public static String getFormattedTime(long interval) {
        return String.format(Locale.US, "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(interval),
                TimeUnit.MILLISECONDS.toMinutes(interval) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(interval)),
                TimeUnit.MILLISECONDS.toSeconds(interval) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval)));
    }

    public static String getTimePretty(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return format.format(calendar.getTime());
    }


    public static class AddDataToRealm extends AsyncTask<ActivityPrayers.AsyncTaskParams, Void, Void> {

        @Override
        protected final Void doInBackground(ActivityPrayers.AsyncTaskParams... params) {
            Realm backgroundRealm = Realm.getDefaultInstance();
            List<Day> days = params[0].days;
            String activePlaceId = params[0].activePlace;
            Context context = params[0].context;
            for (Day day : days) {
                Log.d("", "doInBackground: " + day.getDate().getReadable());

                Calendar dayCal = Calendar.getInstance();
                long dayTimestamp = day.getDate().getTimestamp();
                dayCal.setTimeInMillis(dayTimestamp * 1000);
                int calDay = dayCal.get(Calendar.DAY_OF_MONTH);
                int calMonth = dayCal.get(Calendar.MONTH);
                int calYear = dayCal.get(Calendar.YEAR);

                dayCal.set(calYear, calMonth, calDay,
                        Integer.valueOf(day.getTimings().getFajr().substring(0, 2)),
                        Integer.valueOf(day.getTimings().getFajr().substring(3, 5)));

                Log.d(TAG, "doInBackground: " + "Fajr: " + dayCal.getTimeInMillis());
                backgroundRealm.beginTransaction();
                RealmObjectPrayer realmObjectPrayer = backgroundRealm.createObject(RealmObjectPrayer.class);
                realmObjectPrayer.setPrayerID(Utilities.getPrayerNextID(backgroundRealm));
                realmObjectPrayer.setName(context.getString(R.string.fajr));
                realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                realmObjectPrayer.setPlace(activePlaceId);
                realmObjectPrayer.setIndex(0);
                backgroundRealm.copyToRealmOrUpdate(realmObjectPrayer);
                backgroundRealm.commitTransaction();

                dayCal.set(calYear, calMonth, calDay,
                        Integer.valueOf(day.getTimings().getSunrise().substring(0, 2)),
                        Integer.valueOf(day.getTimings().getSunrise().substring(3, 5)));

                backgroundRealm.beginTransaction();
                realmObjectPrayer = backgroundRealm.createObject(RealmObjectPrayer.class);
                realmObjectPrayer.setPrayerID(Utilities.getPrayerNextID(backgroundRealm));
                realmObjectPrayer.setName(context.getString(R.string.shurouq));
                realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                realmObjectPrayer.setPlace(activePlaceId);
                realmObjectPrayer.setIndex(1);
                backgroundRealm.copyToRealmOrUpdate(realmObjectPrayer);
                backgroundRealm.commitTransaction();

                dayCal.set(calYear, calMonth, calDay,
                        Integer.valueOf(day.getTimings().getDhuhr().substring(0, 2)),
                        Integer.valueOf(day.getTimings().getDhuhr().substring(3, 5)));

                backgroundRealm.beginTransaction();
                realmObjectPrayer = backgroundRealm.createObject(RealmObjectPrayer.class);
                realmObjectPrayer.setPrayerID(Utilities.getPrayerNextID(backgroundRealm));
                realmObjectPrayer.setName(context.getString(R.string.dhuhr));
                realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                realmObjectPrayer.setPlace(activePlaceId);
                realmObjectPrayer.setIndex(2);
                backgroundRealm.copyToRealmOrUpdate(realmObjectPrayer);
                backgroundRealm.commitTransaction();

                dayCal.set(calYear, calMonth, calDay,
                        Integer.valueOf(day.getTimings().getAsr().substring(0, 2)),
                        Integer.valueOf(day.getTimings().getAsr().substring(3, 5)));

                backgroundRealm.beginTransaction();
                realmObjectPrayer = backgroundRealm.createObject(RealmObjectPrayer.class);
                realmObjectPrayer.setPrayerID(Utilities.getPrayerNextID(backgroundRealm));
                realmObjectPrayer.setName(context.getString(R.string.asr));
                realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                realmObjectPrayer.setPlace(activePlaceId);
                realmObjectPrayer.setIndex(3);
                backgroundRealm.copyToRealmOrUpdate(realmObjectPrayer);
                backgroundRealm.commitTransaction();

                dayCal.set(calYear, calMonth, calDay,
                        Integer.valueOf(day.getTimings().getMaghrib().substring(0, 2)),
                        Integer.valueOf(day.getTimings().getMaghrib().substring(3, 5)));

                backgroundRealm.beginTransaction();
                realmObjectPrayer = backgroundRealm.createObject(RealmObjectPrayer.class);
                realmObjectPrayer.setPrayerID(Utilities.getPrayerNextID(backgroundRealm));
                realmObjectPrayer.setName(context.getString(R.string.maghrib));
                realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                realmObjectPrayer.setPlace(activePlaceId);
                realmObjectPrayer.setIndex(4);
                backgroundRealm.copyToRealmOrUpdate(realmObjectPrayer);
                backgroundRealm.commitTransaction();

                dayCal.set(calYear, calMonth, calDay,
                        Integer.valueOf(day.getTimings().getIsha().substring(0, 2)),
                        Integer.valueOf(day.getTimings().getIsha().substring(3, 5)));

                backgroundRealm.beginTransaction();
                realmObjectPrayer = backgroundRealm.createObject(RealmObjectPrayer.class);
                realmObjectPrayer.setPrayerID(Utilities.getPrayerNextID(backgroundRealm));
                realmObjectPrayer.setName(context.getString(R.string.isha));
                realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                realmObjectPrayer.setPlace(activePlaceId);
                realmObjectPrayer.setIndex(5);
                backgroundRealm.copyToRealmOrUpdate(realmObjectPrayer);
                backgroundRealm.commitTransaction();

            }
            backgroundRealm.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
