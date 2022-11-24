package com.firstzoom.bluevision.util;

import android.text.format.DateUtils;
import android.util.Log;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static String getDisplayDate(Date date) {
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL HH:mm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd");
        String dateTime = simpleDateFormat.format(date);
        return dateTime;
    }
    public static String getDisplayTime(Date date) {
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL HH:mm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String dateTime = simpleDateFormat.format(date);
        return dateTime;
    }
    public static Timestamp atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTime().getTime());
    }
    public static Timestamp atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTime().getTime());
    }

   public static int getTimeInMins(Date time){
        long millis=time.getTime();
       int finalMins= (int) TimeUnit.MILLISECONDS.toMinutes(millis);

        int mins = time.getMinutes();
        int hour = time.getHours();
        int timeInMins = hour * 60 + mins;
        Log.d(AppConstants.TAG,"Mins"+finalMins+" "+mins);
        return timeInMins;
    }

    public static String getUTC(Date date){
        String st= ISO8601Utils.format(date);
        return st;
    }



    public static Date getDateFromMins(Float value, Date date) {
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        int hours = (int) Math.floor(Math.round(value) / 60);
        int mins = (int) (Math.round(value) % 60);
        c.set(Calendar.HOUR,hours);
        c.set(Calendar.MINUTE,mins);
        return c.getTime();

    }

}
