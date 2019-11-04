package com.ampwork.driverapp.busarrivaltimehelper;

import com.ampwork.driverapp.model.BusStops;

import java.util.ArrayList;

public interface ArrivalTimeCallBack {
    void onTaskDone(ArrayList<BusStops> busStopsArrayList);
}
