package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.CustomItemDecorator;
import com.ampwork.driverapp.Util.EqualSpacingItemDecoration;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.Util.SimpleLineDividerItemDecoration;
import com.ampwork.driverapp.adapter.NotificationsAdapter;
import com.ampwork.driverapp.adapter.TripListAdapter;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TripListActivity extends AppCompatActivity {

    private ArrayList<BusLog> busLogArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TripListAdapter mAdapter;

    private TextView noDataTv;
    private ImageView backImageView;
    private LinearLayout progressBarLayout;

    private PreferencesManager preferencesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        preferencesManager = new PreferencesManager(this);
        backImageView = findViewById(R.id.backImageView);
        noDataTv = findViewById(R.id.noDataTv);
        progressBarLayout = findViewById(R.id.progressBarLayout);

        recyclerView = findViewById(R.id.tripListView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new EqualSpacingItemDecoration(20));
        recyclerView.addItemDecoration(new SimpleLineDividerItemDecoration(this));


        getTripData();





        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private void getTripData() {

        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String bus_number = preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER);
        String driver_id = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID);
        String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_BUS_LOGS_TB);
        databaseReference.child(institute_key).child(bus_key).orderByChild("driverId").equalTo(driver_id).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    ArrayList<BusLog> busLogs = new ArrayList<BusLog>();
                    busLogArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BusLog busLog = snapshot.getValue(BusLog.class);
                        busLogs.add(busLog);

                    }
                    if (busLogs.size() > 0) {
                        noDataTv.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Collections.reverse(busLogs);
                        displayList(busLogs);
                    } else {
                        noDataTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    noDataTv.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBarLayout.setVisibility(View.GONE);
                noDataTv.setVisibility(View.VISIBLE);

            }
        });
    }

    private void displayList(ArrayList<BusLog> busLogArrayList1) {
        busLogArrayList.addAll(busLogArrayList1);
        mAdapter = new TripListAdapter(this, busLogArrayList);
        recyclerView.setAdapter(mAdapter);
    }
}
