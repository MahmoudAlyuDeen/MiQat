package afterapps.miqat.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.aboutlibraries.LibsBuilder;

import afterapps.miqat.R;

/*
 * Created by Mahmoud on 10/6/2016.
 */

public class ActivityPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new FragmentPreferences()).commit();
    }


    private void setupToolbar() {
        ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root);

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.preferences_toolbar, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.activity_preferences_toolbar);
            setSupportActionBar(toolbar);
            setTitle(R.string.action_settings);
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public static class FragmentPreferences extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            setupAcknowledgementButtons();
        }

        private void setupAcknowledgementButtons() {
            Preference libraries = findPreference(getString(R.string.preference_key_libraries));
            libraries.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new LibsBuilder().withActivityTheme(R.style.ToolbarTheme)
                            .withAutoDetect(true)
                            .withFields(R.string.class.getFields())
                            .start(getActivity());
                    return true;
                }
            });

            Preference api = findPreference(getString(R.string.preference_key_api));
            api.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.about_api_url))));
                    return true;
                }
            });
        }
    }
}