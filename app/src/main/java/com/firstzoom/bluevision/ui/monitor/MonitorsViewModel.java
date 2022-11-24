package com.firstzoom.bluevision.ui.monitor;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.repository.Repository;

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
    @Inject
    public MonitorsViewModel(Application application) {
        super(application);
        mContext=application.getApplicationContext();
    }


    public void getMonitors() {
        repository.getCameras(mCameraList,mContext);
    }
    public LiveData<List<CameraInfo>> getList() {
        return mCameraList;
    }

    public ArrayList<CameraInfo> filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<CameraInfo> filteredlist = new ArrayList<CameraInfo>();
        // running a for loop to compare elements.
        if(getList().getValue()!=null)
            for (CameraInfo item : getList().getValue())
                if (item.getName().toLowerCase().contains(text.toLowerCase()))
                    filteredlist.add(item);
                return filteredlist;
    }

    public void refreshCamera(CameraInfo info) {
        repository.refreshMonitor(info.getId(),mContext);
    }
}