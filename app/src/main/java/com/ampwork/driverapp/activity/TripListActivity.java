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
        recyclerView = findViewById(R.id.tripListView);
        progressBarLayout = findViewById(R.id.progressBarLayout);


        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getTripData();

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
                    busLogArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BusLog busLog = snapshot.getValue(BusLog.class);
                        busLogArrayList.add(busLog);

                    }
                    if (busLogArrayList.size() > 0) {
                        noDataTv.setVisibility(View.GONE);
                        Collections.reverse(busLogArrayList);
                        displayList();
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

    private void displayList() {
        recyclerView.setVisibility(View.VISIBLE);
        mAdapter = new TripListAdapter(this, busLogArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(40));
        recyclerView.setAdapter(mAdapter);
    }
}
