package com.mapme.mapme.mapme.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.CustomSuggestionAdapter;
import com.mapme.mapme.mapme.util.DrawerManager;
import com.mapme.mapme.mapme.util.GoogleAPIManager;
import com.mapme.mapme.mapme.util.SearchResultAdapter;
import com.mapme.mapme.mapme.util.data.obj.Place;
import com.mapme.mapme.mapme.util.data.obj.Suggestion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    //TAG
    private static final String TAG = MapActivity.class.getSimpleName();

    private static final int LOCATION_PERMISSION_REQUEST = 1337;
    private static final int GPS_ON_REQUEST = 1338;
    boolean isSuggestionClicked;
    //Location
    private LocationManager mLocationManager;
    private Location mCurrLocation;
    private boolean isLocationOn;
    private FollowMeLocationSource followMeLocationSource;
    private BroadcastReceiver gpsListener;
    private GoogleMap mMap;
    private MaterialSearchBar.OnSearchActionListener mSearchActionListener;
    private TextWatcher mSearchTextListener;
    private GoogleAPIManager.PlacesPage searchResultsPage;
    private Circle posCircle;
    private ArrayList<Marker> markers;
    private Marker lastMarkerClicked;

    //UI elements
    private MaterialSearchBar mSearchBar;
    private FloatingActionButton mGPS;
    private SupportMapFragment mMapFragment;
    private ListView mSearchResults;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Init helper objects
        DrawerManager.makeDrawer(this);
        GoogleAPIManager.init(this);

        isLocationOn = false;
        isSuggestionClicked = false;
        followMeLocationSource = new FollowMeLocationSource();
        markers = new ArrayList<>();

        //UI elements
        mSearchBar = findViewById(R.id.mapActivity_sb);
        mGPS = findViewById(R.id.mapActivity_fab_gps);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapActivity_frag_googleMapFrag);
        mSearchResults = findViewById(R.id.lv_results);
        mSlidingUpPanelLayout = findViewById(R.id.sliding_layout);

        //Animation
        YoYo.with(Techniques.StandUp).playOn(mSearchBar);
        YoYo.with(Techniques.Tada).duration(3500).playOn(mGPS);

        //Init searchbar
        initSearchbarElements();

        //Init fab
        mGPS.setOnClickListener(this);

        gpsListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isLocationOn) {
                        toggleGPS();
                    }
                }
            }
        };

        mMapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(gpsListener, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        checkPermissions();
    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            initLocationService();
        }
    }

    private void initLocationService() {
        //Location
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        mCurrLocation = getLastKnownLocation();

        Log.d(TAG, "initLocationService: Current Location : " + mCurrLocation);
        GoogleAPIManager.setLocation(mCurrLocation);
    }

    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        try {
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getLastKnownLocation: " + e.getMessage());
        }
        return bestLocation;
    }

    private void initSearchbarElements() {
        mSearchActionListener = new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Log.d(TAG, "onSearchConfirmed: ");
                hideKeyboard(mSearchBar);
                for (Marker marker : markers) {
                    marker.remove();
                }
                if (isLocationOn)
                    GoogleAPIManager.getNearbyPlaces(text.toString(), new GoogleAPIManager.IGetPlacesResponse() {
                        @Override
                        public void onResponse(GoogleAPIManager.PlacesPage page) {
                            Log.d(TAG, "onResponse: " + page);
                            switch (page.getStatus()) {
                                case "OK":
                                    searchResultsPage = page;
                                    ArrayList<Place> places = page.getPlaces();
                                    mSearchResults.setAdapter(new SearchResultAdapter(MapActivity.this, places) {
                                        @NonNull
                                        @Override
                                        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            view.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Place place = searchResultsPage.getPlaces().get(position);
                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude()), 18));
                                                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                                }
                                            });
                                            return view;
                                        }
                                    });
                                    for (Place place : places) {
                                        addMarker(place);
                                    }
                                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                    break;

                                case "ZERO_RESULTS":
                                    Toast.makeText(MapActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    Toast.makeText(MapActivity.this, "Error in server request", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                        }

                        @Override
                        public void onFailure(VolleyError error) {

                        }
                    });
                else
                    GoogleAPIManager.getPlacesByText(text.toString(), new GoogleAPIManager.IGetPlacesResponse() {
                        @Override
                        public void onResponse(GoogleAPIManager.PlacesPage page) {
                            switch (page.getStatus()) {
                                case "OK":
                                    Log.d(TAG, "onResponse: " + page);
                                    searchResultsPage = page;
                                    ArrayList<Place> places = page.getPlaces();
                                    mSearchResults.setAdapter(new SearchResultAdapter(MapActivity.this, places) {
                                        @NonNull
                                        @Override
                                        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            view.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Place place = searchResultsPage.getPlaces().get(position);
                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude()), 18));
                                                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                                }
                                            });
                                            return view;
                                        }
                                    });
                                    for (Place place : places) {
                                        addMarker(place);
                                    }
                                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                    break;

                                case "ZERO_RESULTS":
                                    Toast.makeText(MapActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    Toast.makeText(MapActivity.this, "Error in server request", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(VolleyError error) {

                        }
                    });
            }

            @Override
            public void onButtonClicked(int buttonCode) {

                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        DrawerManager.openDrawer();
                        break;
                    case MaterialSearchBar.BUTTON_BACK:
                        mSearchBar.disableSearch();
                        break;
                }

            }
        };
        CustomSuggestionAdapter adapter = new CustomSuggestionAdapter(getLayoutInflater());
        adapter.setListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                for (Marker marker : markers) {
                    marker.remove();
                }
                isSuggestionClicked = true;
                hideKeyboard(mSearchBar);
                Suggestion suggestion = (Suggestion) mSearchBar.getLastSuggestions().get(position);
                String text = suggestion.toString();
                mSearchBar.setText(text);
                GoogleAPIManager.getPlaceByID(suggestion.getId(), new GoogleAPIManager.IGetSinglePlacesResponse() {
                    @Override
                    public void onResponse(Place place) {
                        addMarker(place);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude()), 500));
                    }

                    @Override
                    public void onFailure(VolleyError error) {

                    }
                });
                mSearchBar.hideSuggestionsList();
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
        mSearchTextListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isSuggestionClicked)
                    GoogleAPIManager.autoCompletePlace(charSequence.toString(), new GoogleAPIManager.IAutoCompleteResponse() {
                        @Override
                        public void onResponse(ArrayList<Suggestion> response) {
                            Log.d(TAG, response.toString());
                            mSearchBar.updateLastSuggestions(response);
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
        };
        mSearchBar.setCustomSuggestionAdapter(adapter);
        mSearchBar.addTextChangeListener(mSearchTextListener);
        mSearchBar.setOnSearchActionListener(mSearchActionListener);
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
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setLocationSource(followMeLocationSource);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        // Position the map's camera near Sydney, Australia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));
    }

    private void addMarker(Place place) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude()))
                .title(place.getPlaceName());
        Marker marker = mMap.addMarker(markerOptions);
        markers.add(marker);
        marker.setTag(place);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.getId());
        switch (view.getId()) {
            case (R.id.mapActivity_fab_gps):
                toggleGPS();
                break;
        }
    }

    private void toggleGPS() {
        isLocationOn = !isLocationOn;
        if (isLocationOn) {
            Log.d(TAG, "location on");
            //Enable GPS
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(viewIntent, GPS_ON_REQUEST);
                return;
            }
            startLocationUpdates();
        } else {
            Log.d(TAG, "location off");
            //Disable GPS
            stopLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.d(TAG, "startLocationUpdates: " + e);
        }
        //mGPS.getDrawable().mutate().setTint(ContextCompat.getColor(this, R.color.gps_active));
    }

    private void stopLocationUpdates() {
        try {
            mMap.setMyLocationEnabled(false);
        } catch (SecurityException e) {
            Log.d(TAG, "startLocationUpdates: " + e);
        }
        //mGPS.getDrawable().mutate().setTint(ContextCompat.getColor(this, R.color.gps_not_active));
    }

    @Override
    protected void onResume() {
        super.onResume();
        followMeLocationSource.getBestAvailableProvider();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(gpsListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ON_REQUEST:
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startLocationUpdates();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mLocationManager == null)
                        initLocationService();
                } else
                    checkPermissions();
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        /*if (lastMarkerClicked == null || lastMarkerClicked != marker) {
            Log.d(TAG, "onMarkerClick: false");
            return false;
        } else {
            Log.d(TAG, "onMarkerClick: true");
            showPlaceView((Place) marker.getTag());
            return true;
        }
        */
        Log.d(TAG, "onMarkerClick: " + marker.getTag());
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick: " + marker.getTag());
        GoogleAPIManager.getPlaceByID(((Place) marker.getTag()).getId(), new GoogleAPIManager.IGetSinglePlacesResponse() {
            @Override
            public void onResponse(Place place) {
                showPlaceView(place);
            }

            @Override
            public void onFailure(VolleyError error) {

            }
        });
    }

    public void showPlaceView(Place place) {
        Log.d(TAG, "showPlaceView");
        View popupView = getLayoutInflater().inflate(R.layout.selected_place_fragment, null);

        RelativeLayout mainLayout = findViewById(R.id.mapActivity_cl_parent);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.place_popup_window);

        // show the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        /*popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });*/

        // Initialize more widgets from `popup_layout.xml`
        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        final ImageView placeImage = popupView.findViewById(R.id.iv_selected_place_fragment);
        TextView placeName = popupView.findViewById(R.id.tv_name_selected_place_fragment);
        TextView placeAddress = popupView.findViewById(R.id.tv_address_selected_place_fragment);
        TextView placeNumber = popupView.findViewById(R.id.tv_phonenum_selected_place_fragment);
        TextView placeWebsite = popupView.findViewById(R.id.tv_website_selected_place_fragment);
        RatingBar ratingBar = popupView.findViewById(R.id.ratingBar);

        if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
            GoogleAPIManager.getPlacePhoto(place.getPhotos().get(0).getReference(),
                    Math.max(place.getPhotos().get(0).getMaxWidth(), placeImage.getMaxWidth()),
                    new GoogleAPIManager.IGetPhotoResponse() {
                        @Override
                        public void onResponse(Bitmap photo) {
                            placeImage.setImageBitmap(photo);
                        }

                        @Override
                        public void onFailuer(VolleyError error) {

                        }
                    });
        }

        placeName.setText(place.getPlaceName());
        placeAddress.setText(place.getAddress());
        if (place.hasPhoneNumber())
            placeNumber.setText(place.getPhoneNumber());
        if (place.hasWebsite())
            placeWebsite.setText(place.getWebsite());
        ratingBar.setRating((float) place.getRating());

        // Using location, the PopupWindow will be displayed right under anchorView
        /*popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                location[0], location[1] + anchorView.getHeight());*/
    }

    /* Our custom LocationSource.
     * We register this class to receive location updates from the Location Manager
     * and for that reason we need to also implement the LocationListener interface. */
    private class FollowMeLocationSource implements LocationSource, LocationListener {

        private final Criteria criteria = new Criteria();
        /* Updates are restricted to one every 10 seconds, and only when
         * movement of more than 10 meters has been detected.*/
        private final int minTime = 10000;     // minimum time interval between location updates, in milliseconds
        private final int minDistance = 0;    // minimum distance between location updates, in meters
        private OnLocationChangedListener mListener;
        private LocationManager locationManager;
        private String bestAvailableProvider;
        private boolean isFirstUpdate = true;

        private FollowMeLocationSource() {
            // Get reference to Location Manager
            locationManager = (LocationManager) MapActivity.this.getSystemService(Context.LOCATION_SERVICE);

            // Specify Location Provider criteria
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(true);
            criteria.setBearingRequired(true);
            criteria.setSpeedRequired(true);
            criteria.setCostAllowed(true);
        }

        private void getBestAvailableProvider() {
            /* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use
             * is to ask the Location Manager for the one that best satisfies our criteria.
             * By passing the 'true' boolean we ask for the best available (enabled) provider. */
            bestAvailableProvider = locationManager.getBestProvider(criteria, true);
        }

        /* Activates this provider. This provider will notify the supplied listener
         * periodically, until you call deactivate().
         * This method is automatically invoked by enabling my-location layer. */
        @Override
        public void activate(OnLocationChangedListener listener) {
            // We need to keep a reference to my-location layer's listener so we can push forward
            // location updates to it when we receive them from Location Manager.
            mListener = listener;

            // Request location updates from Location Manager
            if (bestAvailableProvider != null) {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance, this);
                mGPS.getDrawable().mutate().setTint(ContextCompat.getColor(MapActivity.this, R.color.gps_activating));
            } else {
                // (Display a message/dialog) No Location Providers currently available.
            }
        }

        /* Deactivates this provider.
         * This method is automatically invoked by disabling my-location layer. */
        @Override
        public void deactivate() {
            // Remove location updates from Location Manager
            locationManager.removeUpdates(this);
            mGPS.getDrawable().mutate().setTint(ContextCompat.getColor(MapActivity.this, R.color.gps_not_active));
            mListener = null;
            isFirstUpdate = true;
            if (posCircle != null)
                posCircle.remove();
        }

        @Override
        public void onLocationChanged(Location location) {
            /* Push location updates to the registered listener..
             * (this ensures that my-location layer will set the blue dot at the new/received location) */
            if (mListener != null) {
                mListener.onLocationChanged(location);
                mGPS.getDrawable().mutate().setTint(ContextCompat.getColor(MapActivity.this, R.color.gps_active));
                drawRadius(location);
                if (isFirstUpdate) {
                    isFirstUpdate = false;
                    float zoomLvl = getZoomLevel(posCircle);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLvl));
                }
                GoogleAPIManager.setLocation(location);
            }
            /* ..and Animate camera to center on that location !
             * (the reason for we created this custom Location Source !) */
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        private void drawRadius(Location location) {
            if (posCircle != null)
                posCircle.remove();
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(new LatLng(location.getLatitude(), location.getLongitude()))
                    .strokeColor(Color.argb(Integer.parseInt("E6", 16), 20, 80, 255))
                    .strokeWidth(1.5f)
                    .fillColor(Color.argb(40, 20, 80, 255))
                    .radius(GoogleAPIManager.getRadius());
            posCircle = mMap.addCircle(circleOptions);
        }

        public float getZoomLevel(Circle circle) {
            float zoomLevel = 0;
            if (circle != null) {
                double radius = circle.getRadius();
                double scale = radius / 500;
                zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
            }
            return zoomLevel - .2f;
        }
    }

}
