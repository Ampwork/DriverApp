package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.EqualSpacingItemDecoration;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.adapter.NotificationPagerAdapter;
import com.ampwork.driverapp.adapter.NotificationsAdapter;
import com.ampwork.driverapp.fragment.DriverNotificationFragment;
import com.ampwork.driverapp.fragment.GeneralNotificationFragment;
import com.ampwork.driverapp.model.Notification;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView backImageView;

    private PreferencesManager preferencesManager;
    private Boolean isFromNotification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        preferencesManager = new PreferencesManager(this);
        //Reset the notification flag
        preferencesManager.setBooleanValue(AppConstant.PREF_NOTIFICATION_ARRIVED, false);
        isFromNotification = getIntent().getBooleanExtra("is_from_notification",false);
        backImageView = findViewById(R.id.backImageView);

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        String section_type = "driver";

        Fragment generalNotificatonFrag = GeneralNotificationFragment.newInstance(institute_key);
        Fragment userNotificationFrag = DriverNotificationFragment.newInstance(institute_key,section_type);

        NotificationPagerAdapter adapter = new NotificationPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(generalNotificatonFrag, "GENERAL");
        adapter.addFragment(userNotificationFrag, section_type.toUpperCase());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isFromNotification){
            Intent intent = new Intent(NotificationActivity.this, BusStatusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
