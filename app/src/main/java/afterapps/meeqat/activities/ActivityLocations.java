package afterapps.meeqat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

import afterapps.meeqat.R;
import afterapps.meeqat.adapters.PlacesRecyclerAdapter;
import afterapps.meeqat.datamodel.RealmPlace;
import afterapps.meeqat.datamodel.ReverseGeoResponse;
import afterapps.meeqat.helpers.RetrofitClients;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityLocations extends AppCompatActivity {

    private static final String GEO_LOG_TAG = "GeoLog";
    private static final int PLACES_FAB_FLAG = 1;
    @BindView(R.id.places_parent)
    View parent;
    @BindView(R.id.places_recycler)
    RecyclerView placesRecycler;
    @BindView(R.id.places_progress)
    View placeProgress;
    @BindView(R.id.activity_places_toolbar)
    Toolbar toolbar;

    RetrofitClients.ReverseGeoClient reverseGeoClient;
    Call<ReverseGeoResponse> reverseGeoCall;
    String apiKey;

    Realm realm;
    RealmResults<RealmPlace> places;
    PlacesRecyclerAdapter placesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        realm = Realm.getDefaultInstance();
        apiKey = getString(R.string.geo_api_key);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(R.string.activity_title_location);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initPlacesRecycler();
    }


    @Override
    protected void onDestroy() {
        realm.close();
        if (reverseGeoCall != null) {
            reverseGeoCall.cancel();
            reverseGeoCall = null;
        }
        super.onDestroy();
    }


    private void initPlacesRecycler() {
        placesRecyclerAdapter = new PlacesRecyclerAdapter(this,
                realm.where(RealmPlace.class).findAll());
        placesRecyclerAdapter.setHasStableIds(true);
        placesRecycler.setLayoutManager(new LinearLayoutManager(this));
        placesRecycler.setAdapter(placesRecyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_locations_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add:
                try {
                    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(autocompleteFilter)
                                    .build(ActivityLocations.this);
                    startActivityForResult(intent, PLACES_FAB_FLAG);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Snackbar.make(parent, R.string.play_services_error, Snackbar.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACES_FAB_FLAG && resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            handlePlaceSuccess(place);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handlePlaceSuccess(Place place) {
        showProgress();
        getTimezone(place);
    }

    private void showProgress() {
        placeProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        placeProgress.setVisibility(View.GONE);
    }

    private void addPlaceToRealm(Place place, ReverseGeoResponse reverseGeoResponse) {
        places = realm.where(RealmPlace.class).findAll();
        RealmPlace realmPlace = realm.where(RealmPlace.class).equalTo("id", place.getId()).findFirst();
        if (realmPlace == null) {
            realm.beginTransaction();
            realmPlace = realm.createObject(RealmPlace.class);
            realmPlace.setId(place.getId());
            realmPlace.setLatitude(place.getLatLng().latitude);
            realmPlace.setLongitude(place.getLatLng().longitude);
            realmPlace.setName(String.valueOf(place.getName()));
            realmPlace.setActive(false);
            realmPlace.setTimezone(reverseGeoResponse.getTimeZoneId());
            realm.copyToRealmOrUpdate(realmPlace);
            realm.commitTransaction();
            activatePlace(place.getId());
        }
    }

    private void activatePlace(String id) {
        places = realm.where(RealmPlace.class).findAll();
        realm.beginTransaction();
        for (int i = 0; i < places.size(); i++) {
            RealmPlace place = places.get(i);
            place.setActive(false);
            realm.copyToRealmOrUpdate(place);
        }
        realm.commitTransaction();
        realm.beginTransaction();
        RealmPlace place = realm.where(RealmPlace.class).equalTo("id", id).findFirst();
        place.setActive(true);
        realm.copyToRealmOrUpdate(place);
        realm.commitTransaction();
        finish();
    }

    public void handleRecyclerClick(String id) {
        activatePlace(id);
    }

    private void getTimezone(Place place) {
        final String BASE_URL = "https://maps.googleapis.com/";
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        reverseGeoClient = retrofit.create(RetrofitClients.ReverseGeoClient.class);

        //getting timezone
        String coordinates = place.getLatLng().latitude + "," + place.getLatLng().longitude;
        String timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
        reverseGeoCall = reverseGeoClient.getReverseGeo(apiKey,
                timestamp,
                coordinates);

        reverseGeoCall.enqueue(getTimezoneCallback(place));
    }

    private Callback<ReverseGeoResponse> getTimezoneCallback(final Place place) {
        return new Callback<ReverseGeoResponse>() {
            @Override
            public void onResponse(Call<ReverseGeoResponse> call, Response<ReverseGeoResponse> response) {
                hideProgress();
                Log.d(GEO_LOG_TAG, "response");
                if (response.isSuccessful()) {
                    addPlaceToRealm(place, response.body());
                }
            }

            @Override
            public void onFailure(Call<ReverseGeoResponse> call, Throwable t) {
                Log.d(GEO_LOG_TAG, "failure");
            }
        };
    }

    public void handleDeletionClick(String placeID) {
        places = realm.where(RealmPlace.class).findAll();
        RealmPlace place = places.where().equalTo("id", placeID).findFirst();
        Snackbar.make(parent,
                String.format(getString(R.string.place_deletion_message),
                        place.getName()), Snackbar.LENGTH_LONG).show();
        realm.beginTransaction();
        place.deleteFromRealm();
        realm.commitTransaction();
    }
}