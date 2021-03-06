package com.example.park.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


public class UserLocation implements Parcelable {

    private GeoPoint geoPoint;
    private @ServerTimestamp Date timestamp;
    private User user;

    public UserLocation( GeoPoint geoPoint, Date timestamp, User user) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.user = user;
    }

   public UserLocation() {
   }

   @Override
    public String toString() {
        return "UserLocation{" +
                " geoPoint=" + geoPoint +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                '}';
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

    public LatLng getLatLng(){
       return  new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeDouble(geoPoint.getLatitude());
      dest.writeDouble(geoPoint.getLongitude());
      dest.writeParcelable(user, flags);
   }

   protected UserLocation(Parcel in) {
       Double lat = in.readDouble();
       Double lng = in.readDouble();
       geoPoint = new GeoPoint(lat,lng);
       user = in.readParcelable(User.class.getClassLoader());
   }

   public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
      @Override
      public UserLocation createFromParcel(Parcel in) {
         return new UserLocation(in);
      }

      @Override
      public UserLocation[] newArray(int size) {
         return new UserLocation[size];
      }
   };

}
