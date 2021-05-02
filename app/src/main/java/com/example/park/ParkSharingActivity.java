package com.example.park;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import static com.example.park.util.Constants.PARK_SHARE_TAG;

public class ParkSharingActivity extends Activity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {

   LatLng position = new LatLng(34.6767, 33.04455);
   final Marker marker_final = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_park_sharing);

      MapFragment mapFragment = (MapFragment) getFragmentManager()
              .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);

      ImageButton returnback = (ImageButton) findViewById(R.id.btn_park_share_show_location);
      returnback.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Toast.makeText(ParkSharingActivity.this.getApplicationContext(),
                    position.latitude + ":" + position.longitude, Toast.LENGTH_LONG).show();
         }
      });
   }

   @Override
   public void onMapReady(GoogleMap map) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
         map.setMyLocationEnabled(true);
      }
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
      CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
      map.animateCamera(zoom);

      map.addMarker(new MarkerOptions()
              .title("Shop")
              .snippet("Is this the right location?")
              .position(position))
              .setDraggable(true);

      // map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
      map.setOnInfoWindowClickListener(this);
      map.setOnMarkerDragListener(this);
   }

   @Override
   public void onInfoWindowClick(Marker marker) {
      Log.d(PARK_SHARE_TAG, "onInfoWindowClick");
      Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
   }

   @Override
   public void onMarkerDragStart(Marker marker) {
      LatLng position = marker.getPosition();
   }

   @Override
   public void onMarkerDrag(Marker marker) {
      LatLng position = marker.getPosition();
      Log.d(PARK_SHARE_TAG, "onMarkerDrag");
      Log.d(PARK_SHARE_TAG, String.format("Dragging to %f:%f",
              position.latitude,
              position.longitude));
   }

   @Override
   public void onMarkerDragEnd(Marker marker) {
      position = marker.getPosition();
      Log.d(PARK_SHARE_TAG, "onMarkerDragEnd");
      Log.d(getClass().getSimpleName(), String.format("Dragged to %f:%f",
              position.latitude,
              position.longitude));
   }
}