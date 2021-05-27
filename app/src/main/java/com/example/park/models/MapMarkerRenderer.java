package com.example.park.models;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.park.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class MapMarkerRenderer extends DefaultClusterRenderer<MapMarker> {

   public MapMarkerRenderer(Context context, GoogleMap map, ClusterManager<MapMarker> clusterManager) {
      super(context, map, clusterManager);
   }

   @Override
   protected void onBeforeClusterItemRendered(@NonNull MapMarker item, @NonNull MarkerOptions markerOptions) {
      super.onBeforeClusterItemRendered(item, markerOptions);
      markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_triangle_32));
      markerOptions.snippet(item.getSnippet());
      markerOptions.title(item.getTitle());
   }
}