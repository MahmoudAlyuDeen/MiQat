package afterapps.meeqat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import afterapps.meeqat.fragments.FragmentPrayersContent;

/*
 * Created by Mahmoud on 9/14/2016.
 */

public class PrayersPagerAdapter extends FragmentStatePagerAdapter {

    public PrayersPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentPrayersContent.newInstance(position);
    }



    @Override
    public int getCount() {
        return 2;
    }
}