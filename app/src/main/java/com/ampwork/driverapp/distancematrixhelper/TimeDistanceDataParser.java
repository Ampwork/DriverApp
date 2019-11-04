package com.ampwork.driverapp.distancematrixhelper;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimeDistanceDataParser extends AsyncTask<String ,Void, ArrayList<String>> {

    DistanceMatrixCallBack distanceMatrixCallBack;
    ArrayList<String> busStopsDurationArrayList = new ArrayList<>();
    ArrayList<String> busStopsDistanceArrayList = new ArrayList<>();

    public TimeDistanceDataParser(Context context) {
        this.distanceMatrixCallBack = (DistanceMatrixCallBack) context; }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        String duration = "0";
        String response = strings[0];
        busStopsDurationArrayList.clear();
        try {
            JSONObject jsonResponse = new JSONObject(response.toString());

            JSONArray jsonRowArray = jsonResponse.getJSONArray("rows");
            for (int i = 0; i < jsonRowArray.length(); i++) {

                JSONObject object = jsonRowArray.getJSONObject(i);
                JSONArray elementArray = object.getJSONArray("elements");
                for(int j=0;j<elementArray.length();j++){
                    JSONObject jsonObject = elementArray.getJSONObject(j);
                    JSONObject distanceObject = jsonObject.getJSONObject("distance");
                    JSONObject durationObject = jsonObject.getJSONObject("duration");//duration_in_traffic

                    String distance = distanceObject.getString("text");
                    duration = durationObject.getString("text");

                    busStopsDistanceArrayList.add(distance);
                    busStopsDurationArrayList.add(duration+","+distance);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return busStopsDurationArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> stringArrayList) {
        super.onPostExecute(stringArrayList);
        if(busStopsDurationArrayList!=null) {
            distanceMatrixCallBack.onTaskDone(busStopsDurationArrayList);
        }
    }

}
