package com.firstzoom.bluevision.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firstzoom.bluevision.R;
import com.firstzoom.bluevision.model.CameraInfo;
import com.firstzoom.bluevision.model.Group;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppUtil {
    public static void showSnackbar2(View view,View anchor, String msg) {
        Snackbar s = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        s.setAnchorView(anchor);
        s.show();
    }

    public static void showSnackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
    public static void showSnackbarLong(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void setImage(Context context, String url, ImageView imageView) {
       if(url!=null)
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .into(imageView);
       else
           Glide.with(context)
                   .load("")
                   .placeholder(R.drawable.image_placeholder)
                   .centerCrop()
                   .into(imageView);

    }


    public static MaterialDatePicker getDatePicker() {
        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
        MaterialDatePicker datePicker= MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.MaterialCalendarTheme)
                .setTitleText("Select date")
                .setCalendarConstraints(calendarConstraintBuilder.build())
                .build();

        return datePicker;
    }
    public static MaterialDatePicker getDateRangePicker() {
        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
        MaterialDatePicker datePicker= MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(calendarConstraintBuilder.build())
                .build();
        return datePicker;
    }
    public static String getDisplayDate(Date date) {
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL HH:mm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL yyyy");
        String dateTime = simpleDateFormat.format(date);
        return dateTime;
    }
    public static String getEllipsizeText(String str,int length){
        if(str.length()>length)
           str=str;
            // str=StringUtils.abbreviateMiddle(str,"..",length);
        return capitalize(str);
    }
    public static String capitalize(String myString){
        if(myString!=null && !TextUtils.isEmpty(myString))
        return myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase();
        return myString;
    }
    public static void share(Context context) {
        String text=context.getString(R.string.intro)+context.getString(R.string.ps_url);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, "Choose one"));
    }
    public static void openBrowser(Context context) {
        String url=context.getString(R.string.help_url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public static String getDisplayDateMonth(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd LLL");
        String dateTime = simpleDateFormat.format(date);
        return dateTime;
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
    public static Date getTodayDate(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-7);
        return new Date();
        // return cal.getTime();
    }

}

