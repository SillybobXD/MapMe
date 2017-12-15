package com.mapme.mapme.mapme.util.data.obj;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by Sillybob on 12/14/2017.
 */

public class Place {
    //formatted_address
    private String address;
    //formatted_phone_number
    private String phoneNumber;
    //geometry/location
    private Location location;
    //icon
    private String iconURL;
    //name
    private String placeName;
    //permanently_closed
    private boolean isPremClosed;
    //photos[]
    private ArrayList<Photo> photos;
    //rating
    private double rating;
    //website
    private String website;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public boolean isPremClosed() {
        return isPremClosed;
    }

    public void setPremClosed(boolean premClosed) {
        isPremClosed = premClosed;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        if (photos == null)
            photos = new ArrayList<>();
        photos.add(photo);
    }

    /*public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }*/

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

    @Override
    public String toString() {
        return "Place{" +
                "address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", location=" + location +
                ", iconURL='" + iconURL + '\'' +
                ", placeName='" + placeName + '\'' +
                ", isPremClosed=" + isPremClosed +
                ", photos=" + photos +
                ", rating=" + rating +
                ", website='" + website + '\'' +
                '}';
    }
}
