package com.ampwork.driverapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.model.Driver;
import com.ampwork.driverapp.model.LiveBusDetail;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class OfflineDataUpdateService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.ampwork.driverapp.service.action.FOO";
    private static final String ACTION_BAZ = "com.ampwork.driverapp.service.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.ampwork.driverapp.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.ampwork.driverapp.service.extra.PARAM2";

    public OfflineDataUpdateService() {
        super("OfflineDataUpdateService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, OfflineDataUpdateService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, OfflineDataUpdateService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


   /* private void callUpdateOfflineLogs() {
        DBHelper.init(this);
        ArrayList<BusLog> busLogArrayList = DBHelper.getAllBuslogs();
        if (busLogArrayList.size() > 0) {
            for (BusLog busLog : busLogArrayList) {
                updateDraftLogs(busLog);
            }
        }else {
            mapFragment.getMapAsync(this);
        }
    }

    private void updateDraftLogs(final BusLog logDetail) {

        // insert log and reset Bustrack table.
        Driver driver = getDriverData();
        LiveBusDetail liveBusDetail = getLiveBusObject();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
        String driver_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_KEY);


        Map<String, Object> childUpdates = new HashMap<>();
        if (Double.parseDouble(logDetail.getTripDistance()) > 0) {
            // update the logs.
            String log_key = databaseReference.push().getKey();
            childUpdates.put(AppConstant.PREF_STR_BUS_LOGS_TB + "/" + institute_key + "/" + bus_key + "/" + log_key, logDetail);
            childUpdates.put(AppConstant.PREF_STR_DRIVERS_TB + "/" + institute_key + "/" + driver_key, driver);
        }
        childUpdates.put(AppConstant.PREF_STR_BUSTRACKING_TB + "/" + institute_key + "/" + bus_key, liveBusDetail);


        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                DBHelper.deleteBuslogsTable(logDetail.getArrivedTime());
            }
        });
    }*/
}
