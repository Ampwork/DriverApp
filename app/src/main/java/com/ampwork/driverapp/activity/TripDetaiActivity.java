package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.AppUtility;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.model.BusStops;
import com.ampwork.driverapp.model.LiveBusDetail;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TripDetaiActivity extends AppCompatActivity {

    private static final String TAG = "TripDetaiActivity";
    private TextInputLayout routeNameEdtLayout, busNameEdtLayout, busNumberEdtLayout, tripTypeAutoComTvLayout, departTimeAutoComTvLayout;
    private EditText routeNameEdt, busNameEdt, busNumberEdt;
    private AutoCompleteTextView tripTypeAutoComTv, departTimeAutoComTv;
    private TextView totalTripsTv, tripDistanceTv, mileageTv, busReadingTv;
    private Button startBtn, cancelBtn;
    private ImageView backImageView;

    ArrayAdapter<String> tripTypeAdapter;
    ArrayAdapter<String> departTimeAdapter;
    DatabaseReference databaseReference;
    PreferencesManager preferencesManager;
    Boolean isRouteUpdated;

    String[] trip_type_array = new String[]{AppConstant.PREF_STR_PICKUP, AppConstant.PREF_STR_DROP};
    String[] trip_time_array;
    String institute_key, bus_number, initial_odometer, final_odometer;
    String tripType, tripTime;
    ArrayList<BusStops> busStopsArrayList = new ArrayList<BusStops>();

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferencesManager = new PreferencesManager(TripDetaiActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(TripDetaiActivity.this);

        initViews();


        tripTypeAutoComTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    tripTypeAutoComTvLayout.setError(null);
                    tripTypeAutoComTvLayout.setErrorEnabled(false);
                }

            }
        });

        departTimeAutoComTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    departTimeAutoComTvLayout.setError(null);
                    departTimeAutoComTvLayout.setErrorEnabled(false);
                }
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tripType = tripTypeAutoComTv.getText().toString();
                tripTime = departTimeAutoComTv.getText().toString();

                if (tripType.isEmpty()) {
                    tripTypeAutoComTvLayout.setError(getResources().getString(R.string.triptype_error_text));
                } else if (tripTime.isEmpty()) {
                    departTimeAutoComTvLayout.setError(getResources().getString(R.string.triptime_error_text));
                } else {
                    if (tripType.equalsIgnoreCase(trip_type_array[0])) {
                        // if type is pickup set direction to 1.
                        preferencesManager.setStringValue(AppConstant.PREF_DRIVING_DIRECTION, "1");
                    } else {
                        // if type is drop set direction to -1.
                        preferencesManager.setStringValue(AppConstant.PREF_DRIVING_DIRECTION, "-1");
                    }
                    if (isRouteUpdated) {
                        getBusStopsList();
                    } else {
                        DBHelper.init(TripDetaiActivity.this);
                        busStopsArrayList = DBHelper.getAllBusStops();
                        String route_id = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
                        if (busStopsArrayList.size() > 0 && busStopsArrayList.get(0).getRouteId().equalsIgnoreCase(route_id)) {
                            validate();
                        } else {
                            getBusStopsList();
                        }
                    }

                }

            }
        });


        //getTripDetail();
        LiveBusDetail liveBusDetail = getIntent().getParcelableExtra("input_data");
        isRouteUpdated = getIntent().getBooleanExtra("input_route", false);
        bindData(liveBusDetail);


    }


    private void initViews() {

        routeNameEdtLayout = findViewById(R.id.routeNameEdtLayout);
        busNameEdtLayout = findViewById(R.id.busNameEdtLayout);
        busNumberEdtLayout = findViewById(R.id.busNumberEdtLayout);
        tripTypeAutoComTvLayout = findViewById(R.id.tripTypeAutoComTvLayout);
        departTimeAutoComTvLayout = findViewById(R.id.departTimeAutoComTvLayout);

        routeNameEdt = findViewById(R.id.routeNameEdt);
        busNameEdt = findViewById(R.id.busNameEdt);
        busNumberEdt = findViewById(R.id.busNumberEdt);

        tripTypeAutoComTv = findViewById(R.id.tripTypeAutoComTv);
        departTimeAutoComTv = findViewById(R.id.departTimeAutoComTv);

        startBtn = findViewById(R.id.startBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        backImageView = findViewById(R.id.backImageView);

        totalTripsTv = findViewById(R.id.totalTripsTv);
        tripDistanceTv = findViewById(R.id.tripDistanceTv);
        mileageTv = findViewById(R.id.mileageTv);
        busReadingTv = findViewById(R.id.busReadingTv);

        tripTypeAdapter = new ArrayAdapter<>(TripDetaiActivity.this, R.layout.dropdown_menu_popup_item, trip_type_array);
        tripTypeAutoComTv.setAdapter(tripTypeAdapter);


    }

    private void validate() {

        if (tripType.equalsIgnoreCase(AppConstant.PREF_STR_PICKUP)) {
            if (tripTime.equalsIgnoreCase("Now")) {
                checkBusLocation(busStopsArrayList.get(busStopsArrayList.size() - 1));
                enableTrack();
            } else {
                int diff = Integer.parseInt(AppUtility.getTimeDifferenceMinutes(tripTime));
                if (diff <= 0) {
                    callNextScreen();
                } else {
                    Toast.makeText(TripDetaiActivity.this, "Sorry .. you are late for the trip.", Toast.LENGTH_LONG).show();
                }
            }

        } else if (tripType.equalsIgnoreCase(AppConstant.PREF_STR_DROP)) {
            enableTrack();
            if (tripTime.equalsIgnoreCase("Now")) {
                checkBusLocation(busStopsArrayList.get(0));
            } else {
                callNextScreen();
            }


        }
    }

    private void enableTrack() {
        preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, true);
        preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, true);
        //Since the bus will start from start point , add start point order,
        preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_COVERED, ",0");

    }

    private void checkBusLocation(final BusStops busStops) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //BusStops busStops = busStopsArrayList.get(0);
                    double distance = AppUtility.meterDistanceBetweenPoints((float) location.getLatitude(), (float) location.getLongitude(), Float.valueOf(busStops.getLatitude()), Float.valueOf(busStops.getLongitude()));

                    if (distance <= 100.00) {
                        callNextScreen();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TripDetaiActivity.this, "Please Go to trip starting point then start the trip.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }
        });

    }

    private void callNextScreen() {

        // start btn is clicked . update the pref. data
        preferencesManager.setBooleanValue(AppConstant.PREF_BTN_START, true);
        preferencesManager.setFloatValue(AppConstant.PREF_BUS_SPEED, 0.0f);
        preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_COVERED, "");
        preferencesManager.setFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED, 0.0f);
        preferencesManager.setStringValue(AppConstant.PREF_BUS_LAST_LOCATION, "");


        preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, "");
        preferencesManager.setStringValue(AppConstant.PREF_BUS_FULL_PATH, "");
        preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, false);
        preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, false);
        preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, false);

        preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, false);

        String depart_time = AppUtility.getCurrentDateTime();
        preferencesManager.setStringValue(AppConstant.PREF_BUS_DEPART_TIME, depart_time);

        if (tripTime.equalsIgnoreCase("Now")) {
            // Since the depart time contains both date and time. split and extract only time.
            String[] array = depart_time.split(" ");
            String trip_depart_time = array[1];
            preferencesManager.setStringValue(AppConstant.PREF_SELECTED_TRIP_TIME, trip_depart_time);
        } else {
            preferencesManager.setStringValue(AppConstant.PREF_SELECTED_TRIP_TIME, tripTime);
        }

        Intent busTrackIntent = new Intent(TripDetaiActivity.this, BusStatusActivity.class);
        busTrackIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        busTrackIntent.putExtra("bus_stop_list", busStopsArrayList);
        startActivity(busTrackIntent);

    }

    private void getBusStopsList() {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(TripDetaiActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String route_key = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("busstops_tb" + "/" + institute_key + "/" + route_key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                busStopsArrayList.clear();
                // clear the table
                DBHelper.init(TripDetaiActivity.this);
                DBHelper.deleteGeofenceShopsTable();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BusStops busStops = snapshot.getValue(BusStops.class);
                    busStops.setArrivalTime("");
                    busStopsArrayList.add(busStops);
                    DBHelper.addStops(busStops);
                }
                preferencesManager.setStringValue(AppConstant.PREF_TOTAL_BUS_STOPS, String.valueOf(busStopsArrayList.size()));
                validate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.show();
            }
        });
    }

    private void getBusLogDetail() {
        databaseReference = FirebaseDatabase.getInstance().getReference("bus_logs_tb").child(institute_key);
        databaseReference.orderByChild(AppConstant.PREF_BUS_NUMBER).equalTo(bus_number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float total_distance = (float) 0.0;
                HashMap<String, List<HashMap<String, BusLog>>> result = (HashMap<String, List<HashMap<String, BusLog>>>) dataSnapshot.getValue();
                if (result != null) {
                    Set<String> keyset = result.keySet();
                    String key = null;
                    for (String keyName : keyset) {
                        key = keyName;
                        HashMap<String, Object> logObject = (HashMap<String, Object>) result.get(key);
                        Float distance = Float.parseFloat(logObject.get("tripDistance").toString());
                        total_distance = total_distance + distance;
                    }

                    preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIP_DISTANCE, String.valueOf(total_distance));
                    preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIPS, String.valueOf(result.size()));
                    totalTripsTv.setText(String.valueOf(result.size()));
                    tripDistanceTv.setText(String.valueOf(total_distance));
                } else {
                    totalTripsTv.setText("00");
                    tripDistanceTv.setText("00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                totalTripsTv.setText("00");
                tripDistanceTv.setText("00");
            }
        });
    }

    private void getFuelLIstData() {
        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("fuel_tb");

        databaseReference.child(institute_key).orderByChild(AppConstant.PREF_BUS_NUMBER).equalTo(bus_number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total_fuel = 0;
                int mileage = 00;
                int i = 0;
                if (dataSnapshot.getChildrenCount() > 0) {

                    HashMap<String, List<HashMap<String, BusLog>>> result = (HashMap<String, List<HashMap<String, BusLog>>>) dataSnapshot.getValue();

                    if (result != null) {
                        //  Map<String, Fuel> object = result.get(i);
                        Set<String> keyset = result.keySet();
                        String key = null;
                        for (String keyName : keyset) {
                            key = keyName;
                            Map<String, Object> fuelData = (Map<String, Object>) result.get(key);

                            int fuel_qty = Integer.parseInt(fuelData.get("fuel").toString());
                            total_fuel = total_fuel + fuel_qty;

                            if (i == result.size() - 1) {
                                if (fuelData != null) {
                                    final_odometer = fuelData.get("busReading").toString();
                                    preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, final_odometer);

                                }
                            }
                            // increment i
                            i = i + 1;
                        }


                        busReadingTv.setText(final_odometer);
                        if (mileage > 0) {
                            preferencesManager.setStringValue(AppConstant.PREF_MILEAGE, String.valueOf(mileage));
                            mileageTv.setText(String.valueOf(mileage));
                        } else {
                            preferencesManager.setStringValue(AppConstant.PREF_MILEAGE, "00");
                            mileageTv.setText("00");
                        }

                    }


                } else {

                    preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, initial_odometer);
                    preferencesManager.setStringValue(AppConstant.PREF_MILEAGE, "00");
                    busReadingTv.setText("00");
                    mileageTv.setText("00");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage().toString());
            }
        });


    }


    private void bindData(LiveBusDetail liveBusDetail) {

        routeNameEdt.setText(liveBusDetail.getRouteName());
        busNameEdt.setText(liveBusDetail.getBusName());
        busNumberEdt.setText(liveBusDetail.getBusNumber());

        if (liveBusDetail.getRouteName().isEmpty()) {
            Toast.makeText(TripDetaiActivity.this, "Route is not allocated..", Toast.LENGTH_SHORT).show();
            startBtn.setEnabled(false);
        } else {
            startBtn.setEnabled(true);
            String depart_list = AppUtility.getNextTripTime(preferencesManager.getStringValue(AppConstant.PREF_SC_DEPART_TIME)) + ",Now";
            trip_time_array = depart_list.split(",");

            departTimeAdapter = new ArrayAdapter<>(TripDetaiActivity.this, R.layout.dropdown_menu_popup_item, trip_time_array);
            departTimeAutoComTv.setAdapter(departTimeAdapter);

        }

        institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        bus_number = preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER);
        initial_odometer = preferencesManager.getStringValue(AppConstant.PREF_INITIAL_ODOMETER);
        String total_trips = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIPS);
        String total_trip_distance = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIP_DISTANCE);
        String bus_total_reading = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE);
        String mileage = preferencesManager.getStringValue(AppConstant.PREF_MILEAGE);

        if (total_trips.length() > 0) {
            if (Integer.parseInt(total_trips) > 0) {
                totalTripsTv.setText(total_trips);
            }
            if (Double.parseDouble(total_trip_distance) > 0) {
                tripDistanceTv.setText(String.format("%.2f", Double.parseDouble(total_trip_distance)));
            }
        }/* else {
            getBusLogDetail();
        }*/

        if (!(bus_total_reading.isEmpty() && mileage.isEmpty())) {
            if (Double.parseDouble(bus_total_reading) > 0) {
                busReadingTv.setText(bus_total_reading);
            }
            if (Double.parseDouble(mileage) > 0) {
                mileageTv.setText(mileage);
            }

        } /*else {
            getFuelLIstData();
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* return super.onOptionsItemSelected(item);*/
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
