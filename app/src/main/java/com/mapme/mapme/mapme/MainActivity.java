package com.mapme.mapme.mapme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.mapme.mapme.mapme.util.CustomSuggestionAdapter;
import com.mapme.mapme.mapme.util.GoogleAPIManager;
import com.mapme.mapme.mapme.util.data.obj.Place;
import com.mapme.mapme.mapme.util.data.obj.Suggestion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int PREMISSION_CODE = 20;
    LocationManager locationManager;
    LocationListener locationListener;
    Location currLocation = new Location("");
    MaterialSearchBar searchBar;
    CustomSuggestionAdapter suggestionAdapter;
    List<Suggestion> suggestions;
    TextView textView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleAPIManager.init(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PREMISSION_CODE);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        currLocation.setLatitude(32.477221);
        currLocation.setLongitude(34.962719);
        GoogleAPIManager.setLocation(currLocation);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                currLocation = location;
                Log.d(TAG, "onLocationChanged: " + location.toString());
                GoogleAPIManager.setLocation(currLocation);
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 1, 0, locationListener);

        searchBar = findViewById(R.id.mapActivity_sb_location);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView2);
        searchBar.setRoundedSearchBarEnabled(true);

        ArrayList<Suggestion> arrayList = new ArrayList<Suggestion>();
        arrayList.add(new Suggestion("1", "Main", "Sub"));
        searchBar.setLastSuggestions(arrayList);

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
                        Log.d(TAG, "onFailure: " + error.toString());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(final CharSequence text) {
                GoogleAPIManager.getPlacesByText(text.toString(), new GoogleAPIManager.IGetPlacesTextResponse() {
                    @Override
                    public void onResponse(ArrayList<Place> places) {
                        Log.d(TAG, "onResponse: " + places);
                        for (Place place : places) {
                            textView.setText(textView.getText() + "\n" + place.getPlaceName());
                        }
                    }

                    @Override
                    public void onFailure(VolleyError error) {
                        Log.d(TAG, "onFailure: " + error.toString());
                    }
                });
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        suggestionAdapter.setListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                Toast.makeText(MainActivity.this, "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
                GoogleAPIManager.getPlace(((Suggestion) searchBar.getLastSuggestions().get(position)).getId(), new GoogleAPIManager.IGetPlaceResponse() {
                    @Override
                    public void onResponse(Place place) {
                        Log.d(TAG, "onResponse: " + place.toString());
                        textView.setText(place.toString());
                        if (place.getPhotos() != null)
                            GoogleAPIManager.getPlacePhoto(place.getPhotos().get(0).getReference(), 600, new GoogleAPIManager.IGetPhotoResponse() {
                                @Override
                                public void onResponse(Bitmap photo) {
                                    imageView.setImageBitmap(photo);
                                }

                                @Override
                                public void onFailuer(VolleyError error) {

                                }
                            });
                    }

                    @Override
                    public void onFailure(VolleyError error) {
                        Log.d(TAG, "onFailure: " + error.toString());
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });


        DrawerManager.makeDrawer(MainActivity.this);

        Button btn_openDrawer = findViewById(R.id.btn_menu_mainActivity);
        btn_openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerManager.openDrawer();


            }
        });


    }


}

