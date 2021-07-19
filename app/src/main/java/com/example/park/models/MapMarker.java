package com.example.park.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.concurrent.TimeUnit;

import static com.example.park.util.Check.GeoPointToLatLng;
import static com.example.park.util.Check.getDateDiff;
import static com.example.park.util.Check.getLocalTime;
import static com.example.park.util.Constants.DATE_FORMAT;

public class MapMarker implements ClusterItem {

   private  LatLng position;
   private  String title;
   private  String snippet;

   public MapMarker(ParkingSpot spot) {
      String snippetMesagge = getDateDiff(getLocalTime(), spot.getAvailableUntil(), TimeUnit.HOURS)
              + " more hours";
      this.position = GeoPointToLatLng(spot.getGeoPoint());
      this.title = DATE_FORMAT.format(spot.getAvailableUntil());
      this.snippet = snippetMesagge;
   }

   @NonNull
   @Override
   public LatLng getPosition() {
      return position;
   }

   @Nullable
   @Override
   public String getTitle() {
      return  title;
   }

   @Nullable
   @Override
   public String getSnippet() {
      return snippet;
   }
}
