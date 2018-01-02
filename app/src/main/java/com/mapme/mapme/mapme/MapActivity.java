package com.mapme.mapme.mapme;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mapme.mapme.mapme.util.CustomSuggestionAdapter;
import com.mapme.mapme.mapme.util.GoogleAPIManager;
import com.mapme.mapme.mapme.util.data.obj.Suggestion;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = MapActivity.class.getSimpleName();
    LocationManager locationManager;
    LocationListener locationListener;
    private State screenState;
    private GoogleMap mMap;
    private ConstraintLayout mParentLayout;
    private MaterialSearchBar mLoacionSearchBar;
    private MaterialSearchBar mPlaceSearchBar;
    private FloatingActionButton mGPS;
    private FloatingActionButton mPlaceSearch;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        GoogleAPIManager.init(this);
        Location currLocation = new Location("");
        currLocation.setLatitude(32.477221);
        currLocation.setLongitude(34.962719);
        GoogleAPIManager.setLocation(currLocation);

        mParentLayout = (ConstraintLayout) findViewById(R.id.mapActivity_cl_parent);
        mLoacionSearchBar = (MaterialSearchBar) findViewById(R.id.mapActivity_sb_location);
        mPlaceSearchBar = (MaterialSearchBar) findViewById(R.id.mapActivity_sb_gps);
        mGPS = (FloatingActionButton) findViewById(R.id.mapActivity_fab_gps);
        mPlaceSearch = (FloatingActionButton) findViewById(R.id.mapActivity_fab_search);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapActivity_frag_googleMapFrag);

        final MaterialSearchBar.OnSearchActionListener actionListener = new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //GoogleAPIManager.getLocationByText(text);
                GoogleAPIManager.getLocationByText(text.toString(), new GoogleAPIManager.IGetLocationResponse() {
                    @Override
                    public void onResponse(Location location) {
                        Log.d(TAG, "onResponse: Location is " + location.toString());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                        mLoacionSearchBar.hideSuggestionsList();
                        HideKeyboard(mLoacionSearchBar);
                        //mLoacionSearchBar.setText();
                    }

                    @Override
                    public void onFailure(VolleyError error) {

                    }
                });
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        };
        final CustomSuggestionAdapter adapter = new CustomSuggestionAdapter(getLayoutInflater());
        adapter.setListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                String text = mLoacionSearchBar.getLastSuggestions().get(position).toString();
                mLoacionSearchBar.setText(text);
                mLoacionSearchBar.hideSuggestionsList();
                actionListener.onSearchConfirmed(text);
                /*GoogleAPIManager.getLocation(((Suggestion) mLoacionSearchBar.getLastSuggestions().get(position)).getId(), new GoogleAPIManager.IGetLocationResponse() {
                    @Override
                    public void onResponse(Location location) {
                        Log.d(TAG, "onResponse: Location is " + location.toString());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                        mLoacionSearchBar.hideSuggestionsList();
                        HideKeyboard(mLoacionSearchBar);
                        mLoacionSearchBar.setText();
                    }

                    @Override
                    public void onFailure(VolleyError error) {

                    }
                });*/
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
        mLoacionSearchBar.setCustomSuggestionAdapter(adapter);
        mLoacionSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GoogleAPIManager.autoCompletePlace(charSequence.toString(), new GoogleAPIManager.IAutoCompleteResponse() {
                    @Override
                    public void onResponse(ArrayList<Suggestion> response) {
                        Log.d(TAG, response.toString());
                        mLoacionSearchBar.updateLastSuggestions(response);
                    }

                    @Override
                    public void onFailure(VolleyError error) {
                        Log.d(TAG, "onFailure: " + error.toString());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mLoacionSearchBar.setOnSearchActionListener(actionListener);

        screenState = State.MapFocus;
        /*mPlaceSearchBar.setVisibility(View.INVISIBLE);
        mPlaceSearchBar.setOnClickListener(this);
        mPlaceSearch.setOnClickListener(this);
        //mLoacionSearchBar.setOnClickListener(this);
        */
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        // Position the map's camera near Sydney, Australia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));
    }

    private void focusMap() {
        if (screenState == State.MapFocus)
            return;
        mPlaceSearch.show();
        mPlaceSearchBar.setVisibility(View.INVISIBLE);
    }

    private void focusLocationSearchBar() {
        if (screenState == State.LocationSeachbarFocus)
            return;
        mPlaceSearch.show();
        mPlaceSearchBar.setVisibility(View.INVISIBLE);
    }

    private void focusPlaceSearchBar() {
        Log.d(TAG, "focusPlaceSearchBar: lunched");
        if (screenState == State.PlaceSearchbarFocus)
            return;
        screenState = State.PlaceSearchbarFocus;
        mPlaceSearch.hide();
        mPlaceSearchBar.setVisibility(View.VISIBLE);
    }

    private void HideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: lunched");
        switch (view.getId()) {
            case (R.id.mapActivity_sb_location):
                focusLocationSearchBar();
                break;
            case (R.id.mapActivity_fab_gps):
                break;
            case (R.id.mapActivity_fab_search):
                focusPlaceSearchBar();
                break;
            case (R.id.mapActivity_frag_googleMapFrag):
                focusMap();
                break;
        }
    }

    private enum State {
        MapFocus,
        LocationSeachbarFocus,
        PlaceSearchbarFocus
    }
}
