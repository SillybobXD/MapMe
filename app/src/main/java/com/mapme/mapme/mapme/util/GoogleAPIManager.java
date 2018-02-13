package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapme.mapme.mapme.util.data.obj.Photo;
import com.mapme.mapme.mapme.util.data.obj.Place;
import com.mapme.mapme.mapme.util.data.obj.Suggestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sillybob on 12/13/2017.
 */

public class GoogleAPIManager {
    //TAG
    private static final String TAG = GoogleAPIManager.class.getSimpleName();

    //URL base
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    //Types of requests
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_TEXT = "/textsearch";
    private static final String TYPE_NEARBY = "/nearbysearch";

    //Types of outputs
    private static final String OUT_JSON = "/json?";
    private static final String OUT_PHOTO = "/photo?";

    //API key (Debug)
    private static final String API_KEY = "AIzaSyD_YGBsh8Yb1EzyaUcARl1YZDXZVlEDRlI";

    //User location
    private static Location location;

    //Radius around location(m)
    private static double radius = 3000;

    //Volley HTTP request queue
    private static RequestQueueSingleton requestQueue;

    public static void init(Context context) {
        requestQueue = RequestQueueSingleton.getInstance(context);
        radius = SharedPreferencesManager.getRadius() * 1000;
    }

    public static void autoCompletePlace(String input, final IAutoCompleteResponse autoCompleteResponse) {
        String formattedInput = formatString(input);
        StringBuilder sb = new StringBuilder();
        sb.append(PLACES_API_BASE)
                .append(TYPE_AUTOCOMPLETE)
                .append(OUT_JSON)
                .append("input=" + formattedInput)
                .append("&type=establishment");
        //.append("&type=establishment");
        if (location != null)
            sb.append("&location=" + location.getLatitude() + "," + location.getLongitude())
                    .append("&radius=" + radius);
        //.append("&strictbounds");
        sb.append("&key=" + API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Create a JSON object hierarchy from the results
                    JSONArray predsJsonArray = response.getJSONArray("predictions");
                    ArrayList<Suggestion> resultList;
                    // Extract the Place descriptions from the results
                    resultList = new ArrayList<Suggestion>(predsJsonArray.length());
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                        JSONObject structuredFormatting = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting");
                        String mainText = structuredFormatting.getString("main_text");
                        String secondaryText = "";
                        if (structuredFormatting.has("secondary_text"))
                            secondaryText = structuredFormatting.getString("secondary_text");
                        String id = predsJsonArray.getJSONObject(i).getString("place_id");
                        resultList.add(new Suggestion(id, mainText, secondaryText));
                    }
                    autoCompleteResponse.onResponse(resultList);

                } catch (JSONException e) {
                    Log.e(TAG, "Cannot process JSON results", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                autoCompleteResponse.onFailure(error);
            }
        });

        requestQueue.addToRequestQueue(request);

    }

    public static void getPlaceByID(String id, final IGetSinglePlacesResponse getPlaceResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append(PLACES_API_BASE)
                .append(TYPE_DETAILS)
                .append(OUT_JSON)
                .append("placeid=" + id)
                .append("&key=" + API_KEY);

        Log.d(TAG, "getPlaceByID: " + sb.toString());

        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject resault = response.getJSONObject("result");
                    Place place = convertPlaceFromJSON(resault);
                    getPlaceResponse.onResponse(place);
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getPlaceResponse.onFailure(error);

            }
        });
        requestQueue.addToRequestQueue(request);
    }

    public static void getPlacePhoto(String reference, int width, final IGetPhotoResponse getPhotoResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append(PLACES_API_BASE)
                .append(OUT_PHOTO)
                .append("maxwidth=" + width)
                .append("&photoreference=" + reference)
                .append("&key=" + API_KEY);

        ImageRequest imageRequest = new ImageRequest(sb.toString(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.d(TAG, "onResponse: getPlacePhoto: " + response);
                        getPhotoResponse.onResponse(response);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getPhotoResponse.onFailuer(error);
                    }
                });

        requestQueue.addToRequestQueue(imageRequest);
    }

    public static void getPlacesByText(String query, final IGetPlacesResponse getPlacesResponse) {
        StringBuilder sb = new StringBuilder();
        String formattedQuery = formatString(query);
        sb.append(PLACES_API_BASE)
                .append(TYPE_TEXT)
                .append(OUT_JSON)
                .append("query=" + formattedQuery);
        //.append("&type=establishment");
        if (location != null)
            sb.append("&location=" + location.getLatitude() + "," + location.getLongitude())
                    .append("&radius=" + radius);
        sb.append("&key=" + API_KEY);

        Log.d(TAG, "getPlacesByText: " + sb.toString());

        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PlacesPage page;
                ArrayList<Place> places = new ArrayList<>();
                String nextPageToken = null;
                String status = null;
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        places.add(convertPlaceFromJSON(jsonArray.getJSONObject(i)));
                    }
                    if (response.has("next_page_token"))
                        nextPageToken = response.getString("next_page_token");
                    status = response.getString("status");
                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: " + e.toString());
                }
                page = new PlacesPage(places, status, nextPageToken);
                getPlacesResponse.onResponse(page);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getPlacesResponse.onFailure(error);
            }
        });

        requestQueue.addToRequestQueue(request);
    }

    public static void getNearbyPlaces(@Nullable String input, final IGetPlacesResponse getPlacesResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append(PLACES_API_BASE)
                .append(TYPE_NEARBY)
                .append(OUT_JSON);
        if (input != null && !input.isEmpty()) {
            String formattedInput = formatString(input);
            sb.append("keyword=" + formattedInput);
        }
        if (location != null)
            sb.append("&location=" + location.getLatitude() + "," + location.getLongitude())
                    .append("&radius=" + radius);
        sb.append("&key=" + API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PlacesPage page;
                ArrayList<Place> places = new ArrayList<>();
                String nextPageToken = null;
                String status = null;
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        places.add(convertPlaceFromJSON(jsonArray.getJSONObject(i)));
                    }
                    if (response.has("next_page_token"))
                        nextPageToken = response.getString("next_page_token");
                    status = response.getString("status");
                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: " + e.toString());
                }
                page = new PlacesPage(places, status, nextPageToken);
                getPlacesResponse.onResponse(page);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getPlacesResponse.onFailure(error);
            }
        });

        requestQueue.addToRequestQueue(request);
    }

    public static void getNextPage(String nextPageID) {
        StringBuilder sb = new StringBuilder();
        sb.append(PLACES_API_BASE)
                .append(TYPE_TEXT)
                .append(OUT_JSON)
                .append("pagetoken=" + nextPageID)
                .append("&key=" + API_KEY);
        JsonObjectRequest request = new JsonObjectRequest(sb.toString(), new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.addToRequestQueue(request);
    }

    public static void setLocation(Location location) {
        GoogleAPIManager.location = location;
    }

    public static double getRadius() {
        return radius;
    }

    public static void setRadius(double otherRadius) {
        radius = otherRadius;
    }

    private static Place convertPlaceFromJSON(JSONObject jasonPlace) {
        Place place = new Place();
        Log.d(TAG, "convertPlaceFromJSON: " + jasonPlace.toString());
        try {
            if (jasonPlace.has("place_id"))
                place.setId(jasonPlace.getString("place_id"));

            if (jasonPlace.has("formatted_address"))
                place.setAddress(jasonPlace.getString("formatted_address"));

            if (jasonPlace.has("formatted_phone_number"))
                place.setPhoneNumber(jasonPlace.getString("formatted_phone_number"));

            if (jasonPlace.has("geometry") && jasonPlace.getJSONObject("geometry").has("location")) {
                JSONObject jsonLocation = jasonPlace.getJSONObject("geometry").getJSONObject("location");
                Location location = new Location("");
                location.setLatitude(jsonLocation.getDouble("lat"));
                location.setLongitude(jsonLocation.getDouble("lng"));
                place.setLocation(location);
            }

            if (jasonPlace.has("icon"))
                place.setIconURL(jasonPlace.getString("icon"));

            if (jasonPlace.has("name"))
                place.setPlaceName(jasonPlace.getString("name"));

            if (jasonPlace.has("photos")) {
                JSONArray jsonPhotoArray = jasonPlace.getJSONArray("photos");
                for (int i = 0; i < jsonPhotoArray.length(); i++) {
                    Photo photo = new Photo();
                    JSONObject jsonPhoto = jsonPhotoArray.getJSONObject(i);
                    photo.setReference(jsonPhoto.getString("photo_reference"));
                    photo.setMaxHeight(jsonPhoto.getInt("height"));
                    photo.setMaxWidth(jsonPhoto.getInt("width"));
                    place.addPhoto(photo);
                }
            }

            if (jasonPlace.has("permanently_closed"))
                place.setPremClosed(jasonPlace.getBoolean("permanently_closed"));

            if (jasonPlace.has("rating"))
                place.setRating(jasonPlace.getDouble("rating"));

            if (jasonPlace.has("website"))
                place.setWebsite(jasonPlace.getString("website"));

            if (jasonPlace.has("formatted_phone_number "))
                place.setPhoneNumber(jasonPlace.getString("formatted_phone_number"));

        } catch (JSONException e) {
            Log.d(TAG, "convertPlaceFromJSON: " + e);
        }
        return place;
    }

    private static String formatString(String input) {
        String formattedInput = input.trim().replace(" ", "+");
        return formattedInput;
    }

    public interface IAutoCompleteResponse {
        void onResponse(ArrayList<Suggestion> response);
        void onFailure(VolleyError error);
    }

    public interface IGetPhotoResponse {
        void onResponse(Bitmap photo);
        void onFailuer(VolleyError error);
    }

    public interface IGetPlacesResponse {
        void onResponse(PlacesPage page);

        void onFailure(VolleyError error);
    }

    public interface IGetSinglePlacesResponse {
        void onResponse(Place place);

        void onFailure(VolleyError error);
    }

    public static class PlacesPage {
        private ArrayList<Place> places;
        private String status;
        private String nextPageKey;

        public PlacesPage(ArrayList<Place> places, String status, @Nullable String nextPageKey) {
            this.places = places;
            this.nextPageKey = nextPageKey;
            this.status = status;
        }

        public boolean isNextPageAvailable() {
            return nextPageKey != null || nextPageKey.isEmpty();
        }

        public String getNextPageKey() {
            return nextPageKey;
        }

        public ArrayList<Place> getPlaces() {
            return places;
        }

        public String getStatus() {
            return status;
        }

        public void addPage(PlacesPage nextPage) {
            places.addAll(nextPage.getPlaces());

            if (nextPage.isNextPageAvailable())
                nextPageKey = null;
            else
                nextPageKey = nextPage.getNextPageKey();
        }

        @Override
        public String toString() {
            return "PlacesPage{" +
                    "places=" + places +
                    ", status='" + status + '\'' +
                    ", nextPageKey='" + nextPageKey + '\'' +
                    '}';
        }
    }
}
