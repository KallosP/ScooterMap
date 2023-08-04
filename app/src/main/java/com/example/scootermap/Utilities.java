package com.example.scootermap;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Utilities {

    // Utility method to show a Toast message
    static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    static HashMap<String, Marker> markersOnMap = new HashMap<>();
    static Queue<String> trackActiveIDs = new LinkedList<>();
    static int i = 0;
    static void createAllMarkers(GoogleMap mMap, HashMap<String, MarkerOptions> scooters, ArrayList<String> markerIDs){

        // TODO: - MAKE THIS CODE CLEANER/MORE UNDERSTANDABLE!!!
        //      - implement the removeOldMarkers() method (try to modify so that markersOnMap is a FIFO or LIFO data structure instead

        for(Map.Entry<String, MarkerOptions> markerEntry : scooters.entrySet()) {

            boolean markerExists = false;
            for(Map.Entry<String, Marker> markerOnMapEntry : markersOnMap.entrySet()){
                if(markerEntry.getKey().equals(markerOnMapEntry.getKey())){
                    markerExists = true;
                    break;
                }
            }

            if(!markerExists){
               // Log.d("TEST", "Marker: " + ++i);
                Marker newMarker = mMap.addMarker(markerEntry.getValue());
                markersOnMap.put(markerEntry.getKey(), newMarker);
                trackActiveIDs.add(markerEntry.getKey());
            }


        }

//        if(markersOnMap.size() > 250){
//            Log.d("TEST", "RAN");
//            removeOldMarkers();
//        }

    }

    static void removeOldMarkers() {

        for(int i = 0; i < 50; ++i) {
            String oldestID = trackActiveIDs.peek();
            markersOnMap.get(oldestID).remove();
        }

    }

}
//            boolean markerExists = false;
//            if(!markersOnMap.isEmpty()){
//                for (Marker marker : markersOnMap) {
//                    if (marker.getPosition().equals(coords)) {
//                        //Log.d("TEST", "wut: " + ++i);
//                        markerExists = true;
//                        break;
//                    }
//                }
//            }
//
//            if (!markerExists) {
//                //Log.d("TEST", "Marker: " + ++i);
//                Marker newMarker = mMap.addMarker(new MarkerOptions()
//                        .position(coords)
//                        .title("Bird"));
//                markersOnMap.add(newMarker);
//            }