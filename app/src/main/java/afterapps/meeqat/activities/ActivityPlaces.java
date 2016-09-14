package afterapps.meeqat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import afterapps.meeqat.R;
import afterapps.meeqat.adapters.PlacesRecyclerAdapter;
import afterapps.meeqat.datamodel.RealmPlace;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityPlaces extends AppCompatActivity {

    @BindView(R.id.places_parent)
    View parent;
    @BindView(R.id.places_recycler)
    RecyclerView placesRecycler;
    Realm realm;
    RealmResults<RealmPlace> places;
    PlacesRecyclerAdapter placesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        realm = Realm.getDefaultInstance();

        ButterKnife.bind(this);

        initPlacesFragment();
        initPlacesRecycler();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    private void initPlacesFragment() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(getOnPlaceSelectedListener());
    }

    private void initPlacesRecycler() {
        placesRecyclerAdapter = new PlacesRecyclerAdapter(this,
                realm.where(RealmPlace.class).findAll());
        placesRecycler.setLayoutManager(new LinearLayoutManager(this));
        placesRecycler.setAdapter(placesRecyclerAdapter);
    }

    private PlaceSelectionListener getOnPlaceSelectedListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                handlePlaceSuccess(place);
            }

            @Override
            public void onError(Status status) {
                Log.e("Places", "An error occurred: " + status);
            }
        };
    }

    private void handlePlaceSuccess(Place place) {
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
            realm.copyToRealmOrUpdate(realmPlace);
            realm.commitTransaction();
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
        Log.e ("activatePlace", place.getName() + " state: " + place.isActive());
        realm.copyToRealmOrUpdate(place);
        realm.commitTransaction();
    }

    public void handleRecyclerClick(String id) {
        activatePlace(id);
    }
}