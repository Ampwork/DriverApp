package com.ampwork.driverapp.directionhelper;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public interface DirectionApiCallBack {
    void onTaskDone(PolylineOptions polylineOptions);
}
