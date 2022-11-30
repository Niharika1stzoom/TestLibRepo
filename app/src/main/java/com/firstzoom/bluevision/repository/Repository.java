package com.firstzoom.bluevision.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.model.CameraDatabaseSingleton;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.Group;
import com.firstzoom.bluevision.model.LogOut;
import com.firstzoom.bluevision.model.Message;
import com.firstzoom.bluevision.model.User;
import com.firstzoom.bluevision.network.ApiInterface;
import com.firstzoom.bluevision.ui.login.LoginResult;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.SharedPrefUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private ApiInterface mApiInterface;
    public Repository(ApiInterface apiInterface) {
        mApiInterface = apiInterface;
    }
    public void login(String username, String password,
                      MutableLiveData<LoginResult> liveData, Context context) {
        Call<User> call = mApiInterface.login(new User(username, password));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,
                                   @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    if(response.body().getLoggedIn()) {
                        String cookie = response.headers().get("Set-Cookie");
                        User user = response.body();
                        user.setToken(cookie);
                        liveData.setValue(new LoginResult(user));
                        setLoggedInUser(user, context);
                    }
                    else {
                        liveData.setValue(new LoginResult(R.string.login_failed));

                    }
                } else {
                    liveData.setValue(new LoginResult(R.string.login_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                liveData.postValue(new LoginResult(R.string.login_failed));
                Log.d(AppConstants.TAG, "Failure" + t.getLocalizedMessage());
            }
        });
    }
    public void validate(
            MutableLiveData<LoginResult> liveData, Context context) {
        Call<User> call = mApiInterface.validate(SharedPrefUtils.getToken(context));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,
                                   @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                        if(response.body().getLoggedIn()){
                        String cookie = response.headers().get("Set-Cookie");
                        User user = response.body();
                        user.setToken(cookie);
                        liveData.setValue(new LoginResult(user));
                        setLoggedInUser(user, context);

                        }
                    else {

                        liveData.setValue(new LoginResult(R.string.login_failed));
                        }
                } else {
                    liveData.setValue(new LoginResult(R.string.link_expired));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                liveData.setValue(new LoginResult(R.string.login_unable));
                Log.d(AppConstants.TAG, "Failure" + t.getLocalizedMessage());
            }
        });
    }
    private void setLoggedInUser(User user, Context context) {
        SharedPrefUtils.setUser(context, user);
        SharedPrefUtils.saveUsername(context,user.email);
    }

    void delUser(Context context) {
        //setFirebaseCrashylyticsDetails(context,"");
        SharedPrefUtils.delUser(context);
    }
    public static String getHeaderToken(Context context) {
        //cookie
        return SharedPrefUtils.getUser(context).getToken();
    }


  /*  public void validate(
            MutableLiveData<Boolean> liveData, Context context) {
        Call<User> call = mApiInterface.validateCookie(getHeaderToken(context));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,
                                   @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body().getLoggedIn());
                } else {
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                liveData.postValue(null);
                Log.d(AppConstants.TAG, "Failure" + t.getLocalizedMessage());
            }
        });
    }
*/
    public void getCameras(MutableLiveData<List<CameraInfo>> liveData, Context mContext,MutableLiveData<String> sessionData) {
        Call<List<CameraInfo>> call = mApiInterface.getCameras(getHeaderToken(mContext));
        call.enqueue(new Callback<List<CameraInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<CameraInfo>> call,
                                   @NonNull Response<List<CameraInfo>> response) {
                if (response.isSuccessful()) {

                    if(response.body().size()>0)
                    Collections.sort(response.body(), new Comparator<CameraInfo>() {
                        @Override
                        public int compare(CameraInfo lhs, CameraInfo rhs) {
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    CameraDatabaseSingleton.getInstance().setCameraList(response.body());
                    liveData.postValue(response.body());
                } else {
                    if(response.code()==401)
                    {
                       //Log.d(AppConstants.TAG,"logging out" +response.code());
                      sessionData.postValue(AppConstants.LOGOUT);
                       //   loginCookie(SharedPrefUtils.getUser(mContext).getEmail(),SharedPrefUtils.getUser(mContext).getPassword(), new MutableLiveData<LoginResult>(),mContext,liveData);
                    }
                    else
                        liveData.postValue(null);

                }
            }
            @Override
            public void onFailure(@NonNull Call<List<CameraInfo>> call, @NonNull Throwable t) {
                Log.d(AppConstants.TAG, "Camera fetch failed" + t.getLocalizedMessage()+t.getCause());
                liveData.postValue(null);
            }
        });
    }
/*
    private void loginCookie(String username, String password, MutableLiveData<LoginResult> liveData, Context context, MutableLiveData<List<CameraInfo>> liveDataCamera) {
        Call<User> call = mApiInterface.login(new User(username, password));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,
                                   @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    if(response.body().getLoggedIn()) {
                        Log.d(AppConstants.TAG,"In login Cookie");
                        String cookie = response.headers().get("Set-Cookie");
                        User user = response.body();
                        user.setToken(cookie);
                        user.setEmail(username);
                        Log.d(AppConstants.TAG, "The user is" + response.body().getLoggedIn());
                        liveData.setValue(new LoginResult(user));
                        setLoggedInUser(user, context);
                        getCameras(liveDataCamera,context,null);
                    }
                    else
                        liveData.setValue(new LoginResult(R.string.login_failed));
                } else {
                    liveData.setValue(new LoginResult(R.string.login_failed));
                    Log.d(AppConstants.TAG,"Fail"+response.message()+response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                liveData.postValue(new LoginResult(R.string.login_failed));
                Log.d(AppConstants.TAG, "Failure" + t.getLocalizedMessage());
            }
        });
    }*/

    public void getCameraGroups(MutableLiveData<List<Group>> liveData, Context mContext) {
        Call<List<Group>> call = mApiInterface.getGroups(getHeaderToken(mContext));
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(@NonNull Call<List<Group>> call,
                                   @NonNull Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    if(response.body().size()>0)
                    Collections.sort(response.body(), new Comparator<Group>() {
                        @Override
                        public int compare(Group lhs, Group rhs) {
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Group>> call, @NonNull Throwable t) {
                Log.d(AppConstants.TAG, "Camera groups fetch failed" + t.getLocalizedMessage()+t.getCause());
                liveData.postValue(null);
            }
        });
    }

    public void getRecordings(MutableLiveData<CameraInfo> liveData, Context mContext,
                              String start, String end, CameraInfo cameraInfo) {
        Call<CameraInfo> call = mApiInterface.getRecordings(getHeaderToken(mContext),cameraInfo.getId(),start,end);
        call.enqueue(new Callback<CameraInfo>() {
            @Override
            public void onResponse(@NonNull Call<CameraInfo> call,
                                   @NonNull Response<CameraInfo> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null && response.body().getRecordings()!=null && response.body().getRecordings().size()>0)
                        liveData.postValue(response.body());
                    else
                        liveData.postValue(null);
                } else {
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CameraInfo> call, @NonNull Throwable t) {
                Log.d(AppConstants.TAG, "Camera fetch failed for cam" + t.getLocalizedMessage()+t.getCause());
                liveData.postValue(null);
            }
        });
    }

    public void refreshMonitor(UUID id,Context mContext){
        Call<String> call= mApiInterface.refreshMonitor(getHeaderToken(mContext),id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                   @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                } else {
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d(AppConstants.TAG, "Refresh Failure" + t.getLocalizedMessage());
            }
        });
    }
}
