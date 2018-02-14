package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.data.obj.Place;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Sillybob on 1/15/2018.
 */

public class SearchResultAdapter extends ArrayAdapter<Place> {
    Context context;
    FavoriteManager favoriteManager;
    ArrayList<Place> places;
    Location currlocation;

    public SearchResultAdapter(@NonNull Context context, @NonNull ArrayList<Place> places) {
        super(context, 0, places);
        this.context = context;
        this.places = places;
        favoriteManager = new FavoriteManager(context);
    }


    public SearchResultAdapter(@NonNull Context context, @NonNull ArrayList<Place> places, Location location) {
        this(context, places);
        currlocation = location;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final Place place = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_results_cv, parent, false);
        }

        TextView placeName = convertView.findViewById(R.id.tv_place_name);
        TextView placeAddress = convertView.findViewById(R.id.tv_place_address);
        TextView distance = convertView.findViewById(R.id.tv_distance);
        final ImageView placeImage = convertView.findViewById(R.id.iv_place_image);
        /*final ImageView placeFavorite = convertView.findViewById(R.id.btn_place_favorite);

        favoriteManager.isPlaceFavorite(place.getId(), new FavoriteManager.IIsFavoriteResult() {
            @Override
            public void result(boolean result) {
                if (result){
                    placeFavorite.getDrawable().mutate().setTint(ContextCompat.getColor(context, R.color.favorite_active));
                }
                else {
                    placeFavorite.getDrawable().mutate().setTint(ContextCompat.getColor(context, R.color.favorite_not_active));
                }
            }
        });

        placeFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteManager.isPlaceFavorite(place.getId(), new FavoriteManager.IIsFavoriteResult() {
                    @Override
                    public void result(boolean result) {
                        if (result){
                            favoriteManager.removeFavorite(place.getId());
                            Log.d("SearchResultsAdapter", "result: Removing from favorites" );
                            placeFavorite.getDrawable().mutate().setTint(ContextCompat.getColor(context, R.color.favorite_not_active));
                        }
                        else {
                            favoriteManager.addFavorite(place);
                            Log.d("SearchResultsAdapter", "result: adding from favorites" );
                            placeFavorite.getDrawable().mutate().setTint(ContextCompat.getColor(context, R.color.favorite_active));
                        }
                    }
                });
            }
        });*/

        placeName.setText(place.getPlaceName());
        placeAddress.setText(place.getAddress());
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

        if (currlocation != null) {
            double d = place.getLocation().distanceTo(currlocation);
            switch (SharedPreferencesManager.getUnits()) {
                case "Km":
                    if (d < 1000)
                        distance.setText(String.valueOf((int) d + " meter"));
                    else
                        distance.setText(new DecimalFormat("#.#").format(metersToKm(d)) + " Km");
                    break;

                case "Mile":
                    d = metersToFeet(d);
                    if (d < 5280)
                        distance.setText(String.valueOf((int) d) + " feet");
                    else
                        distance.setText(String.valueOf(new DecimalFormat("#.#").format(feetToMile(d)) + " Mile"));
                    break;
            }

        } else
            distance.setVisibility(View.GONE);

        return convertView;
    }

    private double metersToFeet(double meters) {
        return meters * 3.2808399;
    }

    private double feetToMile(double feet) {
        return feet / 5280;
    }

    private double metersToKm(double meters) {
        return meters / 1000;
    }

}
