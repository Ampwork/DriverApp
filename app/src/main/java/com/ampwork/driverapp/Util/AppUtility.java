package com.ampwork.driverapp.Util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppUtility {

    public static String locationToStr(Location location){
        String str_location = "";
        if(location!=null){
            str_location = String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude());
        }
        return str_location;
    }

    public static LatLng strToLatlng(String location){
        String str_location_arr[] = location.split(",");
        LatLng latLng = new LatLng(Double.parseDouble(str_location_arr[0]),Double.parseDouble(str_location_arr[1]));
        return latLng;
    }

    public static Location strToLocation(String location) {

        String str_location_arr[] = location.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(str_location_arr[0]), Double.parseDouble(str_location_arr[1]));

        Location busLocation = new Location(LocationManager.GPS_PROVIDER);
        busLocation.setLongitude(latLng.longitude);
        busLocation.setLatitude(latLng.latitude);
        return busLocation;
    }
    public static double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public static String getCurrentDateTime(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Long covertDateToMili(String date_time){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date  = dateFormat.parse(date_time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getNotificationDay(String input) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String currentTimeStr = simpleDateFormat.format(new Date());
        Date currentDay = null,inputDay=null;
        try {
            currentDay = simpleDateFormat.parse(currentTimeStr);
            inputDay = simpleDateFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = currentDay.getTime() - inputDay.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));

        String differenceStr = String.valueOf(days);

        if(days==0){
            differenceStr = "Today";
        }else if(days==1){
            differenceStr = "Yesterday";
        }else {
            differenceStr = input;
        }

        return differenceStr;


    }


    public static String getCurrentTimeStamp(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return ts;
    }

       public static String getTimeDifference(String stopTimeStr,String startTimeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date stopTime = null,startTime=null;
        try {
            stopTime = simpleDateFormat.parse(stopTimeStr);
            startTime = simpleDateFormat.parse(startTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = stopTime.getTime() - startTime.getTime();
       /* int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);*/

        String differenceStr = String.valueOf(difference/(1000*60));

        return differenceStr;


    }




    public static String getCurrentDate(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String getArrivalTime(String start_time,String travel_time){
        int bus_stand_time = 0; //bus waiting time at bus stop in minutes.
        String myTime = start_time;//"02:50";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = df.parse(myTime);
        } catch (ParseException e) {
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        //Extract hour and minute from travel time.then add it to starttime.
        String[] timeArray = travel_time.split(":");
        cal.add(Calendar.HOUR,Integer.parseInt(timeArray[0]));
        cal.add(Calendar.MINUTE,Integer.parseInt(timeArray[1])+ bus_stand_time);
        String arrivalTime = df.format(cal.getTime());
        return  arrivalTime;

    }

    public static String getTimeDifferenceMinutes(String arrivalTimeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String currentTimeStr = simpleDateFormat.format(new Date());
        Date currentTime = null,arrivalTime=null;
        try {
            currentTime = simpleDateFormat.parse(currentTimeStr);
            arrivalTime = simpleDateFormat.parse(arrivalTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = currentTime.getTime() - arrivalTime.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);

        /*String differenceStr = hours + "," + min;*/
        String differenceStr = String.valueOf(difference/(1000*60));

        return differenceStr;


    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    public static String getNextTripTime(String tripSchedules){

        String nextTripTime = "";
        String[] trip_times = tripSchedules.split(",");
        for (int i =0; i<trip_times.length;i++){
            int time_diff = Integer.parseInt(getTimeDifferenceMinutes(trip_times[i]));
            if(time_diff<=0){
                nextTripTime = trip_times[i];
                break;
            }
        }

        if(nextTripTime.isEmpty()){
            nextTripTime = trip_times[0];
        }
        return nextTripTime;

    }


}
