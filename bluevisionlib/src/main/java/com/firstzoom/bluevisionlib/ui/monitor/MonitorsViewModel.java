package com.firstzoom.bluevisionlib.ui.monitor;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firstzoom.bluevisionlib.model.CameraInfo;
import com.firstzoom.bluevisionlib.repository.Repository;
import com.firstzoom.bluevisionlib.util.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MonitorsViewModel extends AndroidViewModel {
    @Inject
    Repository repository;
    Context mContext;
    MutableLiveData<List<CameraInfo>> mCameraList=new MutableLiveData<>();
    MutableLiveData<String> mLoginResult=new MutableLiveData<>();
    @Inject
    public MonitorsViewModel(Application application) {
        super(application);
        mContext=application.getApplicationContext();
    }

    public MutableLiveData<String> getLoginResult() {
        return mLoginResult;
    }

    public void setLoginResult(String result) {
        mLoginResult.setValue(result);
    }

    public void getMonitors() {
        repository.getCameras(mCameraList,mContext,mLoginResult);
    }
    public LiveData<List<CameraInfo>> getList() {
        return mCameraList;
    }

    public ArrayList<CameraInfo> filter(String text) {
        ArrayList<CameraInfo> filteredlist = new ArrayList<CameraInfo>();
        if(getList().getValue()!=null)
            for (CameraInfo item : getList().getValue())
                if (item.getName().toLowerCase().contains(text.toLowerCase()))
                    filteredlist.add(item);
                return filteredlist;
    }

    public void refreshCamera(CameraInfo info) {
        repository.refreshMonitor(info.getId(),mContext);
    }

    public void logout() {
        SharedPrefUtils.delUser(mContext);
        SharedPrefUtils.delToken(mContext);
       // SharedPrefUtils.delUrl(mContext);
    }
}