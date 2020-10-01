package com.ampwork.driverapp.directionhelper;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.ampwork.driverapp.activity.TripDetaiActivity;
import com.ampwork.driverapp.database.DBHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    //GoogleMap directionApiCallBack;
    DirectionApiCallBack directionApiCallBack;
    Context context;
    String bus_direction;


    public ParserTask(DirectionApiCallBack directionApiCallBack, Context context, String bus_direction) {
        this.directionApiCallBack = directionApiCallBack;
        this.context = context;
        this.bus_direction=bus_direction;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {

        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        DBHelper dbHelper;


        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
           lineOptions = new PolylineOptions().width(10).color(Color.BLACK).geodesic(true);
            DBHelper.init(context);

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            String str_path = "";

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);

                if (j > 0) {
                    str_path = str_path + ":";
                }
                str_path = str_path + point.get("lat") +","+ point.get("lng");

            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            //Add path into db.
            DBHelper.addRoutePoints(str_path,bus_direction);
        }

        if (lineOptions != null) {
            directionApiCallBack.onTaskDone(lineOptions);
            //  directionApiCallBack.addPolyline(lineOptions);
        }

        // Drawing polyline in the Google Map for the i-th route
        //map.addPolyline(lineOptions);
    }
}

