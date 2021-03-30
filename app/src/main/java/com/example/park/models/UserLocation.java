package com.example.park.models;

import com.google.android.gms.maps.model.LatLng;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


public class UserLocation {

    private LatLng latLng;
    private GeoPoint geoPoint;
    private @ServerTimestamp Date timestamp;
    private User user;

    public UserLocation(LatLng latLng, GeoPoint geoPoint, Date timestamp, User user) {
        this.latLng = latLng;
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "latLng=" + latLng +
                ", geoPoint=" + geoPoint +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                '}';
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
