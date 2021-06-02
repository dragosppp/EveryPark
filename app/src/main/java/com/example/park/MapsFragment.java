package com.example.park;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.park.models.MapMarker;
import com.example.park.models.MapMarkerRenderer;
import com.example.park.models.ParkingSpot;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.MarkerManager;

import java.util.ArrayList;

import static com.example.park.util.Constants.MAIN_TAG;
import static com.example.park.util.Constants.MAP_FRAGMENT_TAG;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ClusterManager.OnClusterItemClickListener{

   private ArrayList<ParkingSpot> parkingSpotList = new ArrayList<>();
   private FirebaseFirestore myDb;
   private ClusterManager<MapMarker> clusterManager;

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

   @SuppressLint("PotentialBehaviorOverride")
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
      LatLng location = new LatLng(44.444,26.103);
      setMapFocus(map,location );

      setUpClusterer(map);
   }

   @Override
   public boolean onClusterItemClick(ClusterItem item) {
      Log.d(MAP_FRAGMENT_TAG,"Cluster item clicked");
      showBottomSheetDialog();
      return false;
   }

   @Override
   public boolean onMarkerClick(Marker marker) {
      Log.d(MAP_FRAGMENT_TAG,"Marker clicked");
      showBottomSheetDialog();
      return false;
   }

   private void showBottomSheetDialog() {
      final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
      bottomSheetDialog.setContentView(R.layout.view_marker_details);

      TextView address = bottomSheetDialog.findViewById(R.id.tv_view_marker_address);
      AppCompatButton btn = bottomSheetDialog.findViewById(R.id.btn_view_marker);
      bottomSheetDialog.show();
   }

   private void setMapFocus(GoogleMap map, LatLng latLng){
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
      CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
      map.animateCamera(zoom);
   }

   private void setUpClusterer(GoogleMap map) {
      clusterManager = new ClusterManager<>(getContext(), map);
      clusterManager.setRenderer(new MapMarkerRenderer(getContext(),map, clusterManager));
      map.setOnCameraIdleListener(clusterManager);
      map.setOnMarkerClickListener(clusterManager);
      clusterManager.setOnClusterItemClickListener(this);
      addMarkersToCluster();
   }

   private void addMarkersToCluster(){
      for(ParkingSpot spot: parkingSpotList){
         MapMarker mapMarker = new MapMarker(spot);
         clusterManager.addItem(mapMarker);
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
}