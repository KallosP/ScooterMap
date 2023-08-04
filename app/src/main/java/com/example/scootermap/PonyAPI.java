package com.example.scootermap;
import java.io.IOException;

import okhttp3.*;
public class PonyAPI {
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    private static final String BASE_URL = "https://api.getapony.com";

    // Method to get the nearest region based on latitude and longitude
    public static void getVehicleParkingLocations(String city) {
        String url = "https://gbfs.getapony.com/v1/" + city + "/en/station_information.json";
        Request request = new Request.Builder()
                .url(url)
                .get()
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

                    System.out.println("Error: " + response.code());
                    return;
                }

                String jsonResponse = response.body().string();
                System.out.println("Vehicle Parking Locations Response: " + jsonResponse);

            }
        });
    }

    public static void getNearestRegion(double latitude, double longitude) {
        String url = BASE_URL + "/georegion/resolve?latitude=" + latitude + "&longitude=" + longitude;
        Request request = new Request.Builder()
                .url(url)
                .get()
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
                    System.out.println("Error: " + response.code());
                    return;
                }

                String jsonResponse = response.body().string();
                System.out.println("Nearest Region Response: " + jsonResponse);
            }
        });
    }


    // Method to get the list of regions (cities)
    public static void getRegionsList() {
        String url = BASE_URL + "/georegion/all/";
        Request request = new Request.Builder()
                .url(url)
                .get()
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
                    System.out.println("Error: " + response.code());
                    return;
                }

                String jsonResponse = response.body().string();
                System.out.println("Regions List Response: " + jsonResponse);
            }
        });
    }

    // Other methods for getting vehicle types, prices, real-time information, geofencing zones, and parking locations.
    // You can implement similar methods to make the respective GET requests.

}

