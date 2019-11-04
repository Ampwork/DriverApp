package com.ampwork.driverapp.busarrivaltimehelper;

import android.content.Context;
import android.os.AsyncTask;

import com.ampwork.driverapp.Util.AppUtility;
import com.ampwork.driverapp.model.BusStops;

import java.util.ArrayList;
import java.util.Collections;

public class GetBusArrivalTime extends AsyncTask<String, Void, ArrayList<String>> {

    private ArrayList<BusStops> busStopsArrayList;
    String drive_direction;

    ArrivalTimeCallBack arrivalTimeCallBack;

    public GetBusArrivalTime(ArrayList<BusStops> busStopsArrayList, String drive_direction, Context context) {
        this.busStopsArrayList = busStopsArrayList;
        this.drive_direction = drive_direction;
        this.arrivalTimeCallBack = (ArrivalTimeCallBack) context;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        String start_time = strings[0];
        ArrayList<String> busStopsArrivalTimeArrayList = new ArrayList<String>();

        if (drive_direction.equalsIgnoreCase("-1")) {
            for (int i = 0; i < busStopsArrayList.size(); i++) {
                String travel_time = "";
                BusStops busStops = busStopsArrayList.get(i);
                if (i == 0) {
                    busStopsArrivalTimeArrayList.add(start_time);
                } else {

                    String time_required = busStops.tripTime;// arrival time 2 hours 30 minutes
                    String[] timeArray = time_required.split(" ");
                    if (timeArray.length > 2) {
                        travel_time = timeArray[0] + ":" + timeArray[2];
                    } else {
                        travel_time = "00" + ":" + timeArray[0];
                    }

                    start_time = AppUtility.getArrivalTime(start_time, travel_time);
                    busStopsArrivalTimeArrayList.add(start_time);
                }
            }
        } else {
            for (int i = busStopsArrayList.size() - 1; i >= 0; i--) {
                String travel_time = "";
                BusStops busStops = busStopsArrayList.get(i);

                if (i == busStopsArrayList.size() - 1) {
                    busStopsArrivalTimeArrayList.add(start_time);
                } else {
                    String time_required = busStopsArrayList.get(i + 1).tripTime;// arrival time 2 hours 30 minutes
                    String[] timeArray = time_required.split(" ");
                    if (timeArray.length > 2) {
                        travel_time = timeArray[0] + ":" + timeArray[2];
                    } else {
                        travel_time = "00" + ":" + timeArray[0];
                    }

                    start_time = AppUtility.getArrivalTime(start_time, travel_time);
                    busStopsArrivalTimeArrayList.add(start_time);
                }

            }
        }

        return busStopsArrivalTimeArrayList;
    }



    @Override
    protected void onPostExecute(ArrayList<String> stringArrayList) {
        super.onPostExecute(stringArrayList);

        if (stringArrayList != null && stringArrayList.size() > 0) {
            ArrayList<BusStops> busStopsWATArrayList = new ArrayList<BusStops>();

            if (drive_direction.equalsIgnoreCase("1")) {
                Collections.reverse(stringArrayList);
            }

            for (int i=0;i<stringArrayList.size();i++){

                BusStops busStops = busStopsArrayList.get(i);
                //busStops.setBusStopOrder(String.valueOf(i));

                String calculated_arrival_time = stringArrayList.get(i);

                busStops.setArrivalTime(calculated_arrival_time);

                busStopsWATArrayList.add(busStops);
            }

            arrivalTimeCallBack.onTaskDone(busStopsWATArrayList);
        }
    }
}
