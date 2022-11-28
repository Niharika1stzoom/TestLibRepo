package com.firstzoom.bluevision.ui.recording;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.SliderTime;
import com.firstzoom.bluevision.repository.Repository;
import com.firstzoom.bluevision.ui.groups.detail.GroupDetailAdapter;
import com.firstzoom.bluevision.util.AppConstants;
import com.firstzoom.bluevision.util.AppUtil;
import com.firstzoom.bluevision.util.BlueVisionUtil;
import com.firstzoom.bluevision.util.DateUtil;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecordingsViewModel extends AndroidViewModel {
    @Inject
    Repository repository;
    Context mContext;
    private final MutableLiveData<Date> mDate = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDurationVideo = new MutableLiveData<>();
    MutableLiveData<CameraInfo> recordingInfo=new MutableLiveData<>();
    MutableLiveData<SliderTime> sliderTime=new MutableLiveData<>();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    //This does not have recordings
    private CameraInfo cameraInfo;
    @Inject
    public RecordingsViewModel(Application application) {
        super(application);
        mContext=application.getApplicationContext();
        mDate.setValue(AppUtil.getTodayDate());
        sliderTime.setValue(new SliderTime(DateUtil.atStartOfDay(AppUtil.getTodayDate()),DateUtil.atEndOfDay(AppUtil.getTodayDate())));
    }

    public LiveData<Integer> getDurationVideo() {
        return mDurationVideo;
    }

    public void setSliderTime(SliderTime time) {
        sliderTime.setValue(time);
    }

    public LiveData<SliderTime> getSliderTime() {
        return sliderTime;
    }

    public LiveData<CameraInfo> getList() {
        return recordingInfo;
    }

    public CameraInfo getCameraInfo() {
        return cameraInfo;
    }

    public ArrayList<String> filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<String> filteredlist = new ArrayList<String>();
        // running a for loop to compare elements.
        if(getList().getValue() != null && getList().getValue().getRecordings()!=null && getList().getValue().getRecordings().size()>0)
            for (String item : getList().getValue().getRecordings())
                if (item.toLowerCase().contains(text.toLowerCase()))
                    filteredlist.add(item);
                   return filteredlist;
    }

    public void pausePlayers(RecyclerView recyclerView) {
        int i = 0;
        if (getList().getValue() != null && getList().getValue().getRecordings()!=null && getList().getValue().getRecordings().size()>0)
            for (String c : getList().getValue().getRecordings()) {
                RecordingsAdapter.RecordingViewHolder holder = (RecordingsAdapter.RecordingViewHolder) recyclerView.findViewHolderForAdapterPosition(i++);

                if (holder!=null && holder.mBinding!=null && holder.mBinding.thumbnail.getPlayer() != null)
                    holder.mBinding.thumbnail.getPlayer().stop();

            }
    }
    public void releasePlayers(RecyclerView recyclerView) {
        int i = 0;
        if (getList().getValue() != null && getList().getValue().getRecordings()!=null && getList().getValue().getRecordings().size()>0 )
            for (String c : getList().getValue().getRecordings()) {
                RecordingsAdapter.RecordingViewHolder holder = (RecordingsAdapter.RecordingViewHolder) recyclerView.findViewHolderForAdapterPosition(i++);
                if (holder!=null && holder.mBinding!=null && holder.mBinding.thumbnail.getPlayer() != null)
                    holder.mBinding.thumbnail.getPlayer().release();
            }
    }


    public void setCamera(CameraInfo info) {
        cameraInfo=info;
    }

    public void getRecordings() {
        String start = DateUtil.getUTC(DateUtil.atStartOfDay(mDate.getValue()));
        String end = DateUtil.getUTC(DateUtil.atEndOfDay(mDate.getValue()));
        repository.getRecordings(recordingInfo, mContext, start, end,cameraInfo);
    }

    public LiveData<Date> getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate.setValue(date);
        mDurationVideo.setValue(0);
        sliderTime.setValue(new SliderTime(DateUtil.atStartOfDay(date),DateUtil.atEndOfDay(date)));
    }

    public List<String> getFilterByTimeList() {
        ArrayList<String>  filterList=new ArrayList<>();
        if(recordingInfo.getValue()!=null && recordingInfo.getValue().getRecordings()!=null && recordingInfo.getValue().getRecordings().size()>0)
        for(String name:recordingInfo.getValue().getRecordings()) {
            Date startTime = BlueVisionUtil.getDateFromFileName(name, mDate.getValue());
            if (startTime != null){
                Date endTime=null;
                if(mDurationVideo.getValue()!=null && mDurationVideo.getValue()!=0)
                endTime = BlueVisionUtil.addMinutes(startTime, mDurationVideo.getValue());
                if (sliderTime.getValue().getStart().before(startTime) && sliderTime.getValue().getEnd().after(startTime) ||
                       endTime!=null && sliderTime.getValue().getStart().before(endTime) && sliderTime.getValue().getEnd().after(endTime)
                )
                    filterList.add(name);
            }
        }
        return filterList;
    }

    public void getDuration() {
        executor.execute(() -> {
           mDurationVideo.postValue(BlueVisionUtil.getDurationVideoUrl(recordingInfo.getValue().getRecordings().get(0)));
        });
    }
}