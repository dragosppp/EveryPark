package com.example.park;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.park.models.ParkingSpot;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.example.park.util.Check.GeoPointToLatLng;
import static com.example.park.util.Constants.MAIN_TAG;
import static com.example.park.util.Constants.MAP_FRAGMENT_TAG;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

   private ArrayList<ParkingSpot> parkingSpotList = new ArrayList<>();
   private FirebaseFirestore myDb;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(MAP_FRAGMENT_TAG,"onCreate: Called");
      myDb = FirebaseFirestore.getInstance();
      getParkingSpots();
   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_maps, container, false);
      Log.d(MAP_FRAGMENT_TAG,"onCreateView: Called");
      return view;
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      Log.d(MAP_FRAGMENT_TAG,"onViewCreated: Called");
      SupportMapFragment mapFragment =
              (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
      if (mapFragment != null) {
         mapFragment.getMapAsync(this);
      }
   }

   @Override
   public void onMapReady(GoogleMap map) {
      Log.d(MAP_FRAGMENT_TAG,"onMapReady: Called");
      if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
              != PackageManager.PERMISSION_GRANTED
              && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
         return;
      }
      map.setMyLocationEnabled(true);

     displayParkingMarkers(map);
   }

   private void displayParkingMarkers(GoogleMap map){
      for(ParkingSpot spot: parkingSpotList){
         SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, HH:mm");
         String availableUntil = dateFormat.format(spot.getAvailableUntil());
         String snippetMesagge = getDateDiff(getLocalTime(), spot.getAvailableUntil(), TimeUnit.HOURS)
                 + " more hours";

         MarkerOptions markerOptions = new MarkerOptions()
                 .title(availableUntil)
                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_triangle_64))
                 .snippet(snippetMesagge)
                 .position(GeoPointToLatLng(spot.getGeoPoint()));
         Log.d(MAP_FRAGMENT_TAG,markerOptions.toString());
         map.addMarker(markerOptions);
      }
   }

   private void getParkingSpots(){
      Log.d(MAP_FRAGMENT_TAG, "----- getParkingSpots -----");
      parkingSpotList.clear();
      CollectionReference parkingRef = myDb.collection(getString(R.string.collection_parking_spots));
      parkingRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
         @Override
         public void onEvent(@Nullable QuerySnapshot snapshotValue, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
               Log.e(MAIN_TAG, "Listener failed to retrieve parking spots: ", error);
            }else if (snapshotValue != null){
               for(QueryDocumentSnapshot doc : snapshotValue){
                  ParkingSpot parkingSpot = doc.toObject(ParkingSpot.class);
                  if( parkingSpot.isAvailable()){
                     parkingSpotList.add(parkingSpot);
                  }
               }
            }
         }
      });
   }

   private Date getLocalTime(){
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