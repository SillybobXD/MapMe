package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sillybob on 12/13/2017.
 */

public class GoogleAPIManager {
    private static final String TAG = GoogleAPIManager.class.getSimpleName();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autoCompletePlace";
    private static final String OUT_JSON = "/json?";

    private static final String API_KEY = "AIzaSyDhHcrTpAfUIwo_R3XdL3L7x2vniELnXxE";

    private static Location location;

    private static double radius = 3000;

    private static RequestQueueSingleton requestQueue;

    public static void init(Context context) {
        requestQueue = RequestQueueSingleton.getInstance(context);
    }

    public static void autoCompletePlace(String input, final IAutoCompleteResponse autoCompleteResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append(PLACES_API_BASE)
                .append(TYPE_AUTOCOMPLETE)
                .append(OUT_JSON)
                .append("input=" + input)
                .append("&type=establishment");
        if (location != null)
            sb.append("&location=" + location.getLatitude() + "," + location.getLongitude())
                    .append("&radius=" + radius)
                    .append("&strictbounds");
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
                        resultList.add(new Suggestion(mainText, secondaryText));
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

    public static void getPlace(String id, final IGetPlaceResponse getPlace) {

    }

    public static Location getLocation() {
        return location;
    }

    public static void setLocation(Location location) {
        GoogleAPIManager.location = location;
    }

    public interface IAutoCompleteResponse {
        void onResponse(ArrayList<Suggestion> response);

        void onFailure(VolleyError error);
    }

    public interface IGetPlaceResponse {
        void onResponse(Place place);

        void onFailure(VolleyError error);
    }
}
