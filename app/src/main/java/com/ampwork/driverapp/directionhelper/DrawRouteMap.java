package com.ampwork.driverapp.directionhelper;

import android.content.Context;
import android.os.AsyncTask;

import com.ampwork.driverapp.database.DBHelper;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawRouteMap extends AsyncTask<Void,Void, PolylineOptions> {
    PolylineOptions lineOptions = null;
    Context mContext;
    DirectionApiCallBack directionApiCallBack;

    public DrawRouteMap(Context mContext) {
        this.mContext = mContext;
        this.directionApiCallBack = (DirectionApiCallBack) mContext;
    }

    @Override
    protected PolylineOptions doInBackground(Void... voids) {
        DBHelper.init(mContext);
        lineOptions = DBHelper.getAllRoutePoints();
        return lineOptions;
    }

    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        super.onPostExecute(polylineOptions);
        if (lineOptions != null) {
            directionApiCallBack.onTaskDone(lineOptions);
            //  directionApiCallBack.addPolyline(lineOptions);
        }
    }
}
