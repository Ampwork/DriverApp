package com.ampwork.driverapp.geofencing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.busarrivaltimehelper.ArrivalTimeCallBack;
import com.ampwork.driverapp.busarrivaltimehelper.GetBusArrivalTime;
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.model.BusStops;
import com.ampwork.driverapp.model.LiveBusDetail;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeofenceTransitionsIntentService extends JobIntentService implements ArrivalTimeCallBack {

    private static final int JOB_ID = 573;

    private static final String TAG = "GeofenceTransitionsIS";

    PreferencesManager preferencesManager;
    Context mContext;
    // GeoFencing geoFencing;


    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        preferencesManager = new PreferencesManager(this);
        this.mContext = GeofenceTransitionsIntentService.this;
        // geoFencing = new GeoFencing(mContext);

        // Enqueues a JobIntentService passing the context and intent as parameters
        // GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = (String) GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Get the geofences that were triggered. A single event can trigger
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        Geofence geofence = triggeringGeofences.get(0);

        Log.d(TAG, "onHandleWork: "+ geofence.getRequestId() + " list siz" + triggeringGeofences.size() + " transition" + geofenceTransition );

        String stop_order = geofence.getRequestId();
        DBHelper.init(this);
        BusStops busStops = DBHelper.getBusStopData(stop_order);
        Boolean start_btn_activated = preferencesManager.getBooleanValue(AppConstant.PREF_BTN_START);
        Boolean isDriving = preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING);


        if (busStops != null && isDriving && start_btn_activated) {

            if ((geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)) {
                String busStopsCovered = preferencesManager.getStringValue(AppConstant.PREF_BUS_STOPS_COVERED);
                if (!busStopsCovered.contains(busStops.getBusStopOrder())) {
                    busStopsCovered = busStopsCovered + "," + busStops.getBusStopOrder();
                    preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_COVERED, busStopsCovered);
                }

            }

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, true);

                String start_point_order = preferencesManager.getStringValue(AppConstant.PREF_START_POINT_ID);
                String dest_stop_order = preferencesManager.getStringValue(AppConstant.PREF_END_POINT_ID);
                Boolean is_destination_reached = preferencesManager.getBooleanValue(AppConstant.PREF_DEST_REACHED);
                Boolean is_track_enabled = preferencesManager.getBooleanValue(AppConstant.PREF_TRACK_ENABLED);
                Boolean is_trip_completed = preferencesManager.getBooleanValue(AppConstant.PREF_TRIP_COMPLETED);
                Boolean isUpdateServer = false;


                if (is_track_enabled) {
                    if (!is_trip_completed) {
                        if (!is_destination_reached) {
                            if (busStops.getBusStopOrder().equalsIgnoreCase(dest_stop_order)) {
                                is_destination_reached = true;
                                is_trip_completed = true;
                            }
                            preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, is_trip_completed);
                            preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, is_destination_reached);
                            if (is_destination_reached) {
                                String dest_loc = busStops.getLatitude() + "," + busStops.getLongitude();
                                String bus_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_PATH);
                                if (!bus_path.contains(dest_loc)) {
                                    bus_path = bus_path + ":" + dest_loc;
                                    preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, bus_path);
                                }
                                isUpdateServer = true;
                            }

                        }
                    }
                } else {
                    if (busStops.getBusStopOrder().equalsIgnoreCase(start_point_order)) {
                        is_track_enabled = true;
                        preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, true);

                        String start_loc = busStops.getLatitude() + "," + busStops.getLongitude();
                        String bus_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_PATH);
                        if (!bus_path.contains(start_loc)) {
                            bus_path = bus_path + ":" + start_loc;
                            preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, bus_path);
                        }

                        isUpdateServer = true;

                    }

                }

                if (isUpdateServer) {

                    String busPath = preferencesManager.getStringValue(AppConstant.PREF_BUS_PATH);
                    String nextStopName = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP);
                    String nextStopOrderId = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP_ORDER);


                    HashMap<String, Object> objectHashMap = LiveBusDetail.getGeofenceEntranceObj(is_destination_reached, nextStopName, nextStopOrderId, is_track_enabled, is_trip_completed, busPath);
                    String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
                    String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bus_tracking_tb/" + institute_key + "/" + bus_key);
                    databaseReference.updateChildren(objectHashMap);

                    //broadCastData(null);

                }


            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, false);

                String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
                String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_BUSTRACKING_TB);
                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put(AppConstant.PREF_STR_BELL_COUNTS, "0");
                databaseReference.child(institute_key).child(bus_key).updateChildren(objectHashMap);

                updateBusData(busStops);

                Log.i(TAG, geofence.getRequestId());

            } else {
                // Log the error.
                Log.e(TAG, this.getString(R.string.geofence_transition_invalid_type,
                        geofenceTransition));
            }
        }

    }

    private void updateBusData(BusStops busStops) {

        String drive_direction = preferencesManager.getStringValue(AppConstant.PREF_DRIVING_DIRECTION);
        String start_point_order = preferencesManager.getStringValue(AppConstant.PREF_START_POINT_ID);
        String dest_stop_order = preferencesManager.getStringValue(AppConstant.PREF_END_POINT_ID);

        Boolean is_destination_reached = preferencesManager.getBooleanValue(AppConstant.PREF_DEST_REACHED);
        Boolean is_track_enabled = preferencesManager.getBooleanValue(AppConstant.PREF_TRACK_ENABLED);
        Boolean is_trip_completed = preferencesManager.getBooleanValue(AppConstant.PREF_TRIP_COMPLETED);

        int next_stop_order = -1;
        BusStops nextBusStop = null;

        if (!is_track_enabled) {
            if (busStops.getBusStopOrder().equalsIgnoreCase(start_point_order)) {
                is_track_enabled = true;
                preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, true);
            }

        }

        // if the track is enabled and destination is not reached display the bus status with bus stops.
        if (!is_destination_reached && is_track_enabled) {
            if (drive_direction.equalsIgnoreCase("-1")) {
                if (busStops.getBusStopOrder().equalsIgnoreCase(dest_stop_order)) {
                    is_destination_reached = true;
                } else {
                    next_stop_order = Integer.parseInt(busStops.getBusStopOrder()) + 1;
                }
            } else {
                if (busStops.getBusStopOrder().equalsIgnoreCase(dest_stop_order)) {
                    is_destination_reached = true;

                } else {
                    next_stop_order = Integer.parseInt(busStops.getBusStopOrder()) - 1;
                }
            }
            preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, is_destination_reached);
            if (is_destination_reached) {
                String dest_loc = busStops.getLatitude() + "," + busStops.getLongitude();
                String bus_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_PATH);
                if (!bus_path.contains(dest_loc)) {
                    bus_path = bus_path + ":" + dest_loc;
                    preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, bus_path);
                }
            }

        }

        if (!is_trip_completed) {
            if (is_destination_reached) {
                preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, true);
            }

        }


        if (next_stop_order != -1 || is_trip_completed || is_destination_reached || is_track_enabled) {

            if (next_stop_order != -1) {

                nextBusStop = DBHelper.getBusStopData(String.valueOf(next_stop_order));

                preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP, nextBusStop.getBusStopName());
                preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP_ORDER, nextBusStop.getBusStopOrder());

            }

          /*  String nextStopName = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP);
            String nextStopOrderId = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP_ORDER);

            HashMap<String, Object> objectHashMap = LiveBusDetail.getBusStopsUpdateObj(is_destination_reached, nextStopName, nextStopOrderId, is_track_enabled, is_trip_completed);
            String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
            String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bus_tracking_tb/" + institute_key + "/" + bus_key);
            databaseReference.updateChildren(objectHashMap);
*/

        }
        broadCastData(nextBusStop);
    }

    private void broadCastData(BusStops busStops) {
        Intent intent1 = new Intent(AppConstant.STR_GEOFENCE_FILTER);
        intent1.putExtra("next_stop", busStops);
        LocalBroadcastManager.getInstance(GeofenceTransitionsIntentService.this).sendBroadcast(intent1);

    }

    @Override
    public void onTaskDone(ArrayList<BusStops> busStopsArrivalTimeArrayList) {
        if (busStopsArrivalTimeArrayList != null && busStopsArrivalTimeArrayList.size() > 0) {
            String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
            String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bus_arrivaltime_tb");
            databaseReference.child(institute_key).child(bus_key).setValue(busStopsArrivalTimeArrayList);
        }

    }

}
