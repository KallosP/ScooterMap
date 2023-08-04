package com.example.scootermap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
public class SpinAPI {
    static String token = "U2PvtR6Z681OQGpNrEom4g";
    private static final MediaType JSON = MediaType.parse("application/json");

    static void useAuth() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);
        OkHttpClient client = new OkHttpClient();
        String apiUrl = "https://web.spin.pm/api/v1/auth_tokens";
        String email2 = "mtelezing23@gmail.com";
        String requestBody = "{" +
                "\"magic_link\": {" +
                "\"email\": \"" + email2 + "\"," +
                "\"token\": \"" + token + "\"" +
                "}" +
                "}";
        RequestBody body = RequestBody.create(JSON, requestBody);
        Request get = new Request.Builder().url(apiUrl)
                .post(body)
                .build();
        manageClientResponse(client, get);
    }




    static void manageClientResponse(OkHttpClient client, Request request) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Log.d("TEST", "Failure");
                //e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int responseCode = response.code();
                Log.d("TEST", "Response Code: " + responseCode);

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("TEST", "Response Body: " + responseBody);
                }
            }

        });
    }
//    static void doInBackground() {
//        OkHttpClient client = new OkHttpClient();
//        String apiUrl = "https://web.spin.pm/api/v1/magic_links";
//        String email2 = "mtelezing23@gmail.com";
//        String requestBody = "{\"email\":\"" + email2 + "\"}";
//        RequestBody body = RequestBody.create(JSON, requestBody);
//        Request get = new Request.Builder().url(apiUrl)
//                .post(body)
//                .build();
//        manageClientResponse(client,get);
//    }


}

