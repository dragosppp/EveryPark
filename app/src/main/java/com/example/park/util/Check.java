package com.example.park.util;



import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

   public static Date getLocalTime(){
      Date in = new Date();
      LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
      Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
      return out;
   }

   public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
      long diffInMillies = date2.getTime() - date1.getTime();
      return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
   }
}
