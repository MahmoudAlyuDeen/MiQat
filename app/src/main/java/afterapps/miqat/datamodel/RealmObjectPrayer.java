package afterapps.miqat.datamodel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/*
 * Created by Mahmoud on 9/25/2016.
 */

public class RealmObjectPrayer extends RealmObject {
    private String name;
    private String englishName;
    private long timestamp;
    private int year;
    private int month;
    private int day;
    private String place;
    private int index;
    private int method;
    private int school;
    private int latitudeMethod;
    @PrimaryKey
    private long prayerID;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getSchool() {
        return school;
    }

    public void setSchool(int school) {
        this.school = school;
    }

    public int getLatitudeMethod() {
        return latitudeMethod;
    }

    public void setLatitudeMethod(int latitudeMethod) {
        this.latitudeMethod = latitudeMethod;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getPrayerID() {
        return prayerID;
    }

    public void setPrayerID(long prayerID) {
        this.prayerID = prayerID;
    }
}
