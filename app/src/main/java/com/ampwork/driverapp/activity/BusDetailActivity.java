package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.model.BusStops;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BusDetailActivity extends AppCompatActivity {

    private EditText routeNameEdt, busNameEdt, busNumberEdt, busReadingEdt;
    private TextView busTimingTv, busStopsTv, totalTripsTv, distanceTv, mileageTv, fuelReadingTv;
    private CardView busStopsView;
    private ImageView backImageView;

    PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_detail);

        preferencesManager = new PreferencesManager(this);

        backImageView = findViewById(R.id.backImageView);

        routeNameEdt = findViewById(R.id.routeNameEdt);
        busNameEdt = findViewById(R.id.busNameEdt);
        busNumberEdt = findViewById(R.id.busNumberEdt);
        busReadingEdt = findViewById(R.id.busReadingEdt);


        busStopsView = findViewById(R.id.busStopsView);

        busTimingTv = findViewById(R.id.busTimingTv);
        busStopsTv = findViewById(R.id.busStopsTv);
        totalTripsTv = findViewById(R.id.totalTripsTv);
        distanceTv = findViewById(R.id.distanceTv);
        mileageTv = findViewById(R.id.mileageTv);
        fuelReadingTv = findViewById(R.id.fuelReadingTv);

        routeNameEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME));
        busNameEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME));
        busNumberEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER));
        busReadingEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE));

        String str_bus_timings = preferencesManager.getStringValue(AppConstant.PREF_SC_DEPART_TIME);
        String bus_timing = "";
        String[] array = str_bus_timings.split(",");

        SpannableString str_separator = new SpannableString(", ");
        str_separator.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_blue)), 0, str_separator.length(), 0);

        for (String string : array) {
            bus_timing = bus_timing + string + str_separator;
        }
        bus_timing.trim();
        str_bus_timings = bus_timing.substring(0, bus_timing.length() - 2);
        busTimingTv.setText(str_bus_timings);


        String str_bus_stops = preferencesManager.getStringValue(AppConstant.PREF_BUS_STOPS_LIST);

        if (str_bus_stops.length() > 0) {

            displayBusStopList(str_bus_stops);

        } else {
            getBusStopsList();
        }


        totalTripsTv.setText(preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_TRIPS));
        distanceTv.setText(String.format("%.2f", Double.parseDouble(preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_TRIP_DISTANCE))));
        fuelReadingTv.setText(preferencesManager.getStringValue(AppConstant.PREF_TOTAL_FUEL));
        mileageTv.setText(preferencesManager.getStringValue(AppConstant.PREF_MILEAGE));

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void displayBusStopList(String str_bus_stops) {
        if (str_bus_stops.length() > 0) {
            String bus_stop_list = "";
            String[] stops_array = str_bus_stops.split(",");
            for (String string : stops_array) {
                bus_stop_list = bus_stop_list + string + ", ";
            }
            bus_stop_list.trim();
            str_bus_stops = bus_stop_list.substring(0, bus_stop_list.length() - 2);

            busStopsView.setVisibility(View.VISIBLE);
            busStopsTv.setText(str_bus_stops);
        } else {
            busStopsView.setVisibility(View.GONE);
        }
    }

    private void getBusStopsList() {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(BusDetailActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Busstops list.");
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
                String route_key = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
                DatabaseReference databaseReference;
                databaseReference = FirebaseDatabase.getInstance().getReference("busstops_tb" + "/" + institute_key + "/" + route_key);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        // clear the table
                        DBHelper.init(BusDetailActivity.this);
                        DBHelper.deleteGeofenceShopsTable();
                        String bus_stops_list = "";
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            BusStops busStops = snapshot.getValue(BusStops.class);
                            busStops.setArrivalTime("");
                            bus_stops_list = bus_stops_list + busStops.getBusStopName() + ", ";
                            DBHelper.addStops(busStops);
                        }
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_LIST, bus_stops_list);
                        preferencesManager.setStringValue(AppConstant.PREF_TOTAL_BUS_STOPS, String.valueOf(dataSnapshot.getChildrenCount()));
                        displayBusStopList(bus_stops_list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }

                });
            }
        },2000);

    }

}
