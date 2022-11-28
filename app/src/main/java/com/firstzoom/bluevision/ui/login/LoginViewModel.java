package com.firstzoom.bluevision.ui.login;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.di.ApiModule;
import com.firstzoom.bluevision.repository.Repository;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.SharedPrefUtils;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    Context mContext;
   // @Inject
    //public Repository repository;

    @Inject
    LoginViewModel(Application application) {
        super(application);
        mContext=application.getApplicationContext();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }


    public void login(String username, String password, String url) {
        if(url!=null)
        SharedPrefUtils.saveUrl(mContext,url);
        Log.d(AppConstants.TAG,"Retrofit url is"+SharedPrefUtils.getUrl(mContext));
        new Repository(ApiModule.getRestApiInterface(ApiModule.getInstance(SharedPrefUtils.getUrl(mContext)))).login(username, password,loginResult,mContext);
        //repository.login(username, password,loginResult,mContext);
    }
    public void validate(String url) {
        Log.d(AppConstants.TAG,"Retrofit url through link is"+SharedPrefUtils.getUrl(mContext));
        new Repository(ApiModule.getRestApiInterface(
                ApiModule.getInstance(url))).validate(loginResult,mContext);

    }

    public void loginDataChanged(String username, String password,String url) {
          if (!isUrlEmpty(url) || isUrlValid(url)==null) {
            loginFormState.setValue(new LoginFormState(null,null, R.string.invalid_url));
        }else
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null,null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password,null));
        }

        else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }


     String isUrlValid(String url) {
        URI uri=null;
         String scheme=null,authority=null;
        try {
            uri = new URI (url);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(uri!=null)
        scheme=uri.getScheme();
        if(scheme==null)
        {
            String newUrl="https://"+url;
            try {
                uri = new URI (newUrl);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
        if(uri!=null)
        authority=uri.getAuthority();
        if(authority!=null && isValidURLSequence(url)) {
            if(authority.contains("www.")){
                authority=authority.replace("www.","");
            }
            String baseURL = "https://" + authority + "/";
            Log.d(AppConstants.TAG, "base url is" + baseURL);
            Log.d(AppConstants.TAG, "parts url is" + uri.getHost() + " " + uri.getScheme() + " " + uri.getAuthority());
            return baseURL;
        }
        else{
            loginFormState.setValue(new LoginFormState(null,null, R.string.invalid_url));
            return null;
        }
         // if (!isUrlValid(url))

    }


    // Function to validate URL
// using regular expression
    boolean isValidURLSequence(String url)
    {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
            }





    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if(!username.contains("@")){
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }
    private boolean isUrlEmpty(String username) {
        if (username == null) {
            return false;
        }
        else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

 /*  void getRegistrationToken() {
        //  String token = SharedPrefUtils.getFcmToken(mContext);
        // if (token == null || TextUtils.isEmpty(token))
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult();
                        SharedPrefUtils.saveFcmToken(mContext, token);
                    }
                });
    }*/
}