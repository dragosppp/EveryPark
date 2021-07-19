package com.example.park.util;

import android.util.Log;

import com.example.park.models.ParkingSpot;
import com.example.park.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.park.util.Check.LatLngToGeoPoint;
import static com.example.park.util.Check.getLocalTime;
import static com.example.park.util.Constants.DUMMY_DATA_TAG;


public class DummyDataGenerator {

   public static void dummySpotsToFirebase(int numberOfElements){
      FirebaseFirestore myDb =  FirebaseFirestore.getInstance();

      for(int i = 0; i < numberOfElements; i++ ){
         ParkingSpot p = randomParkingSpot();
         String id = myDb.collection("Parking Spots").document().getId();
         myDb.collection("Parking Spots").document(id).set(p);
         Log.d(DUMMY_DATA_TAG, "#" + i + ": "+ p);
      }
   }

   private static ParkingSpot randomParkingSpot(){
       Date minDate = getLocalTime();
       Date maxDate = addDaysToDate(minDate, 20);
      ParkingSpot parkingSpot = new ParkingSpot();

      parkingSpot.setAvailable(true);
      parkingSpot.setAvailableUntil(randomDate(minDate,maxDate));
      //location of Bucharest
      parkingSpot.setGeoPoint(randomGeoPoint(44.43225, 26.10626,4000));
      parkingSpot.setUser(new User());

      return parkingSpot;
   }

   private static GeoPoint randomGeoPoint(double latitude, double longitude, int radius) {
      Random random = new Random();

      // Convert radius from meters to degrees
      double radiusInDegrees = radius / 111000f;

      double u = random.nextDouble();
      double v = random.nextDouble();
      double w = radiusInDegrees * Math.sqrt(u);
      double t = 2 * Math.PI * v;
      double x = w * Math.cos(t);
      double y = w * Math.sin(t);

      // Adjust the x-coordinate for the shrinking of the east-west distances
      double new_x = x / Math.cos(Math.toRadians(longitude));

      double foundLatitude = y + latitude;
      double foundLongitude = new_x + longitude;


      return LatLngToGeoPoint(new LatLng(foundLatitude,foundLongitude));
   }

   private static Date randomDate(Date startDate, Date endDate) {
      long startMillis = startDate.getTime();
      long endMillis = endDate.getTime();
      long randomMillisSinceEpoch = ThreadLocalRandom
              .current()
              .nextLong(startMillis, endMillis);
      return new Date(randomMillisSinceEpoch);
   }

   private static Date addDaysToDate(Date date, int days) {

      Calendar cal = Calendar.getInstance();
      cal.setTime(date);

      cal.add(Calendar.DATE, days);

      return cal.getTime();
   }
}
