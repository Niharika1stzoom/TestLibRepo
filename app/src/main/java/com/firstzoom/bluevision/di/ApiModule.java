package com.firstzoom.bluevision.di;

import android.content.Context;
import android.util.Log;

import com.firstzoom.bluevision.network.ApiInterface;
import com.firstzoom.bluevision.repository.Repository;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.SharedPrefUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ApiModule {
    //public static String baseURL="https://camboss.1stzoom.com/";
   // public static String baseURL="https://test.1stzoom.com/";

    @Singleton
    @Provides
    public static ApiInterface getRestApiInterface(Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }

    @Singleton
    @Provides
    public Retrofit getRetroInstance(@ApplicationContext Context context) {
        String baseURL= getBaseUrl(context);
        Log.d(AppConstants.TAG,"Got base url"+baseURL);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
//TODO: if empty then check
    public static String getBaseUrl(@ApplicationContext Context context) {
        return SharedPrefUtils.getUrl(context);
    }

    @Singleton
    @Provides
    Repository provideRepository(ApiInterface apiInterface) {
        return new Repository(apiInterface);
    }
    public static Retrofit getInstance(String baseURL) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
