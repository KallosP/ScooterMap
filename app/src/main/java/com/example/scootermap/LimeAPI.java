package com.example.scootermap;
import android.util.Log;

import java.io.IOException;

import okhttp3.*;

public class LimeAPI {
    final static String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX3Rva2VuIjoiSkRDNUdJR0kzUUhZNCIsImxvZ2luX2NvdW50Ijo1fQ.m73bLxN_Y1TkA5kRuEVvaEf4IT6JyrMbLtLk71yLq8M";
    private static final OkHttpClient client = new OkHttpClient();

    final static String cookieValue = "JDC5GIGI3QHY4";
    final static String magicToken = "Ny1Vo136iv1p51T8yf2M1Bgs";
    final static String email = "mtelezing23@gmail.com";
    private static final String BASE_URL = "https://web-production.lime.bike/api/rider";

    // Method to send the magic link to the email
    public static void sendMagicLink(String email) {
        String url = BASE_URL + "/v2/onboarding/magic-link";

        // Create the request body with the email and user agreement details
        MediaType JSON = MediaType.parse("application/json");
        String jsonBody = "{\"email\": \"" + email + "\", \"user_agreement_version\": 4, \"user_agreement_country_code\": \"US\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    System.out.println("Error: " + response.code() + " - " + jsonResponse);
                    return;
                }

                // Success, the magic link has been sent to the email
                System.out.println("Magic link sent to your email!");
            }
        });
    }
    public static void sendMagicLinkToEmail(String email) {
        String url = BASE_URL + "/v2/onboarding/magic-link";

        MediaType JSON = MediaType.parse("application/json");
        String jsonBody = "{\"email\": \"" + email + "\", \"user_agreement_version\": 4, \"user_agreement_country_code\": \"US\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // Execute the request synchronously (for simplicity, you can make it asynchronous with callbacks)
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d("LimeAPI", "Magic Link sent to email: " + email);
            } else {
                String jsonResponse = response.body().string();
                Log.d("LimeAPI", "Error: " + response.code() + " - " + jsonResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void useMagicLink(String magicToken) {
        String url = BASE_URL + "/v2/onboarding/login";

        // Create the request body with the magic link token
        MediaType JSON = MediaType.parse("application/json");
        String jsonBody = "{\"magic_link_token\": \"" + magicToken + "\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("X-Device-Token", "43fd2a25-56c5-4d1d-b6c0-a1dab08d8e1d") // Replace with a random UUID
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("ResponseError", "Error: " + response.code() + " - " + jsonResponse);
                    return;
                }

                String jsonResponse = response.body().string();
                Log.d("LoginInfo", "Login Response: " + jsonResponse);
            }
        });
    }
    public static void getVehiclesAndZones(String authToken, String cookieValue, double neLat, double neLng, double swLat, double swLng, double userLat, double userLng, int zoom) {
        String url = BASE_URL + "/v1/views/map" +
                "?ne_lat=" + neLat +
                "&ne_lng=" + neLng +
                "&sw_lat=" + swLat +
                "&sw_lng=" + swLng +
                "&user_latitude=" + userLat +
                "&user_longitude=" + userLng +
                "&zoom=" + zoom;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "_limebike-web_session=" +  cookieValue)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("LimeAPI", "Error: " + response.code() + " - " + jsonResponse);
                    return;
                }

                String jsonResponse = response.body().string();
                Log.d("LimeAPI", "Vehicles and Zones Response: " + jsonResponse);
            }
        });
    }


}

