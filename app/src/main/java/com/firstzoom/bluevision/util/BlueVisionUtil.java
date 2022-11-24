package com.firstzoom.bluevision.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.Group;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlueVisionUtil {
    public static String getFeedUrl(CameraInfo item, Context mContext) {
        return SharedPrefUtils.getUrl(mContext)+"hls/"+item.getId().toString()+"/index.m3u8";
    }
    public static String getRecordingUrl(UUID id, Context mContext, String fileName) {
        http://65.1.109.51/0b6b7a70-6685-11ed-b411-edb314f7d4d0/2022-11-17_20-05-54.mp4
        return SharedPrefUtils.getUrl(mContext)+"/"+id.toString()+"/"+fileName;
    }

    public static Bundle getBundleUrl(String feedUrl) {
        Bundle bundle =new Bundle();
        bundle.putString(AppConstants.KEY_URL,feedUrl);
        return bundle;
    }

    public static Bundle getGroupBundle(Group item) {
        Bundle bundle =new Bundle();
        bundle.putSerializable(AppConstants.KEY_CAMERA_GROUP,item);
        return bundle;
    }
    public static Bundle getCameraBundle(CameraInfo item) {
        Bundle bundle =new Bundle();
        bundle.putSerializable(AppConstants.KEY_CAMERA_INFO,item);
        return bundle;
    }

    public static String getImageUrl(CameraInfo item,Context mContext) {
        //http://localhost:27574/53a00f10-17c8-11ed-9d0c-53183d47fd91.jpeg
        return SharedPrefUtils.getUrl(mContext)+item.getId().toString()+".jpeg";
    }

    public static String validateBaseUrl(String url) {
        URI uri=null;
        try {
            uri = new URI (url);
        } catch (URISyntaxException e) {
            Log.d(AppConstants.TAG,"url "+url+e.getLocalizedMessage());
        }
        String scheme=uri.getScheme();
        if(scheme==null)
        {
            String newUrl="https://"+url;
            try {
                uri = new URI (newUrl);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
        String authority=uri.getAuthority();
        if(authority!=null && isValidURLSequence(url)) {
            if (authority.contains("www.")) {
                authority = authority.replace("www.", "");
                Log.d(AppConstants.TAG, "Repla authority" + authority);
            }
            String baseURL = "https://" + authority + "/";
            Log.d(AppConstants.TAG, "Created base url" + baseURL);
            return baseURL;
        }
        else
            return "";

    }


    // Function to validate URL
// using regular expression
   public static boolean isValidURLSequence(String url)
    {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }


    public static ExoPlayer getExoPlayer(Context mContext) {
        HttpDataSource.Factory httpDataSourceFactory =
                new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
        DataSource.Factory dataSourceFactory = () -> {
            HttpDataSource dataSource = httpDataSourceFactory.createDataSource();
            // Set a custom authentication request header.
            dataSource.setRequestProperty("Cookie", SharedPrefUtils.getUser(mContext).getToken());
            return dataSource;
        };

        ExoPlayer player = new ExoPlayer.Builder(mContext)
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(mContext)
                                .setDataSourceFactory(dataSourceFactory))
                .build();
        return player;
    }

    public static String getFileName(String item) {
        String parts[] = item.split("/");
        return parts[parts.length -1];
    }
    public static String getParts(String item) {
        String parts[] = item.split("/");
     return parts[parts.length-2];
    }


    public static Date getDateFromFileName(String name, Date date) {
        String parts[] = name.split("_");
        String time=parts[1];
        int hourOfDay=Integer.parseInt(time.substring(0,2));
        int mins=Integer.parseInt(time.substring(3,5));
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR,hourOfDay);
        c.set(Calendar.MINUTE,mins);
        return c.getTime();
    }

}
