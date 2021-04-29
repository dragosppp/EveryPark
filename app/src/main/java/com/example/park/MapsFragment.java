package com.example.park;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.park.models.UserLocation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import static com.example.park.util.Constants.MAP_FRAGMENT_TAG;
import static com.example.park.util.Constants.USER_LOCATION_KEY;


public class MapsFragment extends Fragment implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

   private UserLocation userLocation;
   private GoogleMap googleMap;
   private LatLngBounds mapBoundaries;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.d(MAP_FRAGMENT_TAG,"onCreate: Called");
      getChildFragmentManager().
              setFragmentResultListener("requestKey", this, new FragmentResultListener() {
         @Override
         public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
            userLocation = bundle.getParcelable(USER_LOCATION_KEY);
            Log.d(MAP_FRAGMENT_TAG,"userlocation_1: " +userLocation);
         }
      });
   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_maps, container, false);
      Log.d(MAP_FRAGMENT_TAG,"onCreateView: Called");
//      if (savedInstanceState != null) {
//         userLocation = getArguments().getParcelable(USER_LOCATION_KEY);
//         Log.d(MAP_FRAGMENT_TAG,"userlocation_2: " +userLocation);
//      } else {
//         Log.d(MAP_FRAGMENT_TAG,"Null Bundle - SavedInstanceState!");
//      }
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
      googleMap = map;
   }

   @Override
   public void onCameraIdle() {

   }

   @Override
   public void onCameraMoveCanceled() {

   }

   @Override
   public void onCameraMove() {

   }

   @Override
   public void onCameraMoveStarted(int i) {

   }
}