package com.firstzoom.bluevision.ui.groups;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.Group;
import com.firstzoom.bluevision.repository.Repository;
import com.firstzoom.bluevision.ui.monitor.MonitorsViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GroupsViewModel extends AndroidViewModel {
    @Inject
    Repository repository;
    Context mContext;
    MutableLiveData<List<Group>> mGroupList=new MutableLiveData<>();;
    @Inject
    public GroupsViewModel(Application application) {
        super(application);
        mContext=application.getApplicationContext();
    }


    public void getMonitorGroups() {
        repository.getCameraGroups(mGroupList,mContext);
    }
    public LiveData<List<Group>> getList() {
        return mGroupList;
    }

    public ArrayList<Group> filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Group> filteredlist = new ArrayList<Group>();
        // running a for loop to compare elements.
        if(getList().getValue()!=null)
            for (Group item : getList().getValue())
                if (item.getName().toLowerCase().contains(text.toLowerCase()))
                    filteredlist.add(item);
        return filteredlist;
    }

}