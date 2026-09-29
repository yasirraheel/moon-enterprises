package com.geo.enterprises;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.geo.enterprises.api.ApiClient;

import java.io.InputStream;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Ensure Glide uses our configured OkHttpClient (with DirectDns) for all image requests
        try {
            Glide.get(this).getRegistry().replace(
                    GlideUrl.class,
                    InputStream.class,
                    new OkHttpUrlLoader.Factory(ApiClient.getOkHttpClient())
            );
        } catch (Exception e) {
            android.util.Log.e("App", "Failed to register OkHttpUrlLoader for Glide: " + e.getMessage());
        }
    }
}
