package com.firstzoom.bluevisionlib.network;


import com.firstzoom.bluevisionlib.model.CameraInfo;
import com.firstzoom.bluevisionlib.model.Group;
import com.firstzoom.bluevisionlib.model.User;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("api/v0/auth/login/")
    Call<User> login(@Body User user);

    @POST("api/v0/auth/qr/validate/")
    Call<User> validate(@Header("bluevision-mobile-app-token") String token);

    @GET("api/v0/feeds/")
    Call<List<CameraInfo>> getCameras(@Header("Cookie") String token);

    @GET("api/v0/monitorGroups/")
    Call<List<Group>> getGroups(@Header("Cookie") String token);

    @GET("api/v0/auth/validate/")
    Call<User> validateCookie(@Header("Cookie") String token);

    @GET("api/v0/feeds/")
    Call<List<CameraInfo>> getRecordings(@Header("Cookie") String token);

    @GET("api/v0/recordings/{feedId}")
    Call<CameraInfo> getRecordings(@Header("Cookie") String token, @Path("feedId")
            UUID cameraId, @Query("start")
                                               String startDate, @Query("end") String endDate);

    @GET("api/v0/monitors/recreate/{id}")
    Call<String> refreshMonitor(@Header("Cookie") String token,@Path("id") UUID id);

/*
    @GET("api/v0/locations/")
    Call<List<Location>> getLocations(@Header("Authorization") String token);





    @GET
    Call<List<CameraInfo>> getCamerasTest(@Header("Authorization") String token, @Url String url);


    @GET("api/v0/cameras/status")
    Call<List<CameraStatus>> getReportAllCameras(@Header("Authorization") String token,@Query("start")
            String startDate,@Query("end") String endDate);

    @GET("api/v0/cameras/status/{cameraId}")
    Call<List<Status>> getReportCamera(@Header("Authorization") String token, @Path("cameraId")
            UUID cameraId, @Query("start")
            String startDate, @Query("end") String endDate);

    @GET("api/v0/cameras/status/{cameraId}")
    Call<List<Status>> getCameraInactiveReport(@Header("Authorization") String token, @Path("cameraId")
            UUID cameraId, @Query("start")
                                               String startDate, @Query("end") String endDate,@Query("status") String status);


    @POST("api/v0/auth/notifications/")
    Call<Message> setNotifications(@Header("Authorization") String token,@Body Notify notify);


    @POST("api/v0/auth/logout/")
    Call<Message> logout(@Header("Authorization") String token, @Body LogOut logOut);

    @GET("api/v0/auth/notifications/")
    Call<Notify> getNotifications(@Header("Authorization") String token);
*/
}


