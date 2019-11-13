package com.ampwork.driverapp.Util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.model.BusStops;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;
    PreferencesManager preferencesManager;

    public CustomInfoWindow(Context context) {
        this.context = context;
        preferencesManager = new PreferencesManager(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = (View) ((Activity) context).getLayoutInflater().inflate(R.layout.map_custom_infowindow,null);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = (View) ((Activity) context).getLayoutInflater().inflate(R.layout.map_custom_info_cntent,null);
        TextView busStopNameTv = view.findViewById(R.id.busStopNameTv);
        TextView arrivalTimeTv = view.findViewById(R.id.arrivalTimeTv);

        BusStops busStops = (BusStops) marker.getTag();
        if(busStops!= null){
            busStopNameTv.setText(busStops.getBusStopName());
            if (preferencesManager.getBooleanValue(AppConstant.PREF_TRACK_ENABLED)) {
                if(busStops.getArrivalTime()!=null && busStops.getArrivalTime().length()>0){
                    arrivalTimeTv.setVisibility(View.VISIBLE);
                    arrivalTimeTv.setText("Est. Time : " + busStops.getArrivalTime());
                }else {
                    arrivalTimeTv.setVisibility(View.GONE);
                }
            }

        }

        return view;
    }
}
