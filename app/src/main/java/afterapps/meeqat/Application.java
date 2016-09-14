package afterapps.meeqat;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/*
 * Created by Mahmoud on 9/14/2016.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        config.shouldDeleteRealmIfMigrationNeeded();
        Realm.setDefaultConfiguration(config);
    }
}
