package afterapps.meeqat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import afterapps.meeqat.fragments.FragmentPrayersContent;

/*
 * Created by Mahmoud on 9/14/2016.
 */

public class PrayersPagerAdapter extends FragmentStatePagerAdapter {

    public PrayersPagerAdapter(FragmentManager fm, DotIndicator dotIndicator) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new FragmentPrayersContent(position);
    }



    @Override
    public int getCount() {
        return 2;
    }
}