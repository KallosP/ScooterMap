package com.example.scootermap;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    final String email = "retepsollak@gmail.com";
    private static final String BASE_URL = "https://api-auth.prod.birdapp.com/api/v1/auth/email";
    private static final MediaType JSON = MediaType.parse("application/json");
    private static final String USER_AGENT = "Bird/4.119.0(co.bird.Ride; build:3; Android 10)";
    private static final String GUID = UUID.randomUUID().toString();

    // Bird token
    static String token = "eyJhbGciOiJSUzUxMiJ9.eyJqdGkiOiJiMjQ0ZmRlMy0wNDRlLTRmOWUtYmZhNi1iOTM0YTA2MGUyZWIiLCJzdWIiOiJkMjMwNDNjMC01NjYxLTRkMTctOTNkMC1iOWQ2ZWYwOTkzYzQiLCJuYmYiOjE2OTA5MTk0NjIsImV4cCI6MTY5MTAwNTg2MiwiYXVkIjoiYmlyZC5zZXJ2aWNlcyIsImlzcyI6ImJpcmQuYXV0aCIsImlhdCI6MTY5MDkxOTQ2Miwicm9sZXMiOlsiVVNFUiJdLCJhcHAiOiI3YjhlZDk1NS02ZTNhLTRlZWMtYmEyMC04OGFmOWQ3YWVhNzYiLCJ2ZXIiOiIwLjAuMiJ9.gekVgRFujLRwaLapgb2z1IiprT-9ptIhLGC_G4XxIEWErroaNTtXScka-r7m_yIAAwTAW7-U6LLGz1mhfckQWNQYFm8mxVtxazSirVJRUUFT16ROlrevq84a1uejpR7mMcFLgJ4C8C76DSZTZJfNvvz3KlfYp_ruSX0J49lgwyc4S3QtUyeyFJ5RlbGz4vholUP_2X_KB2U_iEOozlxa_7s30_hldUJIW8UvhPkKGjyaz-Hu0rf4ITqVUaDK4UNFXyjgpcE670ziyiqNAuGkEOfJ7KfUaWQCkvogARUjg4mZeW9NDDAUzS436_txcVW5k2CNEX9jdmjqFeJtKo3TMOd-15uJo_fuCsSM1y8uQWjqPvNXmCxv92KoBmgjIbJZPSWIIYoy_1cJuHIf5aOpmSUGTwfwhK1x8tGZMy04Fyjj89vYybsdRyiOlpX94WJTwBkwddnYuzx_Reo2i5dKRAON6qGQSvuMGwHT0i05x3dr8PTKQ-ZaqhAl0Ixql3L2RyQe_ztaTCRRW1FL6_IZMDx9sfB9MJZFj99jpTlo5RfVY7PtHNr4GIAVEuMdoWYapNxeKRtdKG9sDaeOX4b4YzH5qTmIcd8F1apwfPc6D6A_ou9xe2KJcYIGm2pSGCkjgLrYXG7aYUfxTN_EvJyx415wfU95Kk96ACDaSNmEs2I";

    static ArrayList<String> scooters;
    static ArrayList<LatLng> scooterCoords = new ArrayList<>();

    static boolean scooterCoordsPopulated = false;

    // TODO:
    //        - change lat/long in REQUEST LOCATION to user's current lat/long
    //        - create system for getting, refreshing, and using AUTH token
    static void setupAPIs() {

        // HTTP client
        OkHttpClient client = new OkHttpClient();

        // GET AUTH TOKEN VIA EMAIL (WORKING)
//        String requestBody = "{\"email\":\"" + email + "\"}";
//        RequestBody body = RequestBody.create(JSON, requestBody);
//
//        Request request = new Request.Builder()
//                .url(BASE_URL)
//                .header("User-Agent", USER_AGENT)
//                .header("Device-Id", GUID)
//                .header("Platform", "android")
//                .header("App-Version", "4.119.0")
//                .header("Content-Type", "application/json")
//                .post(body)
//                .build();

        // USE AUTH TOKEN (WORKING)
//        String requestBody = "{\"token\":\"" + token + "\"}";
//        RequestBody body = RequestBody.create(JSON, requestBody);
//        Request request = new Request.Builder()
//                .url("https://api-auth.prod.birdapp.com/api/v1/auth/magic-link/use")
//                .header("Device-Id", GUID)
//                .post(body)
//                .build();

        // REFRESH AUTH TOKEN (MUST USE REFRESH TOKEN)
//        String requestBody = "{\"token\":\"" + token + "\"}";
//        RequestBody body = RequestBody.create(JSON, requestBody);
//
//        Request request = new Request.Builder()
//                .url("https://api-auth.prod.birdapp.com/api/v1/auth/refresh/token")
//                .addHeader("User-Agent", USER_AGENT)
//                .addHeader("Device-Id", GUID)
//                .addHeader("Platform", "android")
//                .addHeader("App-Version", "4.119.0")
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer " + token)
//                .post(body)
//                .build();

        //        // REQUEST LOCATION (MUST USE ACCESS TOKEN)
        Request request = new Request.Builder()
                .url("https://api-bird.prod.birdapp.com/bird/nearby?latitude=37.77184&longitude=-122.40910&radius=1000")
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", USER_AGENT)
                .header("legacyrequest", "false")
                .header("Device-Id", GUID)
                .header("App-Version", "4.119.0")
                .header("Location", "{\"latitude\":34.07005148224879,\"longitude\":-118.44380658840988,\"altitude\":500,\"accuracy\":65,\"speed\":-1,\"heading\":-1}")
                .get()
                .build();

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

    static ArrayList<String> organizeScooterData(String scooterData){
        try {
            JSONObject jsonObject = new JSONObject(scooterData);
            JSONArray birdsArray = jsonObject.getJSONArray("birds");

            double latitude = -1, longitude = -1;
            int batteryLevel = -1;

            for (int i = 0; i < birdsArray.length(); i++) {
                JSONObject scooterObject = birdsArray.getJSONObject(i);
                JSONObject locationObject = scooterObject.getJSONObject("location");

                latitude = locationObject.getDouble("latitude");
                longitude = locationObject.getDouble("longitude");

                batteryLevel = scooterObject.getInt("battery_level");

                // Log.d("TEST", "Scooter " + i + " Latitude: " + latitude + ", Longitude: " + longitude + ", Battery: " + batteryLevel);
                scooterCoords.add(new LatLng(latitude, longitude));
            }

            scooterCoordsPopulated = true;

            return new ArrayList<>(Arrays.asList(Double.toString(latitude), Double.toString(longitude), Integer.toString(batteryLevel)));

        } catch (JSONException e) {
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
                if (scooterCoordsPopulated) {
                    // If scooterCoords is populated, call addScooterMarkers() and stop the periodic updates
                    Utilities.convertCoordsToMarker(mMap, scooterCoords);
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
