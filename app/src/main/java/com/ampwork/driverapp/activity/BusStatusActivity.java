package com.ampwork.driverapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ampwork.driverapp.MyApplication;
import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.AppUtility;
import com.ampwork.driverapp.Util.BadgeDrawerToggle;
import com.ampwork.driverapp.Util.CustomInfoWindow;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.geofencing.GeoFencing;
import com.ampwork.driverapp.markerhelper.LatLngInterpolator;
import com.ampwork.driverapp.markerhelper.MarkerAnimation;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.model.BusStops;
import com.ampwork.driverapp.model.Driver;
import com.ampwork.driverapp.model.DriverSosData;
import com.ampwork.driverapp.model.LiveBusDetail;
import com.ampwork.driverapp.receiver.ConnectivityReceiver;
import com.ampwork.driverapp.service.LiveTrackingService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BusStatusActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private TextView navHeaderTitleTv, navHeaderSubTitleTv, navHeaderSubTitle2Tv, busDistanceTv, speedTV, nextStopTv, timerTV;
    private Button startBtn, stopBtn;
    private LinearLayout profileDataLayout, busDistanceLayout;
    ExtendedFloatingActionButton fab_bell, fab_fuel;
    NavigationView navigationView;
    BadgeDrawerToggle badgeDrawerToggle;
    TextView notificationActionCount;

    PreferencesManager preferencesManager;
    ArrayList<BusStops> busStopsArrayList = new ArrayList<BusStops>();

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Marker busMarker;

    FusedLocationProviderClient fusedLocationProviderClient;
    String next_stop_order, start_point, end_point, next_stop, drive_direction;

    private LinearLayout notificationLayout,bottomLinkLayout;
    private TextView notificationTv,privacyTv,termsTv;

    private static final String TAG = "BusStatus";


    LiveTrackingService liveTrackingService;
    Intent liveTrackingIntent;
    Boolean isRouteDrawn = false;

    GeoFencing mGeoFencing;
    BroadcastReceiver mBroadcastReceiver;
    boolean isRouteUpdated = false, isBusArrivalTimeUpdated = false;
    Toolbar toolbar;

    ValueEventListener valueEventListener;
    DatabaseReference checkNearByStudentsRef;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long startTimerTime = AppUtility.covertDateToMili(preferencesManager.getStringValue(AppConstant.PREF_BUS_DEPART_TIME));
            long millis = System.currentTimeMillis() - startTimerTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTV.setText(String.format("%d:%02d", minutes, seconds) + " min");

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        preferencesManager = new PreferencesManager(this);

        busStopsArrayList = getIntent().getParcelableArrayListExtra("bus_stop_list");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        liveTrackingIntent = new Intent(BusStatusActivity.this, LiveTrackingService.class);
        mGeoFencing = new GeoFencing(BusStatusActivity.this);


        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(AppConstant.STR_LOCATION_FILTER)) {
                    Double bus_lattitude = intent.getDoubleExtra("bus_lattitude", 0.0);
                    Double bus_longitude = intent.getDoubleExtra("bus_longitude", 0.0);

                    if (bus_lattitude != 0.0 && bus_longitude != 0.0) {
                        Location location = new Location(LocationManager.GPS_PROVIDER);
                        location.setLatitude(bus_lattitude);
                        location.setLongitude(bus_longitude);
                        updateScreenUI(location, null);

                    } else {
                        updateScreenUI(null, null);
                    }
                } else if (intent.getAction().equalsIgnoreCase(AppConstant.STR_GEOFENCE_FILTER)) {
                    BusStops nextBusStop = intent.getParcelableExtra("next_stop");
                    if (nextBusStop != null) {
                        updateScreenUI(null, nextBusStop.getBusStopName());
                    } else {
                        updateScreenUI(null, null);
                    }
                } else if (intent.getAction().equalsIgnoreCase(AppConstant.STR_NOTIFICATION_FILTER)) {
                    preferencesManager.setBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED, true);
                    showNotificationBadge();

                }
            }
        };

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        initView();

        profileDataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(BusStatusActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if (ConnectivityReceiver.isConnected()) {

                LiveBusDetail liveBusDetail = new LiveBusDetail();
                liveBusDetail.setRouteName(preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME));
                liveBusDetail.setBusName(preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME));
                liveBusDetail.setBusNumber(preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER));

                startTrip(liveBusDetail);
                // }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (preferencesManager.getBooleanValue(AppConstant.PREF_TRIP_COMPLETED)) {
                    fab_bell.shrink(true);
                    stopTrip();
                } else if (preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)) {
                    final AlertDialog dialog = new MaterialAlertDialogBuilder(BusStatusActivity.this, R.style.AlertDialogTheme)
                            .setTitle("You want to stop the trip?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    fab_bell.shrink(true);
                                    fab_bell.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_stop_default));

                                    stopTrip();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }


            }
        });

        fab_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fuelIntent = new Intent(BusStatusActivity.this, FuelEntryActivity.class);
                startActivity(fuelIntent);
            }
        });

        notificationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    hideNotificationBadge();
                    Intent notificationIntent = new Intent(BusStatusActivity.this, NotificationActivity.class);
                    startActivity(notificationIntent);
                }

            }
        });


        privacyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtility.callBrowserIntent(BusStatusActivity.this,"https://ampwork.com/");
            }
        });


        termsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtility.callBrowserIntent(BusStatusActivity.this,"https://ampwork.com/");
            }
        });


    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        badgeDrawerToggle = new BadgeDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(badgeDrawerToggle);
        badgeDrawerToggle.setBadgeEnabled(false);
        badgeDrawerToggle.syncState();
        navigationView.setItemIconTintList(null);
       /* ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        notificationActionCount = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_notification));
        notificationActionCount.setGravity(Gravity.CENTER_VERTICAL);
        notificationActionCount.setTypeface(null, Typeface.BOLD);
        notificationActionCount.setTextColor(getResources().getColor(R.color.error_color));
        notificationActionCount.setText("");

        if (preferencesManager.getBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED)) {
            showNotificationBadge();
        }


        navigationView.setNavigationItemSelectedListener(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // NAvigation Header UI
        View headerView = navigationView.getHeaderView(0);
        ImageView imageView = headerView.findViewById(R.id.imageView);
        profileDataLayout = headerView.findViewById(R.id.profileDataLayout);
        navHeaderTitleTv = headerView.findViewById(R.id.navHeaderTitleTv);
        navHeaderSubTitleTv = headerView.findViewById(R.id.navHeaderSubTitleTv);
        navHeaderSubTitle2Tv = headerView.findViewById(R.id.navHeaderSubTitle2Tv);

        View bottomView = navigationView.getChildAt(1);
        notificationLayout = bottomView.findViewById(R.id.notificationLayout);
        notificationTv = bottomView.findViewById(R.id.notificationTv);
        privacyTv = bottomView.findViewById(R.id.privacyTv);
        termsTv = bottomView.findViewById(R.id.termsTv);

        String recent_notification = preferencesManager.getStringValue(AppConstant.PREF_RECENT_NOTIFICATION);
        if(recent_notification!=null && !recent_notification.isEmpty()){
            notificationLayout.setVisibility(View.VISIBLE);
            notificationTv.setText(recent_notification);
        }


        fab_fuel = findViewById(R.id.fab_fuel);
        fab_bell = (ExtendedFloatingActionButton) findViewById(R.id.fab_bell);

        busDistanceTv = findViewById(R.id.busDistanceTv);
        nextStopTv = findViewById(R.id.nextStopTv);
        speedTV = findViewById(R.id.speedTV);
        timerTV = findViewById(R.id.timerTV);


        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);

        busDistanceLayout = findViewById(R.id.busDistanceLayout);

        // Intialize Data
        navHeaderTitleTv.setText(":  " + preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME));
        navHeaderSubTitleTv.setText(":  " + preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE));
        navHeaderSubTitle2Tv.setText(":  " + preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ConnectivityReceiver.isConnected()) {
            if (busStopsArrayList != null) {
                showBusStatus();
            } else {
                getTripDetail();
            }

        } else {
            showBusStatus();
        }

    }

    private void bindData() {

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
        mMap.setMyLocationEnabled(false);

        if (busStopsArrayList != null && busStopsArrayList.size() > 0) {

          /*  // Register Geofence
            mGeoFencing.updateGeofencesList(busStopsArrayList);
            mGeoFencing.registerAllGeofences();*/

            drive_direction = preferencesManager.getStringValue(AppConstant.PREF_DRIVING_DIRECTION);

            if (!preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)) {
                String start_point_order = "0", end_point_order = "0";
                if (drive_direction.equalsIgnoreCase("-1")) {

                    start_point_order = "0";
                    start_point = busStopsArrayList.get(0).busStopName;
                    end_point_order = String.valueOf(busStopsArrayList.size() - 1);
                    end_point = busStopsArrayList.get(busStopsArrayList.size() - 1).busStopName;
                    next_stop_order = "0";
                    next_stop = busStopsArrayList.get(0).getBusStopName();

                    toolbar.setTitle(getResources().getString(R.string.app_name) + " - " + AppConstant.PREF_STR_DROP);

                } else {

                    start_point_order = String.valueOf(busStopsArrayList.size() - 1);
                    start_point = busStopsArrayList.get(busStopsArrayList.size() - 1).busStopName;
                    end_point_order = "0";
                    end_point = busStopsArrayList.get(0).busStopName;
                    next_stop_order = String.valueOf(busStopsArrayList.size() - 1);
                    next_stop = busStopsArrayList.get(busStopsArrayList.size() - 1).getBusStopName();


                    toolbar.setTitle(getResources().getString(R.string.app_name) + " - " + AppConstant.PREF_STR_PICKUP);
                }

                preferencesManager.setStringValue(AppConstant.PREF_SOURCE_POINT_ID, "0");
                preferencesManager.setStringValue(AppConstant.PREF_START_POINT_ID, start_point_order);
                preferencesManager.setStringValue(AppConstant.PREF_START_POINT, start_point);
                preferencesManager.setStringValue(AppConstant.PREF_END_POINT_ID, end_point_order);
                preferencesManager.setStringValue(AppConstant.PREF_END_POINT, end_point);

                preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP, next_stop);
                preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP_ORDER, next_stop_order);

                preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, false);
                preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, false);


            }

            next_stop = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP);
            next_stop_order = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP_ORDER);


            updateScreenUI(null, null);

            //addBusstopsMarker();

            checkLocationPermission();

        } else {
            DBHelper.init(BusStatusActivity.this);
            busStopsArrayList = DBHelper.getAllBusStops();
            String route_id = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
            preferencesManager.setStringValue(AppConstant.PREF_TOTAL_BUS_STOPS, String.valueOf(busStopsArrayList.size()));
            if (busStopsArrayList != null && busStopsArrayList.size() > 0 && busStopsArrayList.get(0).getRouteId().equalsIgnoreCase(route_id)) {
                bindData();
            } else {
                getBusStopsList();
            }
        }

    }

    private void showBusStatus() {

        invalidateOptionsMenu();

        Boolean is_drive_started = preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING);
        Boolean start_btn_activated = preferencesManager.getBooleanValue(AppConstant.PREF_BTN_START);


        if (start_btn_activated) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            startBtn.setEnabled(false);
            timerTV.setVisibility(View.VISIBLE);
            timerHandler.postDelayed(timerRunnable, 0);
            bindData();
        } else {
            if (is_drive_started) {
                startBtn.setEnabled(false);
                Toast.makeText(BusStatusActivity.this, "Click StopTrip Button to reset the trip..", Toast.LENGTH_LONG).show();
            } else {
                toolbar.setTitle(getResources().getString(R.string.app_name));
                checkLocationPermission();
            }

        }
    }

    private void checkLocationPermission() {

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        Dexter.withActivity(BusStatusActivity.this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {


                        if (!preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)) {
                            startTrackerService();

                        } else {
                            // move the marker to current bus location
                            String bus_location = preferencesManager.getStringValue(AppConstant.PREF_BUS_LOCATION);
                            LatLng latLng = AppUtility.strToLatlng(bus_location);
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(latLng.latitude);
                            location.setLongitude(latLng.longitude);
                            updateScreenUI(location, null);

                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).onSameThread().check();

    }

    private void getTripDetail() {

        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String driver_id = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bus_tracking_tb").child(institute_key);
        databaseReference.orderByChild(AppConstant.PREF_DRIVER_ID).equalTo(driver_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, LiveBusDetail> result = (HashMap<String, LiveBusDetail>) dataSnapshot.getValue();
                if (result != null) {
                    Set<String> keyset = result.keySet();
                    String busKey = null;
                    for (String keyName : keyset) {
                        busKey = keyName;
                    }
                    Object driverObject = result.get(busKey);
                    Map<String, Object> object = (Map<String, Object>) driverObject;

                    if (object != null) {

                        preferencesManager.setStringValue(AppConstant.PREF_BUS_TRACKING_KEY, busKey);
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_NAME, object.get(AppConstant.PREF_BUS_NAME).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_NUMBER, object.get(AppConstant.PREF_BUS_NUMBER).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_DRIVING_DIRECTION, object.get(AppConstant.PREF_DRIVING_DIRECTION).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_ROUTE_NAME, object.get(AppConstant.PREF_ROUTE_NAME).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_INITIAL_ODOMETER, object.get(AppConstant.PREF_INITIAL_ODOMETER).toString());


                        preferencesManager.setStringValue(AppConstant.PREF_START_POINT, object.get(AppConstant.PREF_START_POINT).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_END_POINT, object.get(AppConstant.PREF_END_POINT).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_SC_DEPART_TIME, object.get(AppConstant.PREF_SC_DEPART_TIME).toString());
                        preferencesManager.setBooleanValue(AppConstant.PREF_IS_DRIVING, (Boolean) object.get(AppConstant.PREF_IS_DRIVING));
                        preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP, object.get(AppConstant.PREF_NEXT_STOP).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_NEXT_STOP_ORDER, object.get(AppConstant.PREF_NEXT_STOP_ORDER).toString());

                        preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIPS, object.get(AppConstant.PREF_STR_TOTAL_TRIPS).toString());
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIP_DISTANCE, object.get(AppConstant.PREF_STR_TRIP_DISTANCE).toString());
                        //preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, object.get(AppConstant.PREF_STR_CURRENT_ODOMETER).toString());

                        String total_distance = object.get(AppConstant.PREF_STR_TOTAL_DISTANCE).toString();
                        String total_fuel = object.get(AppConstant.PREF_STR_TOTAL_FUEL).toString();
                        String mileage = object.get(AppConstant.PREF_MILEAGE).toString();
                        //String.valueOf(Math.round(((Float.parseFloat(total_distance) / Float.parseFloat(total_fuel))*100.0)/100.0));

                        preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE, total_distance);
                        preferencesManager.setStringValue(AppConstant.PREF_TOTAL_FUEL, total_fuel);
                        preferencesManager.setStringValue(AppConstant.PREF_MILEAGE, mileage);


                        preferencesManager.setBooleanValue(AppConstant.PREF_DRIVER_SOS, (Boolean) object.get(AppConstant.PREF_DRIVER_SOS));

                        String route_id = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
                        String assigned_route_id = object.get(AppConstant.PREF_ROUTE_ID).toString();
                        if (!route_id.isEmpty() && !route_id.equalsIgnoreCase(assigned_route_id)) {
                            isRouteUpdated = true;
                        } else if (route_id.isEmpty()) {
                            isRouteUpdated = true;
                        } else {
                            isRouteUpdated = false;
                        }

                        preferencesManager.setStringValue(AppConstant.PREF_ROUTE_ID, assigned_route_id);

                        showBusStatus();

                    }

                } else {
                    Toast.makeText(BusStatusActivity.this, "Route is not allocated..", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNotificationBadge() {
        badgeDrawerToggle.setBadgeEnabled(true);
        notificationActionCount.setText("1+");
    }

    private void hideNotificationBadge() {
        preferencesManager.setBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED, false);
        badgeDrawerToggle.setBadgeEnabled(false);
        notificationActionCount.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);


        // Register the filter for listening broadcast.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstant.STR_LOCATION_FILTER);
        intentFilter.addAction(AppConstant.STR_GEOFENCE_FILTER);
        intentFilter.addAction(AppConstant.STR_NOTIFICATION_FILTER);

        LocalBroadcastManager.getInstance(BusStatusActivity.this).registerReceiver(mBroadcastReceiver, intentFilter);
        Boolean is_drive_started = preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING);
        Boolean start_btn_activated = preferencesManager.getBooleanValue(AppConstant.PREF_BTN_START);

        navigationView.getMenu().getItem(0).setChecked(true);

        if (is_drive_started && start_btn_activated) {

            mapFragment.getMapAsync(this);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            nextStopTv.setVisibility(View.VISIBLE);
            timerTV.setVisibility(View.VISIBLE);
            timerHandler.postDelayed(timerRunnable, 0);
            checkNearByStudentsCount();
        } else {
            if (ConnectivityReceiver.isConnected()) {
                callUpdateOfflineLogs();
            } else {
                if (mMap == null) {
                    mapFragment.getMapAsync(BusStatusActivity.this);
                }
            }
        }


    }

    private void callUpdateOfflineLogs() {
        DBHelper.init(this);
        ArrayList<BusLog> busLogArrayList = DBHelper.getAllBuslogs();
        if (busLogArrayList.size() > 0) {
            updateDraftLogs(busLogArrayList.get(0));
        } else {
            if (mMap == null) {
                mapFragment.getMapAsync(BusStatusActivity.this);
            }
        }
    }

    private void updateDraftLogs(final BusLog logDetail) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(BusStatusActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating offline logs... ");
        progressDialog.show();

        // reset Bustrack table and update driver detail.
        Driver driver = getDriverData();
        LiveBusDetail liveBusDetail = getLiveBusObject();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
        String driver_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_KEY);


        Map<String, Object> childUpdates = new HashMap<>();
        if (Double.parseDouble(logDetail.getTripDistance()) > 0) {
            // update the logs.
            String log_key = databaseReference.push().getKey();
            childUpdates.put(AppConstant.PREF_STR_BUS_LOGS_TB + "/" + institute_key + "/" + bus_key + "/" + log_key, logDetail);
            childUpdates.put(AppConstant.PREF_STR_DRIVERS_TB + "/" + institute_key + "/" + driver_key, driver);
        }
        childUpdates.put(AppConstant.PREF_STR_BUSTRACKING_TB + "/" + institute_key + "/" + bus_key, liveBusDetail);


       /* Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(AppConstant.PREF_STR_DRIVERS_TB + "/" + institute_key + "/" + driver_key, driver);
        childUpdates.put(AppConstant.PREF_STR_BUSTRACKING_TB + "/" + institute_key + "/" + bus_key, liveBusDetail);
*/

        databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                progressDialog.dismiss();
                DBHelper.deleteBuslogsTable(logDetail.getArrivedTime());
                if (mMap == null) {
                    mapFragment.getMapAsync(BusStatusActivity.this);
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRouteDrawn = false;
        timerHandler.removeCallbacks(timerRunnable);
        LocalBroadcastManager.getInstance(
                BusStatusActivity.this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        Boolean isSosPressed = preferencesManager.getBooleanValue(AppConstant.PREF_DRIVER_SOS);
        MenuItem item = menu.getItem(0);
        SpannableString s = new SpannableString(getResources().getString(R.string.action_sos));
        if (isSosPressed) {
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error_color)), 0, s.length(), 0);
        } else {
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.secondaryColor)), 0, s.length(), 0);
        }

        item.setTitle(s);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sos) {
            enableSOS();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableSOS() {

        Boolean is_driving = preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING);
        Boolean is_sos_pressed = preferencesManager.getBooleanValue(AppConstant.PREF_DRIVER_SOS);
        final String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        final String bus_tracking_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
        if (is_driving && !is_sos_pressed) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("bus_tracking_tb");
            databaseReference.child(institute_key).child(bus_tracking_key).child(AppConstant.PREF_DRIVER_SOS).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    preferencesManager.setBooleanValue(AppConstant.PREF_DRIVER_SOS, true);
                    invalidateOptionsMenu();
                    Toast.makeText(BusStatusActivity.this, "SOS is successfull..", Toast.LENGTH_SHORT).show();
                    addSosToLog(institute_key, bus_tracking_key);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BusStatusActivity.this, "SOS failed..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addSosToLog(String institute_key, String bus_tracking_key) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_SOS_TB);
        DriverSosData driverSosData = new DriverSosData();
        driverSosData.setBloodgroup(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_BG));
        driverSosData.setDriverId(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID));
        driverSosData.setDriverKey(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_KEY));
        driverSosData.setDriverName(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME));
        driverSosData.setDriverDL(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_DL));
        driverSosData.setDriverPhone(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_PHONE));
        driverSosData.setEmergency(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_EMERGENCY_PHONE));
        driverSosData.setBusNumber(preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME));
        driverSosData.setBusNumber(preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER));
        driverSosData.setRouteId(preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID));
        driverSosData.setRouteName(preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME));
        driverSosData.setLocation(preferencesManager.getStringValue(AppConstant.PREF_BUS_LOCATION));
        driverSosData.setDateTime(AppUtility.getCurrentDateTime());

        String current_date = AppUtility.getCurrentDate();
        String key = reference.push().getKey();
        reference.child(institute_key).child(bus_tracking_key).child(current_date).child("drivers").child(key).setValue(driverSosData);

    }

    private void startTrip(LiveBusDetail liveBusDetail) {
        Intent tripDetailIntent = new Intent(BusStatusActivity.this, TripDetaiActivity.class);
        tripDetailIntent.putExtra("input_data", liveBusDetail);
        tripDetailIntent.putExtra("input_route", isRouteUpdated);
        startActivity(tripDetailIntent);
    }

    private void stopTrip() {

        if (preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)) {

            stopService(liveTrackingIntent);


            //   unbindService(mConnection);
            // mGeoFencing.unRegisterAllGeofences();

            timerHandler.removeCallbacks(timerRunnable);
            timerTV.setVisibility(View.GONE);

            String trip_finished_time = AppUtility.getCurrentDateTime();
            final BusLog logDetail = getLogData(trip_finished_time);
            String stopsCovered = preferencesManager.getStringValue(AppConstant.PREF_BUS_STOPS_COVERED);


            if (ConnectivityReceiver.isConnected()) {


                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(BusStatusActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Updating logs...");
                progressDialog.show();


                // insert log and reset Bustrack table.
                Driver driver = getDriverData();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
                String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
                String driver_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_KEY);


                Map<String, Object> childUpdates = new HashMap<>();
                if (stopsCovered.length() > 0) {
                    // update the logs.
                    String log_key = databaseReference.push().getKey();
                    childUpdates.put(AppConstant.PREF_STR_BUS_LOGS_TB + "/" + institute_key + "/" + bus_key + "/" + log_key, logDetail);
                    childUpdates.put(AppConstant.PREF_STR_DRIVERS_TB + "/" + institute_key + "/" + driver_key, driver);
                }
                childUpdates.put(AppConstant.PREF_STR_BUSTRACKING_TB + "/" + institute_key + "/" + bus_key, getLiveBusObject());


                databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        progressDialog.dismiss();
                        preferencesManager.setBooleanValue(AppConstant.PREF_IS_DRIVING, false);
                        preferencesManager.setBooleanValue(AppConstant.PREF_BTN_START, false);

                        preferencesManager.setBooleanValue(AppConstant.PREF_DRIVER_SOS, false);

                        preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, "");
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_FULL_PATH, "");
                        preferencesManager.setFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED, 0.0f);
                        preferencesManager.setFloatValue(AppConstant.PREF_BUS_SPEED, 0.0f);
                        preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, false);
                        preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, false);
                        preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, false);
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_COVERED, "");

                        preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, false);
                        preferencesManager.setStringValue(AppConstant.PREF_SELECTED_TRIP_TIME, "");

                        toolbar.setTitle(getResources().getString(R.string.app_name));
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                        showLogDetail(logDetail);
                    }
                });

            } else {
                //store bus log offline then update.
                if (stopsCovered.length() > 0) {
                    DBHelper.init(this);
                    DBHelper.addBusLog(logDetail);
                }

                preferencesManager.setBooleanValue(AppConstant.PREF_IS_DRIVING, false);
                preferencesManager.setBooleanValue(AppConstant.PREF_BTN_START, false);

                preferencesManager.setBooleanValue(AppConstant.PREF_DRIVER_SOS, false);

                preferencesManager.setStringValue(AppConstant.PREF_BUS_PATH, "");
                preferencesManager.setStringValue(AppConstant.PREF_BUS_FULL_PATH, "");
                preferencesManager.setFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED, 0.0f);
                preferencesManager.setFloatValue(AppConstant.PREF_BUS_SPEED, 0.0f);
                preferencesManager.setBooleanValue(AppConstant.PREF_TRACK_ENABLED, false);
                preferencesManager.setBooleanValue(AppConstant.PREF_DEST_REACHED, false);
                preferencesManager.setBooleanValue(AppConstant.PREF_TRIP_COMPLETED, false);
                preferencesManager.setStringValue(AppConstant.PREF_BUS_STOPS_COVERED, "");

                preferencesManager.setBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS, false);
                preferencesManager.setStringValue(AppConstant.PREF_SELECTED_TRIP_TIME, "");

                toolbar.setTitle(getResources().getString(R.string.app_name));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                showLogDetail(logDetail);
            }

        }
    }

    private Driver getDriverData() {

        String bloodgroup = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_BG);
        String driverDL = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_DL);
        String driverId = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID);
        String driverName = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME);
        String driverPhone = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_PHONE);
        String emergency = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_EMERGENCY_PHONE);
        String gender = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_GENDER);
        String institute = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE);
        String password = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_PASSWORD);
        String fcmToken = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_FCMTOKEN);
        String totalTrips = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIPS);
        String tripDistance = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIP_DISTANCE);

        Driver driver = new Driver(bloodgroup, driverDL, driverId, driverName, driverPhone, emergency, gender, institute, password, fcmToken
                , totalTrips, tripDistance, true);

        return driver;
    }

    private void showLogDetail(BusLog logDetail) {

        View view = getLayoutInflater().inflate(R.layout.trip_status_layout, null);
        TextView routeNameTv = view.findViewById(R.id.routeNameTv);
        TextView tripTypeTv = view.findViewById(R.id.tripTypeTv);
        TextView tripDepartTv = view.findViewById(R.id.tripDepartTv);
        TextView tripArrivalTv = view.findViewById(R.id.tripArrivalTv);
        TextView tripstausTv = view.findViewById(R.id.tripstausTv);
        TextView busStopsListTv = view.findViewById(R.id.busStopsListTv);
        TextView tripDistanceTv = view.findViewById(R.id.tripDistanceTv);
        TextView tripDurationTv = view.findViewById(R.id.tripDurationTv);
        TextView stopsCoveredTv = view.findViewById(R.id.stopsCoveredTv);
        Button successBtn = view.findViewById(R.id.successBtn);

        routeNameTv.setText(": " + logDetail.getRouteName());
        if (logDetail.getDirection().equalsIgnoreCase("1")) {
            tripTypeTv.setText(": " + AppConstant.PREF_STR_PICKUP);
        } else {
            tripTypeTv.setText(": " + AppConstant.PREF_STR_DROP);
        }
        tripDepartTv.setText(": " + logDetail.getDepartTime());
        tripArrivalTv.setText(": " + logDetail.getArrivedTime());
        busStopsListTv.setText(": " + logDetail.getBusStopsCovered());
        if (logDetail.getTripCompleted().equalsIgnoreCase("1")) {
            tripstausTv.setText(": " + "Yes");
            stopsCoveredTv.setText(": Yes");
        } else if (logDetail.getTripCompleted().equalsIgnoreCase("0")) {
            tripstausTv.setText(": " + "Yes");
            stopsCoveredTv.setText(": No");
        } else if (logDetail.getTripCompleted().equalsIgnoreCase("-1")) {
            tripstausTv.setText(": " + "No");
            stopsCoveredTv.setText(": No");
        }

        tripDistanceTv.setText(": " + logDetail.getTripDistance() + " kms");
        tripDurationTv.setText(": " + logDetail.getTripDuration() + " minutes");


        final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setView(view)
                .setCancelable(false)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Refresh Activity
                finish();
                startActivity(getIntent());
            }
        });
    }

    private HashMap<String, Object> getResetBusTrackObject() {

        if (!(busStopsArrayList != null && busStopsArrayList.size() >= 0)) {
            DBHelper.init(BusStatusActivity.this);
            busStopsArrayList = DBHelper.getAllBusStops();
        }

        String busLocation = busStopsArrayList.get(0).getLatitude() + "," + busStopsArrayList.get(0).getLongitude();
        String busPath = "";
        String busName = preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME);
        String busNumber = preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER);
        String odometerReading = preferencesManager.getStringValue(AppConstant.PREF_INITIAL_ODOMETER);
        String departureTime = preferencesManager.getStringValue(AppConstant.PREF_SC_DEPART_TIME);
        String driverId = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID);
        String driverName = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME);
        String routeId = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
        String routeName = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME);
        String institute = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE);
        Boolean destinationReached = false;
        String direction = "";
        Boolean driving = false;
        String endPoint = busStopsArrayList.get(busStopsArrayList.size() - 1).busStopName;
        String nextStopName = "";
        String nextStopOrderId = "";
        String startPoint = busStopsArrayList.get(0).busStopName;
        Boolean trackEnabled = false;
        Boolean tripCompleted = false;
        String sos = "";
        Boolean driverSos = false;
        String bellCounts = "0";


        HashMap<String, Object> objectHashMap = LiveBusDetail.getResetBusTrackObj(busLocation, busPath, destinationReached, driving, endPoint
                , nextStopName, nextStopOrderId, startPoint, trackEnabled, tripCompleted, bellCounts);

        return objectHashMap;


    }

    private LiveBusDetail getLiveBusObject() {


        if (!(busStopsArrayList != null && busStopsArrayList.size() >= 0)) {
            DBHelper.init(BusStatusActivity.this);
            busStopsArrayList = DBHelper.getAllBusStops();
        }

        String busLocation = busStopsArrayList.get(0).getLatitude() + "," + busStopsArrayList.get(0).getLongitude();
        String busPath = "";
        String busName = preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME);
        String busNumber = preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER);
        String odometerReading = preferencesManager.getStringValue(AppConstant.PREF_INITIAL_ODOMETER);
        String departureTime = preferencesManager.getStringValue(AppConstant.PREF_SC_DEPART_TIME);
        String driverId = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID);
        String driverName = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME);
        String routeId = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
        String routeName = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME);
        String institute = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE);
        Boolean destinationReached = false;
        String direction = "";
        Boolean driving = false;
        String endPoint = busStopsArrayList.get(busStopsArrayList.size() - 1).busStopName;
        String nextStopName = "";
        String nextStopOrderId = "";
        String startPoint = busStopsArrayList.get(0).busStopName;
        Boolean trackEnabled = false;
        Boolean tripCompleted = false;
        String sos = "";
        Boolean driverSos = false;
        String bellCounts = "0";
        // String currentOdometer = preferencesManager.getStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER);
        String totalDistance = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE);
        String totalFuel = preferencesManager.getStringValue(AppConstant.PREF_TOTAL_FUEL);
        String totalTrips = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_TRIPS);
        String tripDistance = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_TRIP_DISTANCE);
        String mileage = preferencesManager.getStringValue(AppConstant.PREF_MILEAGE);

        LiveBusDetail liveBusDetail = new LiveBusDetail(busLocation, busName, busNumber, busPath, departureTime, destinationReached, direction, driverId, driverName, driving, endPoint, institute, nextStopName, nextStopOrderId, odometerReading, routeId, routeName, startPoint, trackEnabled, tripCompleted, sos, driverSos, bellCounts,
                totalDistance, totalFuel, totalTrips, tripDistance, mileage);

        return liveBusDetail;

    }

    private BusLog getLogData(String trip_finished_time) {

        String current_date = AppUtility.getCurrentDate();

        Boolean isPermisionEnabled = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                isPermisionEnabled = false;
            }
        }

        if (isPermisionEnabled) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    String current_loc = AppUtility.locationToStr(location);
                    preferencesManager.setStringValue(AppConstant.PREF_BUS_LOCATION, current_loc);

                    String bus_full_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_FULL_PATH);
                    if (!bus_full_path.contains(current_loc)) {
                        bus_full_path = bus_full_path + ":" + current_loc;
                        preferencesManager.setStringValue(AppConstant.PREF_BUS_FULL_PATH, bus_full_path);
                    }

                    if (bus_full_path.contains(":")) {
                        String[] path = bus_full_path.split(":");
                        if (path.length > 2) {
                            /*Calculate Bus Travelled  meters*/
                            float distance = preferencesManager.getFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED);
                            //String strLastLoc = preferencesManager.getStringValue(AppConstant.PREF_BUS_LAST_LOCATION);
                            String strLastLoc = path[path.length - 2];
                            if (strLastLoc != null && strLastLoc.trim().length() > 0) {
                                Location last_location = AppUtility.strToLocation(strLastLoc);
                                distance = (distance + last_location.distanceTo(location));
                            }
                            Log.d(TAG, "Distance: " + distance);

                            if (distance > 0) {
                                preferencesManager.setFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED, distance);
                            }
                        }
                    }

                }
            });
        }

        String busName = preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME);
        String busNumber = preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER);
        String departureTime = preferencesManager.getStringValue(AppConstant.PREF_BUS_DEPART_TIME);
        String direction = preferencesManager.getStringValue(AppConstant.PREF_DRIVING_DIRECTION);
        String driverId = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID);
        String driverName = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME);
        String routeName = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME);
        String bus_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_FULL_PATH);
        double distance = preferencesManager.getFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED) / 1000.00;
        String duration = AppUtility.getTimeDifference(trip_finished_time, departureTime);


        Boolean tripCompleted = preferencesManager.getBooleanValue(AppConstant.PREF_TRIP_COMPLETED);
        String busStopsCovered = preferencesManager.getStringValue(AppConstant.PREF_BUS_STOPS_COVERED);
        String[] busStopList = busStopsCovered.split(",");
        Boolean isAllPointsCoverd = false;

        if (busStopsCovered.length() > 0) {
            if (busStopList.length > busStopsArrayList.size()) {
                isAllPointsCoverd = true;
            }
        }

        BusLog busLog = new BusLog();
        busLog.setArrivedTime(trip_finished_time);
        busLog.setDepartTime(departureTime);
        busLog.setBusName(busName);
        busLog.setBusNumber(busNumber);
        busLog.setBusPath(bus_path);
        busLog.setDate(current_date);
        busLog.setDirection(direction);
        busLog.setDriverId(driverId);
        busLog.setDriverName(driverName);
        busLog.setRouteName(routeName);
        busLog.setTripDistance(String.format("%.2f", distance));
        busLog.setTripDuration(duration);
        if (tripCompleted && isAllPointsCoverd) {
            busLog.setTripCompleted("1");
        } else if (tripCompleted) {
            busLog.setTripCompleted("0");
        } else {
            busLog.setTripCompleted("-1");
        }
        if (busStopsCovered.length() > 0) {
            busLog.setBusStopsCovered(busStopsCovered.substring(1));
        } else {
            busLog.setBusStopsCovered(busStopsCovered);
        }


        if (busLog.getTripCompleted().equalsIgnoreCase("0") || busLog.getTripCompleted().equalsIgnoreCase("1")) {


            String pref_bus_trip = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_TRIPS);
            pref_bus_trip = String.valueOf(Integer.parseInt(pref_bus_trip) + 1);
            String pref_bus_trip_distance = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_TRIP_DISTANCE);
            pref_bus_trip_distance = String.valueOf(Math.round((Double.parseDouble(pref_bus_trip_distance) + distance) * 100.0) / 100.0);

            preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIPS, pref_bus_trip);
            preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIP_DISTANCE, pref_bus_trip_distance);

            String pref_driver_trips = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIPS);
            pref_driver_trips = String.valueOf(Integer.parseInt(pref_driver_trips) + 1);
            String pref_driver_trip_distance = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIP_DISTANCE);
            pref_driver_trip_distance = String.valueOf(Math.round((Double.parseDouble(pref_driver_trip_distance) + distance) * 100.0) / 100.0);

            preferencesManager.setStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIPS, pref_driver_trips);
            preferencesManager.setStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIP_DISTANCE, pref_driver_trip_distance);

        }


        return busLog;
    }

    private void getBusStopsList() {

        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String route_key = preferencesManager.getStringValue(AppConstant.PREF_ROUTE_ID);
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("busstops_tb" + "/" + institute_key + "/" + route_key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (busStopsArrayList != null) {
                    busStopsArrayList.clear();
                }

                DBHelper.init(BusStatusActivity.this);
                DBHelper.deleteGeofenceShopsTable();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BusStops busStops = snapshot.getValue(BusStops.class);
                    busStopsArrayList.add(busStops);
                    DBHelper.addStops(busStops);
                }
                preferencesManager.setStringValue(AppConstant.PREF_TOTAL_BUS_STOPS, String.valueOf(busStopsArrayList.size()));

                bindData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void startTrackerService() {

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

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            Log.d(TAG, "onSuccess: Current location = " + location);

                            //if trip is started .. start LocationUpdate service
                            if (preferencesManager.getBooleanValue(AppConstant.PREF_BTN_START)) {

                                // initialize bus last location to strting point
                                String current_loc = AppUtility.locationToStr(location);
                                preferencesManager.setStringValue(AppConstant.PREF_BUS_LAST_LOCATION, current_loc);

                                String bus_full_path = preferencesManager.getStringValue(AppConstant.PREF_BUS_FULL_PATH);
                                if (!bus_full_path.contains(current_loc)) {
                                    bus_full_path = bus_full_path + ":" + current_loc;
                                    preferencesManager.setStringValue(AppConstant.PREF_BUS_FULL_PATH, bus_full_path);
                                }

                                Intent trackingIntent = new Intent(BusStatusActivity.this, LiveTrackingService.class);
                                trackingIntent.putParcelableArrayListExtra("bus_stops_list", busStopsArrayList);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(trackingIntent);
                                } else {
                                    startService(trackingIntent);
                                }

                                // bindService(liveTrackingIntent, mConnection, Context.BIND_AUTO_CREATE);

                              /*  // Register Geofence
                                mGeoFencing.updateGeofencesList(busStopsArrayList);
                                mGeoFencing.registerAllGeofences();*/

                                //Notify the user that tracking has been enabled//
                                Log.d(TAG, "onSuccess: GPS tracking enabled");

                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    }
                                }
                                mMap.setMyLocationEnabled(true);
                                nextStopTv.setVisibility(View.VISIBLE);
                                busDistanceLayout.setVisibility(View.GONE);
                                speedTV.setVisibility(View.GONE);
                                String nextTripTime = AppUtility.getNextTripTime(preferencesManager.getStringValue(AppConstant.PREF_SC_DEPART_TIME));
                                nextStopTv.setText(/*"Route Name : "+ preferencesManager.getStringValue(AppConstant.PREF_ROUTE_NAME)+".\n*/"Next trip is at " + nextTripTime + ".");
                                updateScreenUI(location, null);
                            }


                        }

                    }
                });

    }

    private void addBusstopsMarker() {

        DBHelper.init(this);
        ArrayList<BusStops> stopsArrayList = DBHelper.getAllBusStops();

        Marker originMarker, destinationMarker;

        if (preferencesManager.getBooleanValue(AppConstant.PREF_TRACK_ENABLED)) {
            isBusArrivalTimeUpdated = true;
        }

        CustomInfoWindow customInfoWindow = new CustomInfoWindow(this);
        mMap.setInfoWindowAdapter(customInfoWindow);


        // add Origin and Destination Marker
        String bus_direction = preferencesManager.getStringValue(AppConstant.PREF_DRIVING_DIRECTION);
        BusStops busStopsOrigin, busStopsDestination;
        if (bus_direction.equalsIgnoreCase("1")) {
            busStopsOrigin = stopsArrayList.get(stopsArrayList.size() - 1);
            busStopsDestination = stopsArrayList.get(0);
        } else {
            busStopsOrigin = stopsArrayList.get(0);
            busStopsDestination = stopsArrayList.get(stopsArrayList.size() - 1);
        }

        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_white);
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap bus_stop_icon = Bitmap.createScaledBitmap(b, 65, 65, false);

        originMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(busStopsOrigin.getLatitude()), Double.valueOf(busStopsOrigin.getLongitude()))).title(busStopsOrigin.getBusStopName()));
        originMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bus_stop_icon));
        originMarker.setTitle(busStopsOrigin.getBusStopName());
        originMarker.setTag(busStopsOrigin);
        destinationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(busStopsDestination.getLatitude()), Double.valueOf(busStopsDestination.getLongitude()))).title(busStopsDestination.getBusStopName()));
        destinationMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bus_stop_icon));
        destinationMarker.setTitle(busStopsDestination.getBusStopName());
        destinationMarker.setTag(busStopsDestination);


        // add inbetween busstops marker.
        for (int i = stopsArrayList.size() - 2; i >= 1; i--) {
            BusStops busStops = stopsArrayList.get(i);
            BitmapDrawable bitmapDrawable1 = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_black);
            Bitmap b1 = bitmapDrawable1.getBitmap();
            Bitmap bus_stop = Bitmap.createScaledBitmap(b1, 50, 50, false);

            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(busStops.getLatitude()), Double.valueOf(busStops.getLongitude()))).title(busStops.getBusStopName()));
            m.setIcon(BitmapDescriptorFactory.fromBitmap(bus_stop));
            m.setTag(busStops);
        }

      /*  //Draw the route.
        String path = preferencesManager.getStringValue(AppConstant.PREF_BUS_PATH);
        if (path.length() > 1) {
            redrawLine(path);
        }*/

    }

    private void updateScreenUI(Location location, String next_stop_name) {


        if (location != null) {

            Boolean is_track_enabled = preferencesManager.getBooleanValue(AppConstant.PREF_TRACK_ENABLED);
            Boolean is_driving = preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING);


            // get distance in kilometer.
            float distance = (float) (preferencesManager.getFloatValue(AppConstant.PREF_BUS_DISATNCE_COVERED) / 1000.00);

            if (is_driving) {
                if (distance > -1) {
                    busDistanceLayout.setVisibility(View.VISIBLE);
                    busDistanceTv.setText(String.format("%.2f", distance));
                }
                float speed = (float) (preferencesManager.getFloatValue(AppConstant.PREF_BUS_SPEED));
                /*if(speed == 0 ) {
                    String[] strings = timerTV.getText().toString().split(":");
                    float time = Float.parseFloat(strings[0]);
                    if (distance > 0 && time > 0) {
                        speed = (distance * 60) / time;
                    }
                }*/
                if (speed > 0) {
                    speedTV.setVisibility(View.VISIBLE);
                    speedTV.setText("Speed : " + String.format("%.2f", speed) + " km/h");
                } else {
                    speedTV.setVisibility(View.GONE);
                }

            }

            LatLng bus_location = new LatLng(location.getLatitude(), location.getLongitude());
            String path = preferencesManager.getStringValue(AppConstant.PREF_BUS_PATH);

            if (path.length() > 1) {
                redrawLine(path);
            }

            if (busMarker != null) {
                if (is_track_enabled && !isBusArrivalTimeUpdated) {

                    displayMapObject(bus_location);

                } else {
                    // Set busmarker title
                    BusStops busMarkerData = new BusStops();
                    busMarkerData.setBusStopName(preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME));
                    busMarker.setTag(busMarkerData);

                    MarkerAnimation.animateMarkerToGB(busMarker, bus_location, new LatLngInterpolator.Spherical());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busMarker.getPosition(), 16));
                }

            } else {

                displayMapObject(bus_location);

            }

        }

        if (preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)) {
            Boolean is_destination_reached = preferencesManager.getBooleanValue(AppConstant.PREF_DEST_REACHED);
            Boolean is_track_enabled = preferencesManager.getBooleanValue(AppConstant.PREF_TRACK_ENABLED);
            Boolean is_trip_completed = preferencesManager.getBooleanValue(AppConstant.PREF_TRIP_COMPLETED);


            String direction = preferencesManager.getStringValue(AppConstant.PREF_DRIVING_DIRECTION);
            String nextStopName = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP);
            String nextStopOrder = preferencesManager.getStringValue(AppConstant.PREF_NEXT_STOP_ORDER);
            String startPointOrder = preferencesManager.getStringValue(AppConstant.PREF_START_POINT_ID);
            String startPointName = preferencesManager.getStringValue(AppConstant.PREF_START_POINT);

            if (is_track_enabled) {
                if (is_destination_reached) {
                    if (is_trip_completed) {
                        nextStopTv.setText("Trip Completed..");
                    } else {
                        if (preferencesManager.getStringValue(AppConstant.PREF_DRIVING_DIRECTION).equalsIgnoreCase("1")) {
                            nextStopTv.setText("Pick Up is Completed..");

                        } else {
                            nextStopTv.setText("Drop is Completed.. Returning to college.");
                        }
                    }
                } else {
                    if (preferencesManager.getBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS)) {
                        nextStopTv.setText("Current Stop : " + nextStopName);
                    } else {
                        nextStopTv.setText("Next Stop : " + nextStopName);
                    }
                }
            } else {
                if (direction.equalsIgnoreCase("1")) {
                    nextStopTv.setText("Pick up from " + startPointName);
                } else {
                    nextStopTv.setText("Drop from " + startPointName);
                }

            }

            if (preferencesManager.getBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS)) {
                checkNearByStudentsCount();
            }

        }
    }

    private void displayMapObject(LatLng bus_location) {

        mMap.clear();

        MarkerOptions markerOptions1 = new MarkerOptions();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.bus_marker);
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap smallCar = Bitmap.createScaledBitmap(b, 84, 96, false);
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(smallCar));
        markerOptions1.position(bus_location);
        markerOptions1.title(preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME));

        busMarker = mMap.addMarker(markerOptions1);
        BusStops busMarkerData = new BusStops();
        busMarkerData.setBusStopName(preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME));
        busMarker.setTag(busMarkerData);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busMarker.getPosition(), 14));

        if (preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)) {

            addBusstopsMarker();

        }
    }

    private void checkNearByStudentsCount() {
        if (preferencesManager.getBooleanValue(AppConstant.PREF_CHECK_NEARBY_STUDENTS)) {
            String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
            String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
            checkNearByStudentsRef = FirebaseDatabase.getInstance().getReference("bus_tracking_tb" + "/" + institute_key + "/" + bus_key);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String count = dataSnapshot.getValue(String.class);
                    updateStudentsBellUI(count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            checkNearByStudentsRef.child(AppConstant.PREF_STR_BELL_COUNTS).addValueEventListener(valueEventListener);

        }

    }

    private void updateStudentsBellUI(String count) {
        if (count != null && Integer.parseInt(count) > 0) {
            if (Integer.parseInt(count) > 9) {
                count = "9+";
            } else {
                count = "0" + count;
            }
            if (!fab_bell.isExtended()) {
                playNotificationSound();
            }
            fab_bell.setText(count);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                fab_bell.setTextColor(getColor(R.color.app_blue));
                fab_bell.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_stop_filled));
                fab_bell.setIconTint(ColorStateList.valueOf(getColor(R.color.app_blue)));

            }
            fab_bell.extend(true);

        } else {
            fab_bell.shrink(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fab_bell.setIconTint(ColorStateList.valueOf(getColor(R.color.primaryColor)));
                fab_bell.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_stop_default));
            }
            checkNearByStudentsRef.removeEventListener(valueEventListener);
        }

    }

    private void redrawLine(String path) {

        String[] points = path.split(":");

        if (points.length > 2) {
            PolylineOptions options = new PolylineOptions().width(10).color(Color.BLACK).geodesic(true);

            if (!isRouteDrawn) {  // if the app is realunched
                isRouteDrawn = true;
                DrawLineAsyncTask drawLineAsyncTask = new DrawLineAsyncTask();
                drawLineAsyncTask.execute(path);
              /*  for (int i = 1; i < points.length; i++) {
                    LatLng point = AppUtility.strToLatlng(points[i]);
                    options.add(point);
                }
                mMap.addPolyline(options); //add Polyline*/
            } else {
                for (int i = points.length - 2; i < points.length; i++) {
                    LatLng point = AppUtility.strToLatlng(points[i]);
                    options.add(point);
                }
                mMap.addPolyline(options); //add Polyline
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_notification) {
            if (ConnectivityReceiver.isConnected()) {
                hideNotificationBadge();
                Intent notificationIntent = new Intent(BusStatusActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
            }

        } else if (id == R.id.nav_trips) {
            if (ConnectivityReceiver.isConnected()) {
                Intent tripListIntent = new Intent(BusStatusActivity.this, TripListActivity.class);
                startActivity(tripListIntent);
            }


        } else if (id == R.id.nav_busdetail) {
            Intent busDetailIntent = new Intent(BusStatusActivity.this, BusDetailActivity.class);
            startActivity(busDetailIntent);

        } else if (id == R.id.nav_settings) {

            Intent settingsIntent = new Intent(BusStatusActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        } else if (id == R.id.nav_logout) {
            if(preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)){
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
                materialAlertDialogBuilder.setTitle("Please complete the trip.");
                materialAlertDialogBuilder.setCancelable(false);
                materialAlertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });
                materialAlertDialogBuilder.show();
            }else {
                logout();
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void logout() {
        if (ConnectivityReceiver.isConnected()) {

            preferencesManager.setBooleanValue(AppConstant.PREF_IS_LOGGEDIN, false);
            preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIPS, "0");
            preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_TRIP_DISTANCE, "0");
            preferencesManager.setStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIPS, "0");
            preferencesManager.setStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIP_DISTANCE, "0");
            preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE, "0");
            preferencesManager.setStringValue(AppConstant.PREF_TOTAL_FUEL, "0");
            preferencesManager.setStringValue(AppConstant.PREF_MILEAGE, "0");
            preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, "0");


            // clear the table
            DBHelper.init(BusStatusActivity.this);
            DBHelper.deleteGeofenceShopsTable();

            // update loggedin false.
            String driverKey = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_KEY);
            String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_DRIVERS_TB).child(institute_key);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put(AppConstant.PREF_STR_LOGGEDIN, false);
            databaseReference.child(driverKey).updateChildren(objectMap);


            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(BusStatusActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (ConnectivityReceiver.isConnected()) {
            finish();
            startActivity(getIntent());
        } else {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.fab_bell), getResources().getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.app_blue));
            snackbar.show();

        }
    }

    private class DrawLineAsyncTask extends AsyncTask<String, Void, PolylineOptions> {

        @Override
        protected PolylineOptions doInBackground(String... strings) {

            PolylineOptions options = new PolylineOptions().width(10).color(Color.BLACK).geodesic(true);

            String path = strings[0];
            String[] points = path.split(":");

            if (points.length > 2) {
                //  PolylineOptions options = new PolylineOptions().width(10).color(Color.BLACK).geodesic(true);

                for (int i = 1; i < points.length; i++) {
                    LatLng point = AppUtility.strToLatlng(points[i]);
                    options.add(point);
                }

            }

            return options;
        }

        @Override
        protected void onPostExecute(PolylineOptions polylineOptions) {
            super.onPostExecute(polylineOptions);
            mMap.addPolyline(polylineOptions); //add Polyline
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Playing notification sound
    public void playNotificationSound() {

        //play sound if notification sound is on
        //  if (myPref.getBooleanValue(IS_NOTIFICATION_SOUND_ON)) {
        try {
            //Uri path = Uri.parse("android.resource://" + mContext.getPackageName() + "/raw/notification.mp3");
            //RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, path);
            Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), path);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}

    }

}
