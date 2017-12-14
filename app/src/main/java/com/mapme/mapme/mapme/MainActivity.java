package com.mapme.mapme.mapme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mapme.mapme.mapme.util.CustomSuggestionAdapter;
import com.mapme.mapme.mapme.util.GoogleAPIManager;
import com.mapme.mapme.mapme.util.Suggestion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int PREMISSION_CODE = 20;
    LocationManager locationManager;
    LocationListener locationListener;
    Location currLocation;
    MaterialSearchBar searchBar;
    CustomSuggestionAdapter suggestionAdapter;
    List<Suggestion> suggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleAPIManager.init(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PREMISSION_CODE);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                currLocation = location;
                Log.d(TAG, "onLocationChanged: " + location.toString());
                GoogleAPIManager.setLocation(location);
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
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 30, 0, locationListener);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setRoundedSearchBarEnabled(true);

        suggestionAdapter = new CustomSuggestionAdapter(getLayoutInflater());
        suggestions = new ArrayList<>();
        searchBar.setCustomSuggestionAdapter(suggestionAdapter);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GoogleAPIManager.autoCompletePlace(charSequence.toString(), new GoogleAPIManager.IAutoCompleteResponse() {
                    @Override
                    public void onResponse(ArrayList<Suggestion> response) {
                        suggestions.clear();
                        suggestions.addAll(response);
                        Log.d(TAG, suggestions.toString());
                        searchBar.updateLastSuggestions(suggestions);
                    }

                    @Override
                    public void onFailure(VolleyError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        suggestionAdapter.setListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                Toast.makeText(MainActivity.this, "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });


/*
        queue = Volley.newRequestQueue(this);
        String url ="https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Pizza&types=establishment&location=37.76999,-122.44696&radius=500&key=AIzaSyDhHcrTpAfUIwo_R3XdL3L7x2vniELnXxE";

// Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null)
                    Log.d(TAG, "ERROR: " + error.toString());
                else
                    Log.d(TAG, "ERROR: null");
            }
        });
// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);*/

    }


}
