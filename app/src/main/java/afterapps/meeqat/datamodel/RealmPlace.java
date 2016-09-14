package afterapps.meeqat.datamodel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/*
 * Created by Mahmoud on 9/14/2016.
 */
public class RealmPlace extends RealmObject {
    @PrimaryKey
    String id;
    String name;
    String timezone;
    double longitude;
    double latitude;
    boolean active;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
