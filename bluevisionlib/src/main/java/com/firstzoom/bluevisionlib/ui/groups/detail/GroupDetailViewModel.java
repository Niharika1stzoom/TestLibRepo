package com.firstzoom.bluevisionlib.ui.groups.detail;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.firstzoom.bluevisionlib.model.CameraDatabaseSingleton;
import com.firstzoom.bluevisionlib.model.CameraInfo;
import com.firstzoom.bluevisionlib.model.Group;
import com.firstzoom.bluevisionlib.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GroupDetailViewModel extends AndroidViewModel {
    @Inject
    Repository repository;
    Context mContext;
    List<CameraInfo> mCameraList;
    Group group;
    @Inject
    public GroupDetailViewModel(Application application) {
        super(application);
        mContext=application.getApplicationContext();
    }
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        setCameraList();
    }

    public ArrayList<CameraInfo> filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<CameraInfo> filteredlist = new ArrayList<CameraInfo>();
        // running a for loop to compare elements.
        if(getList()!=null)
            for (CameraInfo item : getList())
                if (item.getName().toLowerCase().contains(text.toLowerCase()))
                    filteredlist.add(item);
        return filteredlist;
    }

    public List<CameraInfo> getList() {
        return mCameraList;
    }

    public void setCameraList() {
        if(group!=null)
        mCameraList= CameraDatabaseSingleton.getCameraInfoList(group.getFeeds());
    }

    public void pausePlayers(RecyclerView recyclerView) {
        int i = 0;
        if (getGroup() != null && getGroup().getFeeds() != null && getGroup().getFeeds().size()>0)
            for (UUID id : getGroup().getFeeds()) {
                GroupDetailAdapter.GroupDetailViewHolder holder = (GroupDetailAdapter.GroupDetailViewHolder) recyclerView.findViewHolderForAdapterPosition(i++);
                if (holder!=null && holder.mBinding!=null && holder.mBinding.thumbnail.getPlayer() != null)
                    holder.mBinding.thumbnail.getPlayer().stop();

            }
    }


    public void releasePlayers(RecyclerView recyclerView) {
        int i = 0;
        if (getGroup() != null && getGroup().getFeeds() != null && getGroup().getFeeds().size()>0)
            for (UUID id : getGroup().getFeeds()) {
                GroupDetailAdapter.GroupDetailViewHolder holder = (GroupDetailAdapter.GroupDetailViewHolder) recyclerView.findViewHolderForAdapterPosition(i++);
                if (holder!=null && holder.mBinding!=null && holder.mBinding.thumbnail.getPlayer() != null)
                    holder.mBinding.thumbnail.getPlayer().release();
            }
    }

    public void refreshCamera(CameraInfo info) {
        repository.refreshMonitor(info.getId(),mContext);
    }
}