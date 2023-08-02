package com.example.scootermap;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, LocationListener {

    // Program entry point
    private GoogleMap mMap;
    private SearchView SearchScooter;

    // Important flag for fixing infinite loop with alert dialogs
    private boolean locationPermissionRequested = false;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SearchView searchView = findViewById(R.id.searchViews);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        APIHandler.setupAPIs();

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
                        Utilities.showToast(context, "Error: Unable to fetch location data. Please try again.");
                        return false;
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0); //gets the value
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude()); //gets the lat and long of the place
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location)); //adds marker to the position
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17)); //zooms into the location
                    } else {
                        Utilities.showToast(context, "Error: Please Enter A Valid Location.");
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

        // Sets location to the users location whenever button is clicked
        enableMyLocation();

        APIHandler.displayScooterMarkers(mMap);

    }


    // TODO: Move to separate class
    // LOCATION PERMISSIONS -------------------------------

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     * #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    void enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Enable location layer (display user location)
            if(mMap != null){
                mMap.setMyLocationEnabled(true);
            }
            startLocationProcedures();
            return;
        }

        // Check if the permission has been requested before
        if (!locationPermissionRequested) {
            // 2. Otherwise, request location permissions from the user.
            PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE);
            // Set the flag to true to indicate that the permission has been requested
            locationPermissionRequested = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    // LOCATION UPDATES ----------------------------------
    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation;

    @SuppressLint("MissingPermission")
    void startLocationProcedures() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mCurrentLocation = location;
                            // Logic to handle location object
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 17));
                        }
                    }
                });

        // Change location settings
        // TODO: Not needed for now. Should figure before publishing app just in case it's required. Research further
        //createLocationRequest();

        getCurrentLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        enableMyLocation();
    }

    // Request Location Updates
    LocationManager locationManager;
    @SuppressLint("MissingPermission")
    void getCurrentLocation(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 0, this);

    }

    // Store Location Updates
    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(@NonNull Location location) {

        mMap.setMyLocationEnabled(true);
        // Set the current lat and long
        mCurrentLocation = location;

//        Log.d("TEST", "Lat: " + mCurrentLocation.getLatitude());
//        Log.d("TEST", "Long: " + mCurrentLocation.getLongitude());
    }

    // Change location settings
//    LocationRequest locationRequest;
//    protected void createLocationRequest() {
//
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//
//       // LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//        requestingLocationUpdates = true;
//        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                // All location settings are satisfied. The client can initialize
//                // location requests here.
//                // ...
//            }
//        });
//
//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(),
//                        // and check the result in onActivityResult().
//                        ResolvableApiException resolvable = (ResolvableApiException) e;
//                        resolvable.startResolutionForResult(MainActivity.this,
//                                0x1);
//                    } catch (IntentSender.SendIntentException sendEx) {
//                        // Ignore the error.
//                    }
//                }
//            }
//        });
//    }


}