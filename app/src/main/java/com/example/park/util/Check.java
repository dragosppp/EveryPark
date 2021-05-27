package com.example.park.util;



import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Check {

   public static boolean areStringsEqual(String s1, String s2){
      return s1.equals(s2);
   }

   public static GeoPoint LatLngToGeoPoint(LatLng latLng){
      return new GeoPoint(latLng.latitude, latLng.longitude);
   }

   public static LatLng GeoPointToLatLng(GeoPoint geoPoint){
      return  new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
   }

}
