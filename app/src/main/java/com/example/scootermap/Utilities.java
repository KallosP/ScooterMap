package com.example.scootermap;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Utilities {

    // Utility method to show a Toast message
    static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    static void convertCoordsToMarker(GoogleMap mMap, ArrayList<LatLng> markerList){
        for(LatLng coords : markerList) {
            mMap.addMarker(new MarkerOptions()
                    .position(coords)
                    .title("Bird"));
        }
    }

}
