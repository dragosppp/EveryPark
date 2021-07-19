package com.example.park.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class ParkingSpot implements Parcelable {

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

   protected ParkingSpot(Parcel in) {
      available = in.readByte() != 0;
      user = in.readParcelable(User.class.getClassLoader());
   }

   public static final Creator<ParkingSpot> CREATOR = new Creator<ParkingSpot>() {
      @Override
      public ParkingSpot createFromParcel(Parcel in) {
         return new ParkingSpot(in);
      }

      @Override
      public ParkingSpot[] newArray(int size) {
         return new ParkingSpot[size];
      }
   };

   @Override
   public String toString() {
      return "ParkingSpot{" +
              "geoPoint= {" + geoPoint.getLatitude() + ", " + geoPoint.getLongitude() + " }"+
              ", available=" + available +
              ", availableUntil=" + availableUntil +
              //", userEmail=" + user.getEmail() +
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

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte((byte) (available ? 1 : 0));
      dest.writeParcelable(user, flags);
   }
}
