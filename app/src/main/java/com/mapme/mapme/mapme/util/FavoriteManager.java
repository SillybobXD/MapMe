package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.mapme.mapme.mapme.util.data.obj.Place;
import com.mapme.mapme.mapme.util.room.AppDatabase;
import com.mapme.mapme.mapme.util.room.PlaceFavorite;

public class FavoriteManager {

    private Context context;
    private AppDatabase database;

    public FavoriteManager(Context context) {
        database = AppDatabase.getAppDatabase(context);
        this.context = context;
    }

    public void isPlaceFavorite(String id, IIsFavoriteResult iIsFavoriteResult) {
        new CheckIsFavoriteTask(iIsFavoriteResult).execute(id);
    }

    public void addFavorite(final Place place) {
        if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
            GoogleAPIManager.getPlacePhoto(place.getPhotos().get(0).getReference(),
                    place.getPhotos().get(0).getMaxWidth(),
                    new GoogleAPIManager.IGetPhotoResponse() {
                        @Override
                        public void onResponse(Bitmap photo) {
                            new AddPlaceWithPhoto(place, photo).start();
                        }

                        @Override
                        public void onFailuer(VolleyError error) {

                        }
                    });
        } else {
            new AddPlaceNoPhoto(place).start();
        }
    }

    public void removeFavorite(String id) {
        new RemoveFromDBTask().execute(id);
    }

    public void getAllFavorites(IGetAllFavorites iGetAllFavorites) {
        new GetAllFavoritesTask(iGetAllFavorites).execute();
    }

    private void printStringArray(String[] arr) {
        if (arr != null)
            for (int i = 0; i < arr.length; i++)
                Log.d("doInBackground", "Input: " + arr[i]);
    }

    public interface IIsFavoriteResult {
        void result(boolean result);
    }

    public interface IGetAllFavorites {
        void onResult(PlaceFavorite[] favorites);
    }

    private class AddPlaceNoPhoto extends Thread {
        private Place place;
        private String photoPath;

        public AddPlaceNoPhoto(Place place) {
            this.place = place;
            this.photoPath = "";
        }


        @Override
        public void run() {
            if (!photoPath.equals(""))
                database.PlaceDao().addPlacesToFavorite(new PlaceFavorite(place, photoPath));
            else
                database.PlaceDao().addPlacesToFavorite(new PlaceFavorite(place));
        }
    }

    private class AddPlaceWithPhoto extends Thread {
        private Bitmap photo;
        private Place place;

        public AddPlaceWithPhoto(Place place, Bitmap photo) {
            this.photo = photo;
            this.place = place;
        }

        @Override
        public void run() {
            String photoPath = new ImageSaver(context)
                    .setPlaceID(place.getId())
                    .setFileName("1")
                    .save(photo);
            database.PlaceDao().addPlacesToFavorite(new PlaceFavorite(place, photoPath));
        }
    }

    private class RemoveFromDBTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... ids) {
            String id = ids[0];
            AppDatabase database = AppDatabase.getAppDatabase(context);
            database.PlaceDao().deletePlacesInFavorites(database.PlaceDao().loadPlaceFromID(id));
            return null;
        }
    }

    private class CheckIsFavoriteTask extends AsyncTask<String, Void, Boolean> {
        private IIsFavoriteResult callback;

        public CheckIsFavoriteTask(IIsFavoriteResult callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... ids) {
            printStringArray(ids);
            PlaceFavorite[] arr = database.PlaceDao().loadAllUsers();
            for (int i = 0; i < arr.length; i++)
                Log.d("doInBackground", "dataBase: " + arr[i].getId());
            String id = ids[0];
            return database.PlaceDao().loadPlaceFromID(id) != null && database.PlaceDao().loadPlaceFromID(id).length != 0;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            callback.result(aBoolean);
        }
    }

    private class GetAllFavoritesTask extends AsyncTask<Void, Void, PlaceFavorite[]> {
        private IGetAllFavorites callback;

        public GetAllFavoritesTask(IGetAllFavorites callback) {
            this.callback = callback;
        }

        @Override
        protected PlaceFavorite[] doInBackground(Void... voids) {
            return database.PlaceDao().loadAllUsers();
        }

        @Override
        protected void onPostExecute(PlaceFavorite[] favorites) {
            super.onPostExecute(favorites);
            callback.onResult(favorites);
        }
    }

}
