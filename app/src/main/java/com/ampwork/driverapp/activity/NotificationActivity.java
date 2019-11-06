package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.EqualSpacingItemDecoration;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.adapter.NotificationsAdapter;
import com.ampwork.driverapp.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {
    private ArrayList<Notification> generalNotificationArrayList = new ArrayList<>();
    private ArrayList<Notification> sectionNotificationArrayList = new ArrayList<>();
    private RecyclerView generalNotificationListView,sectionNotificationListView;
    private NotificationsAdapter generalAdapter,sectionAdapter;

    private TextView noDataTv;
    private ImageView backImageView;
    private LinearLayout progressBarLayout,generalLayout,sectionLayout;

    private PreferencesManager preferencesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        preferencesManager = new PreferencesManager(this);

        noDataTv = findViewById(R.id.noDataTv);
        generalNotificationListView = findViewById(R.id.generalNotificationListView);
        sectionNotificationListView = findViewById(R.id.sectionNotificationListView);
        backImageView = findViewById(R.id.backImageView);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        generalLayout = findViewById(R.id.generalLayout);
        sectionLayout = findViewById(R.id.sectionLayout);



        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getGeneralNotifications();
        getSectionNotifications();

    }

    private void getSectionNotifications() {

        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_NOTIFICATION_TB);
        databaseReference.child(institute_key).orderByChild("usergroup").equalTo("driver").limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    sectionNotificationArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        sectionNotificationArrayList.add(notification);
                    }
                    if (sectionNotificationArrayList.size() > 0) {
                        noDataTv.setVisibility(View.GONE);
                        sectionLayout.setVisibility(View.VISIBLE);
                        Collections.reverse(sectionNotificationArrayList);
                        displaySectionList();

                    } else {
                        noDataTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    noDataTv.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
             //   progressBarLayout.setVisibility(View.GONE);
                noDataTv.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displaySectionList() {
        preferencesManager.setBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED,false);

        sectionNotificationListView.setVisibility(View.VISIBLE);
        sectionAdapter = new NotificationsAdapter(NotificationActivity.this, sectionNotificationArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        sectionNotificationListView.setLayoutManager(mLayoutManager);
        sectionNotificationListView.addItemDecoration(new EqualSpacingItemDecoration(40));
        sectionNotificationListView.setAdapter(sectionAdapter);
    }

    private void getGeneralNotifications() {

        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_NOTIFICATION_TB);
        databaseReference.child(institute_key).orderByChild("usergroup").equalTo("all").limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    generalNotificationArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        generalNotificationArrayList.add(notification);
                    }
                    if (generalNotificationArrayList.size() > 0) {
                        noDataTv.setVisibility(View.GONE);
                        generalLayout.setVisibility(View.VISIBLE);
                        Collections.reverse(generalNotificationArrayList);
                        displayGeneralList();

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

    private void displayGeneralList() {

        preferencesManager.setBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED,false);

        generalNotificationListView.setVisibility(View.VISIBLE);
        generalAdapter = new NotificationsAdapter(NotificationActivity.this, generalNotificationArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        generalNotificationListView.setLayoutManager(mLayoutManager);
        generalNotificationListView.addItemDecoration(new EqualSpacingItemDecoration(40));
        generalNotificationListView.setAdapter(generalAdapter);
    }

}
