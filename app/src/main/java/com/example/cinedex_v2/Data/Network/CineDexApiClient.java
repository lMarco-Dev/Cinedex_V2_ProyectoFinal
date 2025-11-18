package com.example.cinedex_v2.Data.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CineDexApiClient {

    private static Retrofit retrofit = null;

    private static final String BASE_URL = "http://cinedex.somee.com/";

    public static CineDexApiService getApiService() {
        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            // --- OKHTTP CON TIMEOUTS LARGOS (Somee es lento) ---
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(40, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true) // Reintenta si falla la conexión
                    .build();

            // --- Retrofit ---
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(CineDexApiService.class);
    }
}