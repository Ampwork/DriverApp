package com.ampwork.driverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.activity.BusStatusActivity;
import com.ampwork.driverapp.service.LiveTrackingService;

public class StopLocUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, LiveTrackingService.class));
        new PreferencesManager(context).setBooleanValue(AppConstant.PREF_IS_DRIVING,false);
    }
}
