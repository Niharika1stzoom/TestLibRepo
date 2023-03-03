package com.firstzoom.bluevisionlib.model;

import com.firstzoom.bluevisionlib.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CameraDatabaseSingleton {
    private static final Object LOCK = new Object();
    public static List<CameraInfo> cameraList;
    private static CameraDatabaseSingleton dInstance;


    public static CameraDatabaseSingleton getInstance() {
        if (dInstance == null) {
            synchronized (LOCK) {
                dInstance = new CameraDatabaseSingleton();
            }
        }
        return dInstance;
    }

    public static String[] getCameraNames(List<UUID> feeds) {
        String[] arr=new String[feeds.size()];
        int i=0;
        if(feeds!=null)
        for(UUID id:feeds){
            CameraInfo cameraInfo=getCameraInfo(id);
            if(cameraInfo!=null)
                arr[i++]= AppUtil.capitalize(cameraInfo.getName());
        }
        return arr;
    }

    public static List<CameraInfo> getCameraList() {
        return cameraList;
    }

    public static void setCameraList(List<CameraInfo> list) {
        cameraList = list;
    }
    public static CameraInfo getCameraInfo(UUID uuid){
        if(cameraList!=null)
        for(CameraInfo c:getCameraList())
            if(c.getId().equals(uuid))
                return c;
            return null;

    }

    public static List<CameraInfo> getCameraInfoList(List<UUID> feeds) {
        List<CameraInfo> cameraInfoList=new ArrayList<>();
       if(feeds!=null)
        for(UUID id:feeds){
            CameraInfo cameraInfo=getCameraInfo(id);
            if(cameraInfo!=null)
                cameraInfoList.add(cameraInfo);
        }
        return cameraInfoList;
    }
}
