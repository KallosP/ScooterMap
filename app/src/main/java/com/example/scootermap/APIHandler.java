package com.example.scootermap;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIHandler extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    // okHTTP header values
    final static String email = "retepsollak@gmail.com";
    private static final String BASE_URL = "https://api-auth.prod.birdapp.com/api/v1/auth/email";
    private static final MediaType JSON = MediaType.parse("application/json");
    private static final String USER_AGENT = "Bird/4.119.0(co.bird.Ride; build:3; Android 10)";
    private static final String GUID = UUID.randomUUID().toString();

    // Bird token
    static String token = "eyJhbGciOiJSUzUxMiJ9.eyJqdGkiOiIzZjFkNjgzNC0xYTU3LTQxOWUtYWM2NC1jN2QxNjY3Yzc0NzUiLCJzdWIiOiJkMjMwNDNjMC01NjYxLTRkMTctOTNkMC1iOWQ2ZWYwOTkzYzQiLCJuYmYiOjE2OTEwMjkzNDYsImV4cCI6MTY5MTExNTc0NiwiYXVkIjoiYmlyZC5zZXJ2aWNlcyIsImlzcyI6ImJpcmQuYXV0aCIsImlhdCI6MTY5MTAyOTM0Niwicm9sZXMiOlsiVVNFUiJdLCJhcHAiOiI3YjhlZDk1NS02ZTNhLTRlZWMtYmEyMC04OGFmOWQ3YWVhNzYiLCJ2ZXIiOiIwLjAuMiJ9.g0scw-wlI6TWQzd8LBqRPMas-IC7_AyR5em3Jl7eSxzE2efTbuhNMxTHJdjrW2FpQ9_oLbF5sJDZtQLXhFq0KWbzbxpXZzLLXmKU8ERhOdftC75O6Gd5lpIao7dn09hKkkn-Q6xdmihHA_VYYL5IOxSnernldBmLMSdcnlycdoSgGIoLQRsdSkXeqAX9ytuJPiEUlMjsSOECmpLHODXkwoHE3UE2EGieDbzk73vXXCaWRgltZkV-2jtOBMWX17uGZcaV4FaJ5WcT0IVEjyqVbq4CGTv38nZPodSKLGY-pq_4IEELvpp5aGP3SYw6RelaI5GCgUkVIYPQ0vWo34QcqT8lCIkk0AmIeILdqvcnmxenFVFEpRXC_Zks6W3GrFLv83TSkTEALMzmIcI6s2ZsAUQXhWBI5uCcZTXMhHUGE9gJs4zgU08eOgsoee0DBIJJmFXkvib-kEJ2pyTZPoYQrtQvNiiDndqCFLH95RXSxBPD8IHtY-gJaDglHuOE5IK4pW94KjXP181xqEpfubv_MJHxOOF83SXwFrHHP-UnGohitcv68WQqf5nfM6ic3am_41PziEqchJry3ZG4mUV5UMs3nLD2GZcwkhgFwZLflgr4lh9_hHZ4o_MII_4cwi3DHTarMy84N43foRl64eWMvsCqdaU0X3f9LoxmtdIJTbQ";

    static ArrayList<String> scooters;
    static ArrayList<LatLng> scooterCoords = new ArrayList<>();

    static boolean storedScooters = false;

    static Request getAuthToken(OkHttpClient client) {
        String requestBody = "{\"email\":\"" + email + "\"}";
        RequestBody body = RequestBody.create(JSON, requestBody);

        return new Request.Builder()
                .url(BASE_URL)
                .header("User-Agent", USER_AGENT)
                .header("Device-Id", GUID)
                .header("Platform", "android")
                .header("App-Version", "4.119.0")
                .header("Content-Type", "application/json")
                .post(body)
                .build();
    }

    static Request useAuthToken(OkHttpClient client) {
        String requestBody = "{\"token\":\"" + token + "\"}";
        RequestBody body = RequestBody.create(JSON, requestBody);
        return new Request.Builder()
                .url("https://api-auth.prod.birdapp.com/api/v1/auth/magic-link/use")
                .header("Device-Id", GUID)
                .post(body)
                .build();
    }

    static Request refreshAuthToken(OkHttpClient client) {
        // TODO: use updated refresh token from previous request

        String requestBody = "{\"token\":\"" + token + "\"}";
        RequestBody body = RequestBody.create(JSON, requestBody);

        return new Request.Builder()
                .url("https://api-auth.prod.birdapp.com/api/v1/auth/refresh/token")
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Device-Id", GUID)
                .addHeader("Platform", "android")
                .addHeader("App-Version", "4.119.0")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        // TODO: update access and refresh tokens
    }

    static Request requestScooterLocations(OkHttpClient client, Location userLocation) {
        return new Request.Builder()
                .url("https://api-bird.prod.birdapp.com/bird/nearby?latitude=37.77184&longitude=-122.40910&radius=1000")
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", USER_AGENT)
                .header("legacyrequest", "false")
                .header("Device-Id", GUID)
                .header("App-Version", "4.119.0")
                .header("Location", "{\"latitude\":" + userLocation.getLatitude() + ",\"longitude\":" + userLocation.getLongitude() + ",\"altitude\":500,\"accuracy\":65,\"speed\":-1,\"heading\":-1}")
                .get()
                .build();
    }

    static void manageClientResponse(OkHttpClient client, Request request){
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Log.d("TEST", "Failure");
                //e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws
                    IOException {
                // Log.d("TEST", "Entered");
                if(response.isSuccessful()){
                    String myResponse = response.body().string();

                    Log.d("TEST", myResponse);

                    scooters = organizeScooterData(myResponse);
                    if(scooters == null){
                        Log.d("SCOOTER ERROR", "ERROR: Scooters list is null.");
                    }
                    else {
                        //Log.d("TEST", "Latitude: " + scooters.get(0));
                    }
                }
            }
        });
    }

    // TODO:
    //        - create system for getting, refreshing, and using AUTH token
    //        - remove markers that aren't around user
    static void setupAPIs(Location userLocation) {

        // HTTP client
        OkHttpClient client = new OkHttpClient();

        Request request;

        // GET AUTH TOKEN VIA EMAIL (WORKING)
//        request = getAuthToken(client);

        // USE AUTH TOKEN (WORKING)
//        request = useAuthToken(client);

        // REFRESH AUTH TOKEN (MUST USE REFRESH TOKEN)
//        request = refreshAuthToken(client);

        // REQUEST LOCATION (MUST USE ACCESS TOKEN)
        request = requestScooterLocations(client, userLocation);
        manageClientResponse(client, request);

        // FIXME: REQUEST AREAS (NOT WORKING - 404)
//        Request request = new Request.Builder()
//                .url("https://api.birdapp.com/bird/nearby?latitude=37.77184&longitude=-122.40910&radius=1000")
//                .header("Authorization", "Bearer " + token)
//                .header("User-Agent", USER_AGENT)
//                .header("legacyrequest", "false")
//                .header("Device-Id", GUID)
//                .header("App-Version", "4.119.0")
//                .header("Location", "{\"latitude\":37.77249,\"longitude\":-122.40910,\"altitude\":500,\"accuracy\":65,\"speed\":-1,\"heading\":-1}")
//                .get()
//                .build();

        // FIXME: REQUEST CONFIG (NOT WORKING - 404)
//        Request request = new Request.Builder()
//                .url("https://api.birdapp.com/config/location?latitude=42.3140089&longitude=-71.2490943")
//                .header("App-Version", "4.119.0")
//                .get()
//                .build();



    }

    static HashMap<String, MarkerOptions> markers = new HashMap<String, MarkerOptions>();
    static ArrayList<String> markerIDs = new ArrayList<>();

    static int i = 0;
    static ArrayList<String> organizeScooterData(String scooterData){
        try {
            JSONObject jsonObject = new JSONObject(scooterData);
            JSONArray birdsArray = jsonObject.getJSONArray("birds");

            double latitude = -1, longitude = -1;
            int batteryLevel = -1;
            String scooterID = "";

            for (int i = 0; i < birdsArray.length(); i++) {
                JSONObject scooterObject = birdsArray.getJSONObject(i);
                JSONObject locationObject = scooterObject.getJSONObject("location");

                latitude = locationObject.getDouble("latitude");
                longitude = locationObject.getDouble("longitude");

                batteryLevel = scooterObject.getInt("battery_level");
                scooterID = scooterObject.getString("id");

                // Log.d("TEST", "Scooter " + i + " Latitude: " + latitude + ", Longitude: " + longitude + ", Battery: " + batteryLevel);
                LatLng tmpCoord = new LatLng(latitude, longitude);

                // Only add new coordinates
//                if(!scooterCoords.contains(tmpCoord)){
//                    //Log.d("TEST", ++i + "");
//                    scooterCoords.add(tmpCoord);
//                }
                MarkerOptions markerOptions = new MarkerOptions().position(tmpCoord).title("Bird");

                markers.put(scooterID, markerOptions);
                markerIDs.add(scooterID);
            }

            storedScooters = true;

            return new ArrayList<>(Arrays.asList(Double.toString(latitude), Double.toString(longitude), Integer.toString(batteryLevel)));

        } catch (JSONException e) {
            // No Scooters Found
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Periodically displays scooter markers on the map until scooterCoords is populated.
     */
    static void displayScooterMarkers(GoogleMap mMap) {
        // Handler to manage periodic updates
        Handler handler = new Handler();

        // Runnable that defines the task to be executed periodically
        Runnable updateMarkersRunnable = new Runnable() {
            @Override
            public void run() {
                if (storedScooters) {
                    // FIXME: createAllMarkers() is causing app crash
                    // If all scooters have been stored, call addScooterMarkers() and stop the periodic updates
                   // Log.d("TEST", "Current Size:" + scooterCoords.size() + "");
                    //Utilities.createAllMarkers(mMap, markers, markerIDs);
                    handler.removeCallbacks(this);
                } else {
                    // If scooterCoords is not populated yet, continue periodic updates
                    handler.postDelayed(this, 1000);
                }
            }
        };

        // Start the periodic updates with a delay of 1000 milliseconds (1 second)
        handler.postDelayed(updateMarkersRunnable, 1000);
    }

}
