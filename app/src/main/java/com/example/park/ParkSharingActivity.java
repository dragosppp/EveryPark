package com.example.park;

import com.example.park.models.UserLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.example.park.MainActivity.EXTRA_USERLOCATION;
import static com.example.park.util.Constants.PARK_SHARE_TAG;
import static com.example.park.util.Constants.AUTOCOMPLETE_REQUEST;

public class ParkSharingActivity extends Activity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {

   UserLocation userLocation;
   LatLng currentPosition;
   EditText editText;
   MarkerOptions markerOptions;
   Marker marker;
   GoogleMap googleMap;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_park_sharing);

      editText = findViewById(R.id.et_park_share_locationsearch);

      Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
      editText.setFocusable(false);
      editText.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(getApplicationContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST);
         }
      });


      userLocation = getIntent().getExtras().getParcelable(EXTRA_USERLOCATION);
      if (userLocation == null) {
         Log.e(PARK_SHARE_TAG, "UserLocation has null value.");
      }else{
         currentPosition = userLocation.getLatLng();
         Log.d(PARK_SHARE_TAG, "Position: " + currentPosition);
      }

      MapFragment mapFragment = (MapFragment) getFragmentManager()
              .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if(requestCode == AUTOCOMPLETE_REQUEST && resultCode == RESULT_OK){
         Place place = Autocomplete.getPlaceFromIntent(data);
         marker.setPosition(Objects.requireNonNull(place.getLatLng()));
         setMapFocus(googleMap, place.getLatLng());
         editText.setText(place.getAddress());
      } else if( resultCode == AutocompleteActivity.RESULT_ERROR){
         Status status = Autocomplete.getStatusFromIntent(data);
         Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
         Log.e(PARK_SHARE_TAG, "Autocomplete activity result error: " + status.getStatusMessage());
      }
   }

   @Override
   public void onMapReady(GoogleMap map) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
         map.setMyLocationEnabled(true);
      }
      googleMap = map;

      setMapFocus(map, currentPosition);

      markerOptions = new MarkerOptions()
              .title("My parking space")
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_64))
              .position(currentPosition);
      marker = map.addMarker(markerOptions);
      marker.setDraggable(true);

      // map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
      map.setOnInfoWindowClickListener(this);
      map.setOnMarkerDragListener(this);
   }

   private void setMapFocus(GoogleMap map, LatLng latLng){
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
      CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
      map.animateCamera(zoom);
   }

   @Override
   public void onInfoWindowClick(Marker marker) {
      Log.d(PARK_SHARE_TAG, "onInfoWindowClick");
      Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
   }

   @Override
   public void onMarkerDragStart(Marker marker) { }

   @Override
   public void onMarkerDrag(Marker marker) { }

   @Override
   public void onMarkerDragEnd(Marker marker) {
      currentPosition = marker.getPosition();
      Log.d(PARK_SHARE_TAG, "onMarkerDragEnd");
   }
}