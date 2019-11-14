package com.ampwork.driverapp.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.AppUtility;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.busarrivaltimehelper.ArrivalTimeCallBack;
import com.ampwork.driverapp.busarrivaltimehelper.GetBusArrivalTime;
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.model.BusStops;
import com.ampwork.driverapp.model.LiveBusDetail;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class LiveTrackingService extends Service implements ArrivalTimeCallBack {

    private static final String TAG = "LiveBusTracking";
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    PreferencesManager preferencesManager;

    private LocationCallback locationCallback;


    private ArrayList<BusStops> busStopsArrayList = new ArrayList<BusStops>();
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public LiveTrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesManager = new PreferencesManager(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {

                    String current_loc = AppUtility.locationToStr(location);
                    preferencesManager.setStringValue(AppConstant.PREF_BUS_LOCATION, current_loc);

                    String bus_full_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_FULL_PATH);
                    if (!bus_full_path.contains(current_loc)) {
                        bus_full_path = bus_full_path + ":" + current_loc;
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_FULL_PATH, bus_full_path);
                    }


                    /*Calculate Bus Speed*/
                    float speed = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        speed = location.getSpeedAccuracyMetersPerSecond() * 18 / 5;
                    } else {
                        speed = location.getSpeed() * 18 / 5;
                    }
                    preferencesManager.setFloatValue(AppConstant.PREF_BUS_SPEED, speed);
                    // Log.d(TAG, "Speed: " + speed);


                    /*Calculate Bus Travelled Distance meters*/
                    if (bus_full_path.contains(":")) {
                        String[] path = bus_full_path.split(":");
                        if (path.length > 2) {
                            /*Calculate Bus Travelled  meters*/
                            float distance = preferencesManager.getFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED);
                            //String strLastLoc = preferencesManager.getStringValue(AppConstant.PREF_BUS_LAST_LOCATION);
                            String strLastLoc = path[path.length - 2];
                            if (strLastLoc != null && strLastLoc.trim().length() > 0) {
                                Location last_location = AppUtility.strToLocation(strLastLoc);
                                distance = (distance + last_location.distanceTo(location));
                            }
                            // Log.d(TAG, "Distance: " + distance);

                            if (distance > 0) {
                                preferencesManager.setFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED, distance);
                            }
                        }
                    }
                    // Log.d(TAG, "onLocationResult: " + location);
                    //Save the location data to the database//
                    UpdateDatabase(location);

                }
            }
        };

        buildNotification();
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        busStopsArrayList = intent.getParcelableArrayListExtra("bus_stops_list");
        return START_NOT_STICKY;

    }

    //Create the persistent notification//
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        String channel_id = createNotificationChannel("my_service", "My Background Service");

        // Create the persistent notification//
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channel_id)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("updating bus location..")
                    .setCategory(Notification.CATEGORY_SERVICE)

                    //Make this notification ongoing so it can’t be dismissed by the user//

                    .setOngoing(true)
                    //.setContentIntent(broadcastIntent)
                    .setSmallIcon(R.mipmap.ic_app_launcher);
        } else {
            builder = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("updating bus location..")
                    .setOngoing(true)
                    //.setContentIntent(broadcastIntent)
                    .setSmallIcon(R.mipmap.ic_app_launcher);
        }
        startForeground(1, builder.build());
    }

    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            service.createNotificationChannel(chan);
        }

        return channelId;
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);

            preferencesManager.setBooleanValue(AppConstant.PREF_IS_DRIVING, false);
            //Stop the Service//
            stopSelf();
        }
    };


    private void requestLocationUpdates() {

        locationRequest = new LocationRequest();//LocationRequest.create();

        //Specify how often your app should request the device’s location//
        locationRequest.setInterval(0/*UPDATE_INTERVAL_IN_MILLISECONDS*/);
        locationRequest.setFastestInterval(0/*FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS*/);
        locationRequest.setSmallestDisplacement(AppConstant.PREF_BUS_DISPLACEMENT);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                startLocationUpdate();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                Log.d(TAG, "onFailure: Location settings are not satisfied ");
            }
        });


    }

    private void startLocationUpdate() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        //If the app currently has access to the location permission...//
        if (permission == PackageManager.PERMISSION_GRANTED) {

            //...then request location updates//
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

    }

    private void UpdateDatabase(Location location) {

        // get Data from sharedPreference.
        Boolean start_btn_activated = preferencesManager.getBooleanValue(AppConstant.PREF_BTN_START);
        String busLocation = preferencesManager.getStringValue(AppConstant.PREF_BUS_LOCATION);
        Boolean isDriving = preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING);
        String nextStopName = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP);
        String nextStopOrderId = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP_ORDER);
        String direction = preferencesManager.getStringValue(AppConstant.PREF_DRIVING_DIRECTION);


        String start_point_order = preferencesManager.getStringValue(AppConstant.PREF_START_POINT_ID);
        String dest_stop_order = preferencesManager.getStringValue(AppConstant.PREF_END_POINT_ID);


        Boolean is_destination_reached = preferencesManager.getBooleanValue(AppConstant.PREF_DEST_REACHED);
        Boolean is_track_enabled = preferencesManager.getBooleanValue(AppConstant.PREF_TRACK_ENABLED);
        Boolean is_trip_completed = preferencesManager.getBooleanValue(AppConstant.PREF_TRIP_COMPLETED);
        String bus_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_PATH);


        // when driver start the trip set isDriving true; and update trackEnabled flag.
        if (!isDriving && start_btn_activated) {

            /*if (direction.equalsIgnoreCase("-1")) {
                preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, true);
                preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, true);
                //Since the bus will start from start point , add start point order,
                preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_COVERED, ",0");
                is_track_enabled = true;
            }*/
            String depart_time = AppUtility.getCurrentDateTime();
            preferencesManager.setStringValue(AppConstant.PREF_BUS_DEPART_TIME, depart_time);

            preferencesManager.setBooleanValue(AppConstant.PREF_IS_DRIVING, true);
            isDriving = true;

            if (is_track_enabled) {
                updateBusArrivalTiming(depart_time, direction);
            }
        }


        /*Find the Bus next BusStop*/
        if (!preferencesManager.getBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS)) {
            int next_stop_order = -1;
            String busStopsCovered = preferencesManager.getStringValue(AppConstant.PREF_BUS_STOPS_COVERED);
            BusStops busStops = null;

            Log.d(TAG, " bus stop array list" + busStopsArrayList.size() + " last stop " + dest_stop_order);

            if (direction.equalsIgnoreCase("-1")) {
                for (int i = 0; i <= busStopsArrayList.size() - 1; i++) {
                    busStops = busStopsArrayList.get(i);
                    Location nextStopLoc = AppUtility.strToLocation(busStops.getLatitude() + "," + busStops.getLongitude());
                    float distance = location.distanceTo(nextStopLoc);
                    if (distance < 50) {
                        next_stop_order = i;
                        Log.d(TAG, "next Bus Stop: " + i + " bus stop array list" + busStopsArrayList.size());
                        break;
                    }
                }

            } else {
                for (int j = busStopsArrayList.size() - 1; j >= 0; j--) {
                    busStops = busStopsArrayList.get(j);
                    Location nextStopLoc = AppUtility.strToLocation(busStops.getLatitude() + "," + busStops.getLongitude());
                    float distance = location.distanceTo(nextStopLoc);
                    if (distance < 50) {
                        next_stop_order = j;
                        break;
                    }
                }
            }

            if (next_stop_order > -1) {

                nextStopOrderId = String.valueOf(next_stop_order);
                nextStopName = busStopsArrayList.get(next_stop_order).getBusStopName();

                if (!is_track_enabled) {

                    if (nextStopOrderId.equalsIgnoreCase(start_point_order)) {
                        String depart_time = AppUtility.getCurrentDateTime();
                        is_track_enabled = true;
                        preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, true);
                        updateBusArrivalTiming(depart_time,direction);
                    }
                }
                if (is_track_enabled) {

                    if (!busStopsCovered.contains(nextStopOrderId)) {
                        busStopsCovered = busStopsCovered + "," + nextStopOrderId;
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_COVERED, busStopsCovered);
                    }

                    if (nextStopOrderId.equalsIgnoreCase(dest_stop_order)) {
                        is_destination_reached = true;
                        is_trip_completed = true;
                        preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, true);
                        preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, true);

                        String dest_loc = busStops.getLatitude() + "," + busStops.getLongitude();
                        if (!bus_path.contains(dest_loc)) {
                            bus_path = bus_path + ":" + dest_loc;
                            preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, bus_path);
                        }
                    }

                    preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP, nextStopName);
                    preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP_ORDER, nextStopOrderId);
                    preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, true);
                }

            }

        }


        /*Handle Enter and Exit of bus inside Busstop*/
        if (preferencesManager.getBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS)) {
            if (!is_trip_completed) {
                int nextOrder = Integer.parseInt(preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP_ORDER));
                BusStops busStops = busStopsArrayList.get(nextOrder);
                Location nextStopLoc = AppUtility.strToLocation(busStops.getLatitude() + "," + busStops.getLongitude());
                float distance = location.distanceTo(nextStopLoc);
               /* if (distance < 50) {

                    String currentStopOrderId = nextStopOrderId;
                    preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, true);

                    if (!is_track_enabled) {
                        is_track_enabled = true;
                        preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, true);
                    } else {

                        if (currentStopOrderId.equalsIgnoreCase(dest_stop_order)) {
                            is_destination_reached = true;
                            is_trip_completed = true;
                            preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, true);
                            preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, true);
                        }
                    }
                } else */
                if (distance > 50) {
                    if (preferencesManager.getBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS)) {
                        int next_stop_order = Integer.parseInt(nextStopOrderId);
                        if (direction.equalsIgnoreCase("-1")) {
                            next_stop_order = Integer.parseInt(nextStopOrderId) + 1;
                        } else if (direction.equalsIgnoreCase("1")) {
                            next_stop_order = Integer.parseInt(nextStopOrderId) - 1;
                        }

                        nextStopOrderId = String.valueOf(next_stop_order);
                        nextStopName = busStopsArrayList.get(next_stop_order).getBusStopName();

                        preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP, nextStopName);
                        preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP_ORDER, nextStopOrderId);

                        preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, false);


                        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
                        String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_BUSTRACKING_TB);
                        HashMap<String, Object> objectHashMap = new HashMap<>();
                        objectHashMap.put(AppConstant.PREF_STR_BELL_COUNTS, "0");
                        databaseReference.child(institute_key).child(bus_key).updateChildren(objectHashMap);

                    }

                }
            }
        }



      /* if(!is_track_enabled){
            if (direction.equalsIgnoreCase("-1")) {
                BusStops busStops = busStopsArrayList.get(0);
                double distance = AppUtility.meterDistanceBetweenPoints((float) location.getLatitude(), (float) location.getLongitude(), Float.valueOf(busStops.getLatitude()), Float.valueOf(busStops.getLongitude()));
                if (distance <= AppConstant.GEOFENCE_RADIUS) {
                    preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, true);
                    preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, true);
                    is_track_enabled = true;
                }
            }
        }*/
        if (is_track_enabled && !is_destination_reached) {
            // If the track is enabled update the bus path.
            if (!bus_path.contains(busLocation)) {
                bus_path = bus_path + ":" + busLocation;
                preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, bus_path);
            }

        }

        if (isDriving && start_btn_activated) {
            // Update Database with new location.
            HashMap<String, Object> objectHashMap = LiveBusDetail.getLocationUpdateObj(busLocation, bus_path, is_destination_reached, direction,
                    isDriving, nextStopName, nextStopOrderId, is_track_enabled, is_trip_completed);

            String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
            String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bus_tracking_tb/" + institute_key + "/" + bus_key);
            databaseReference.updateChildren(objectHashMap);

        }

        // Update UI
        if (location != null) {
            //Calling LocalBroadcast listener to receive UI Updates.
            Intent intent1 = new Intent(AppConstant.STR_LOCATION_FILTER);
            intent1.putExtra("bus_lattitude", location.getLatitude());
            intent1.putExtra("bus_longitude", location.getLongitude());
            LocalBroadcastManager.getInstance(LiveTrackingService.this).sendBroadcast(intent1);
        }

    }

    private void updateBusArrivalTiming(String depart_time, String direction) {
        String[] array = depart_time.split(" ");
        String trip_depart_time = array[1];
        preferencesManager.setStringValue(AppConstant.PREF_SELECTED_TRIP_TIME, trip_depart_time);

        if (busStopsArrayList != null && busStopsArrayList.size() > 0) {
            String bus_trip_time = preferencesManager.getStringValue(AppConstant.PREF_SELECTED_TRIP_TIME);
            new GetBusArrivalTime(busStopsArrayList, direction, LiveTrackingService.this).execute(bus_trip_time);
        }
    }

    @Override
    public void onTaskDone(ArrayList<BusStops> busStopsArrivalTimeArrayList) {
        if (busStopsArrivalTimeArrayList != null && busStopsArrivalTimeArrayList.size() > 0) {
            String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
            String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bus_arrivaltime_tb");
            databaseReference.child(institute_key).child(bus_key).setValue(busStopsArrivalTimeArrayList);

            for (BusStops busStops : busStopsArrivalTimeArrayList) {
                DBHelper.init(this);
                DBHelper.addStops(busStops);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopReceiver);
        stopLocationUpdates();

    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

}
