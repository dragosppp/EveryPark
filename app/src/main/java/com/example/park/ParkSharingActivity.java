package com.example.park;

import com.example.park.models.ParkingSpot;
import com.example.park.models.User;
import com.example.park.models.UserClient;
import com.example.park.models.UserLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.example.park.util.Constants.EXTRA_DATE_PICKER;
import static com.example.park.util.Constants.EXTRA_USER_LOCATION;
import static com.example.park.util.Constants.PARK_SHARE_TAG;
import static com.example.park.util.Constants.AUTOCOMPLETE_REQUEST;
import static com.example.park.util.Check.*;
import static com.example.park.util.Constants.PICKER_DATE_REQUEST;

public class ParkSharingActivity extends Activity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {

   private UserLocation userLocation;
   private ParkingSpot parkingSpot;
   private LatLng currentPosition;
   private EditText editText;
   private MarkerOptions markerOptions;
   private Marker marker;
   private GoogleMap googleMap;
   private FirebaseFirestore myDb;
   private AppCompatButton btnSave;
   private Date availabilityDate;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_park_sharing);

      editText = findViewById(R.id.et_park_share_locationsearch);
      btnSave = findViewById(R.id.btn_park_share_save);

      myDb = FirebaseFirestore.getInstance();

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


      userLocation = getIntent().getExtras().getParcelable(EXTRA_USER_LOCATION);
      if (userLocation == null) {
         Log.e(PARK_SHARE_TAG, "UserLocation has null value.");
      }else{
         currentPosition = userLocation.getLatLng();
         Log.d(PARK_SHARE_TAG, "Position: " + currentPosition);
      }

      MapFragment mapFragment = (MapFragment) getFragmentManager()
              .findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);

      setBtnSave();
   }

   private void setBtnSave(){
      btnSave.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            //todo make button change aspect on click
            Intent intent = new Intent(getApplicationContext(), DatePickerActivity.class);
            startActivityForResult(intent, PICKER_DATE_REQUEST);
         }
      });
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

      else if(requestCode == PICKER_DATE_REQUEST && resultCode == RESULT_OK){
         Log.d(PARK_SHARE_TAG, "intent data: "+data.toString());
         availabilityDate = new Date();
         availabilityDate.setTime(data.getLongExtra(EXTRA_DATE_PICKER, -1));
         Log.d(PARK_SHARE_TAG, "Parking spot date received:  " + availabilityDate.toString());
         Toast.makeText(ParkSharingActivity.this, "Location is now set", Toast.LENGTH_SHORT).show();
         setParkingSpotDetails(availabilityDate);
         returnToMainActivity();
      } else if (requestCode == PICKER_DATE_REQUEST && resultCode == ParkSharingActivity.RESULT_CANCELED) {
         Log.e(PARK_SHARE_TAG, "Picker date answer canceled. ");
      }
   }

   @RequiresApi(api = Build.VERSION_CODES.O)
   private void setParkingSpotDetails(Date date){
      if(parkingSpot == null){
         User user = ((UserClient)getApplicationContext()).getUser();
         parkingSpot = new ParkingSpot(LatLngToGeoPoint(currentPosition), true, date, user);
         //todo: make a ParkingSpotClient singleton instance
      }else{
         parkingSpot.setGeoPoint(LatLngToGeoPoint(currentPosition));
         parkingSpot.setAvailable(true);
         parkingSpot.setAvailableUntil(date);
      }
      saveParkingSpot();
   }

   private void saveParkingSpot(){
      if(parkingSpot!=null){
         DocumentReference parkingRef = myDb
                 .collection(getString(R.string.collection_parking_spots))
                 .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
         parkingRef.set(parkingSpot).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                  Log.d(PARK_SHARE_TAG,"saveParkingSpot in DB: " + "\n\t " + parkingSpot);
               }
            }
         });
      }
   }

   private void returnToMainActivity(){
      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      startActivity(intent);
      finish();
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