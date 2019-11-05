package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.ampwork.driverapp.database.DBHelper;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {
    private ArrayList<Notification> notificationArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotificationsAdapter mAdapter;

    private TextView noDataTv;
    private ImageView backImageView;
    private LinearLayout progressBarLayout;

    private PreferencesManager preferencesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        preferencesManager = new PreferencesManager(this);

        noDataTv = findViewById(R.id.noDataTv);
        recyclerView = findViewById(R.id.notificationListView);
        backImageView = findViewById(R.id.backImageView);
        progressBarLayout = findViewById(R.id.progressBarLayout);


       /* DBHelper.init(this);
        notificationArrayList = DBHelper.getAllNotifications();
        if (notificationArrayList.size() > 0) {
            mAdapter.notifyDataSetChanged();
        } else {
            getNotifications();
        }*/

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getNotifications();

    }

    private void getNotifications() {

        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_NOTIFICATION_TB);
        databaseReference.child(institute_key).orderByChild("usergroup").equalTo("student").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    notificationArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        notificationArrayList.add(notification);
                    }
                    if (notificationArrayList.size() > 0) {
                        noDataTv.setVisibility(View.GONE);
                        Collections.reverse(notificationArrayList);
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

        preferencesManager.setBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED,false);

        recyclerView.setVisibility(View.VISIBLE);
        mAdapter = new NotificationsAdapter(NotificationActivity.this, notificationArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new EqualSpacingItemDecoration(40));
        recyclerView.setAdapter(mAdapter);
    }

}
