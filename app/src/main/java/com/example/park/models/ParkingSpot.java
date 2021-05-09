package com.example.park.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class ParkingSpot {

   private GeoPoint geoPoint;
   private boolean available;
   private Date availableUntil;
   private User user;

   public ParkingSpot(GeoPoint geoPoint, boolean available, Date availableUntil, User user) {
      this.geoPoint = geoPoint;
      this.available = available;
      this.availableUntil = availableUntil;
      this.user = user;
   }

   public ParkingSpot() {

   }

   @Override
   public String toString() {
      return "ParkingSpot{" +
              "geoPoint= {" + geoPoint.getLatitude() + ", " + geoPoint.getLongitude() + " }"+
              ", available=" + available +
              ", availableUntil=" + availableUntil +
              ", userEmail=" + user.getEmail() +
              '}';
   }

   public GeoPoint getGeoPoint() {
      return geoPoint;
   }

   public void setGeoPoint(GeoPoint geoPoint) {
      this.geoPoint = geoPoint;
   }

   public boolean isAvailable() {
      return available;
   }

   public void setAvailable(boolean available) {
      this.available = available;
   }

   public Date getAvailableUntil() {
      return availableUntil;
   }

   public void setAvailableUntil(Date availableUntil) {
      this.availableUntil = availableUntil;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}
