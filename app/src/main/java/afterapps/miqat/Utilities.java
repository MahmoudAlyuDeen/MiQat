package afterapps.miqat;


import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import afterapps.miqat.datamodel.Day;
import afterapps.miqat.datamodel.RealmObjectPrayer;
import io.realm.Realm;
import needle.Needle;

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

    public static void addDataToRealm(final List<Day> days, final String activePlaceId, final int method, final int school, final int latitudeMethod, final Context context) {
        Needle.onBackgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                Realm backgroundRealm = Realm.getDefaultInstance();
                for (Day day : days) {
                    Log.d("Needle", "doInBackground: " + day.getDate().getReadable());

                    Calendar dayCal = Calendar.getInstance();
                    long dayTimestamp = day.getDate().getTimestamp();
                    dayCal.setTimeInMillis(dayTimestamp * 1000);
                    int calDay = dayCal.get(Calendar.DAY_OF_MONTH);
                    int calMonth = dayCal.get(Calendar.MONTH);
                    int calYear = dayCal.get(Calendar.YEAR);

                    dayCal.set(calYear, calMonth, calDay,
                            Integer.valueOf(day.getTimings().getFajr().substring(0, 2)),
                            Integer.valueOf(day.getTimings().getFajr().substring(3, 5)));

                    backgroundRealm.beginTransaction();
                    RealmObjectPrayer realmObjectPrayer = backgroundRealm.createObject(RealmObjectPrayer.class);
                    realmObjectPrayer.setPrayerID(Utilities.getPrayerNextID(backgroundRealm));
                    realmObjectPrayer.setName(context.getString(R.string.fajr));
                    realmObjectPrayer.setEnglishName(context.getString(R.string.dawn));
                    realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                    realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                    realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                    realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                    realmObjectPrayer.setPlace(activePlaceId);
                    realmObjectPrayer.setMethod(method);
                    realmObjectPrayer.setSchool(school);
                    realmObjectPrayer.setLatitudeMethod(latitudeMethod);
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
                    realmObjectPrayer.setEnglishName(context.getString(R.string.sunrise));
                    realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                    realmObjectPrayer.setPlace(activePlaceId);
                    realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                    realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                    realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                    realmObjectPrayer.setMethod(method);
                    realmObjectPrayer.setSchool(school);
                    realmObjectPrayer.setLatitudeMethod(latitudeMethod);
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
                    realmObjectPrayer.setEnglishName(context.getString(R.string.noon));
                    realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                    realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                    realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                    realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                    realmObjectPrayer.setPlace(activePlaceId);
                    realmObjectPrayer.setMethod(method);
                    realmObjectPrayer.setSchool(school);
                    realmObjectPrayer.setLatitudeMethod(latitudeMethod);
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
                    realmObjectPrayer.setEnglishName(context.getString(R.string.afternoon));
                    realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                    realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                    realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                    realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                    realmObjectPrayer.setPlace(activePlaceId);
                    realmObjectPrayer.setMethod(method);
                    realmObjectPrayer.setSchool(school);
                    realmObjectPrayer.setLatitudeMethod(latitudeMethod);
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
                    realmObjectPrayer.setEnglishName(context.getString(R.string.sunset));
                    realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                    realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                    realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                    realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                    realmObjectPrayer.setPlace(activePlaceId);
                    realmObjectPrayer.setMethod(method);
                    realmObjectPrayer.setSchool(school);
                    realmObjectPrayer.setLatitudeMethod(latitudeMethod);
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
                    realmObjectPrayer.setEnglishName(context.getString(R.string.night));
                    realmObjectPrayer.setTimestamp(dayCal.getTimeInMillis());
                    realmObjectPrayer.setYear(dayCal.get(Calendar.YEAR));
                    realmObjectPrayer.setMonth(dayCal.get(Calendar.MONTH));
                    realmObjectPrayer.setDay(dayCal.get(Calendar.DAY_OF_MONTH));
                    realmObjectPrayer.setPlace(activePlaceId);
                    realmObjectPrayer.setMethod(method);
                    realmObjectPrayer.setSchool(school);
                    realmObjectPrayer.setLatitudeMethod(latitudeMethod);
                    realmObjectPrayer.setIndex(5);
                    backgroundRealm.copyToRealmOrUpdate(realmObjectPrayer);
                    backgroundRealm.commitTransaction();
                }
                backgroundRealm.close();
            }
        });
    }
}
