package com.ampwork.driverapp.directionhelper;

import android.content.Context;
import android.os.AsyncTask;

import com.ampwork.driverapp.database.DBHelper;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawRouteMap extends AsyncTask<Void,Void, PolylineOptions> {
    PolylineOptions lineOptions = null;
    Context mContext;
    DirectionApiCallBack directionApiCallBack;
    String bus_direction;

    public DrawRouteMap(Context mContext, String bus_direction) {
        this.mContext = mContext;
        this.directionApiCallBack = (DirectionApiCallBack) mContext;
        this.bus_direction=bus_direction;
    }

    @Override
    protected PolylineOptions doInBackground(Void... voids) {
        DBHelper.init(mContext);
        lineOptions = DBHelper.getAllRoutePoints(bus_direction);
        return lineOptions;
    }

    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        if (lineOptions != null) {
            directionApiCallBack.onTaskDone(lineOptions);
            //  directionApiCallBack.addPolyline(lineOptions);
        }
    }
}
