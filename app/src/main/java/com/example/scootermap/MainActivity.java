package com.example.scootermap;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Program entry point

    private GoogleMap mMap;
    private SearchView SearchScooter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SearchView searchView = findViewById(R.id.searchViews);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString(); //query's through the input and makes it a string to set to location
                List<Address> addressList = null; //sets address to null
                if (location != null) //if person input a location
                {
                    Geocoder geocoder = new Geocoder(MainActivity.this); //stores location info into geocoder
                    try {
                        addressList = geocoder.getFromLocationName(location, 1); //gets location name and puts it
                        //in address list
                    } catch (IOException e) {
                        showToast("Error: Unable to fetch location data. Please try again.");
                        return false;
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0); //gets the value
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude()); //gets the lat and long of the place
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location)); //adds marker to the position
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17)); //zooms into the location
                    } else {
                        showToast("Error: Please Enter A Valid Location.");
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    // Utility method to show a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in California and move the camera
        LatLng California = new LatLng(37, -119);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(California));
        //zoom button appears on map and allows double tap feature to zoom in as well
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //Gets rid of rotation to make it easier to use
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        //sets location to the users location whenever button is clicked

    }
}