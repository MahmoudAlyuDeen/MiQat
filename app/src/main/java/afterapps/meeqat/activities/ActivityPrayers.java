package afterapps.meeqat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import afterapps.meeqat.R;
import afterapps.meeqat.adapters.PrayersPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityPrayers extends AppCompatActivity {

    @BindView(R.id.prayers_view_pager)
    ViewPager prayersViewPager;
    @BindView(R.id.activity_prayers_toolbar)
    Toolbar toolbar;
    @BindView(R.id.prayers_dot_indicator)
    DotIndicator dotIndicator;

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
        prayersViewPager.setAdapter(new PrayersPagerAdapter(getSupportFragmentManager(), dotIndicator));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_location:
                Intent intent = new Intent(this, ActivityPlaces.class);
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
}
