package com.example.park.util;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Check {

   public static boolean areStringsEqual(String s1, String s2){
      return s1.equals(s2);
   }

   public static boolean isStringEmpty(String string) {
      return string.equals("");

   }

   public static GeoPoint LatLngToGeoPoint(LatLng latLng){
      return new GeoPoint(latLng.latitude, latLng.longitude);
   }

}
