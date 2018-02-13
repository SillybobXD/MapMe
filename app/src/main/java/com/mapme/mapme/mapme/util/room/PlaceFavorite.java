package com.mapme.mapme.mapme.util.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import com.mapme.mapme.mapme.util.data.obj.Place;

/**
 * Created by Sillybob on 2/5/2018.
 */

@Entity(tableName = "Places")
public class PlaceFavorite {
    @PrimaryKey
    @NonNull
    private String id;
    private String address;
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    private double lat;
    private double lng;
    private String name;
    @ColumnInfo(name = "closed")
    private boolean isPremClosed;
    private String photoPath;
    private double rating;
    private String website;

    public PlaceFavorite() {
    }

    public PlaceFavorite(Place place) {
        this();
        id = place.getId();
        address = place.getAddress();
        phoneNumber = place.getPhoneNumber();
        lat = place.getLocation().getLatitude();
        lng = place.getLocation().getLongitude();
        name = place.getPlaceName();
        isPremClosed = place.isPremClosed();
        rating = place.getRating();
        website = place.getWebsite();

        photoPath = "";
    }

    public PlaceFavorite(Place place, String photo) {
        this(place);
        photoPath = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPremClosed() {
        return isPremClosed;
    }

    public void setPremClosed(boolean premClosed) {
        isPremClosed = premClosed;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Dao
    public interface MyDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void addPlacesToFavorite(PlaceFavorite... places);

        @Update
        void updatePlacesInFavorites(PlaceFavorite... places);

        @Delete
        void deletePlacesInFavorites(PlaceFavorite... places);

        @Query("SELECT * FROM Places")
        PlaceFavorite[] loadAllUsers();

        @Query("SELECT * FROM Places WHERE id = :id")
        PlaceFavorite[] loadPlaceFromID(String id);

    }
}
