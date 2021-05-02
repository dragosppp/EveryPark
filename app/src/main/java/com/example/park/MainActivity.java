package com.example.park;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.park.models.User;
import com.example.park.models.UserClient;
import com.example.park.models.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import static com.example.park.util.Constants.ERROR_DIALOG_REQUEST;
import static com.example.park.util.Constants.MAIN_TAG;
import static com.example.park.util.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.park.util.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {
   
   private boolean fineLocationPermission = false;
   private FusedLocationProviderClient fusedLocationClient;
   private UserLocation userLocation;
   private FirebaseFirestore myDb;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
      myDb = FirebaseFirestore.getInstance();
   }

   @Override
   protected void onResume() {
      super.onResume();
      if (checkMapServices()) {
         if (fineLocationPermission) {
            getUserDetails();
         } else {
            getLocationPermission();
         }
      }
   }

   private boolean checkMapServices() {
      if (arePlayServicesAvailable()) {
         if (isMapsEnabled()) {
            return true;
         }
      }
      return false;
   }

   public boolean arePlayServicesAvailable() {
      Log.d(MAIN_TAG, "isServicesOK: checking google services version");

      int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

      if (available == ConnectionResult.SUCCESS) {
         Log.d(MAIN_TAG, "isServicesOK: Google Play Services is working");
         return true;
      } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
         Log.d(MAIN_TAG, "isServicesOK: an error occured but we can fix it");
         Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
         dialog.show();
      } else {
         Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
      }
      return false;
   }

   public boolean isMapsEnabled() {
      final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

      if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         buildAlertMessageNoGps();
         return false;
      }
      return true;
   }

   private void buildAlertMessageNoGps() {
      final AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
              .setCancelable(false)
              .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                 public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                 }
              });
      final AlertDialog alert = builder.create();
      alert.show();
   }

   private void getUserDetails() {
      if(userLocation == null) {
         userLocation = new UserLocation();
         DocumentReference userRef = myDb.collection(getString(R.string.collection_users))
                 .document(FirebaseAuth.getInstance().getUid());
         userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
                  Log.d(MAIN_TAG, "Succesefully got the user details.");
                  User user = task.getResult().toObject(User.class);
                  userLocation.setUser(user);
                  ((UserClient)getApplicationContext()).setUser(user);
                  getLastKnownLocation();
               }
            }
         });
      } else {
         getLastKnownLocation();
      }
   }

   private void getLastKnownLocation() {
      Log.d(MAIN_TAG, "getLatsKnownLocation: OK");
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
         return;
      }
      fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
         @Override
         public void onComplete(@NonNull Task<Location> task) {
            if (task.isSuccessful()) {
               Location location = task.getResult();
               GeoPoint geoPoint= new GeoPoint(location.getLatitude(), location.getLongitude());

               userLocation.setGeoPoint(geoPoint);
               userLocation.setTimestamp(null);
               Log.d(MAIN_TAG, "UserLocation: " + userLocation);
               saveUserLocation();
            }
         }
      });
   }

   private void saveUserLocation() {
      if(userLocation != null){
         DocumentReference locationRef = myDb
                 .collection(getString(R.string.collection_user_location))
                 .document(FirebaseAuth.getInstance().getUid());
         locationRef.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                  Log.d(MAIN_TAG,"saveUserLocation in DB: "
                          + "\n\t latitude: " + userLocation.getGeoPoint().getLatitude()
                          + "\n\t longitude: " + userLocation.getGeoPoint().getLongitude());
               }
            }
         });
      }
   }

   private void getLocationPermission() {
      if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
              android.Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED) {
         fineLocationPermission = true;
         getUserDetails();
      } else {
         ActivityCompat.requestPermissions(this,
                 new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                 PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String permissions[],
                                          @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      fineLocationPermission = false;
      switch (requestCode) {
         case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               fineLocationPermission = true;
            }
         }
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Log.d(MAIN_TAG, "onActivityResult: called.");
      switch (requestCode) {
         case PERMISSIONS_REQUEST_ENABLE_GPS: {
            if (!fineLocationPermission) {
               getLocationPermission();
            } else {
               getUserDetails();
            }
         }
      }
   }

   private void signOut() {
      FirebaseAuth.getInstance().signOut();
      Intent intent = new Intent(this, LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }

   private void parkShare() {
      Intent intent = new Intent(this, ParkSharingActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.main_menu_sign_out: {
            signOut();
            return true;
         }
         case R.id.main_menu_add_parking_spot: {
            parkShare();
            return true;
         }
         default: {
            return super.onOptionsItemSelected(item);
         }
      }
   }
}