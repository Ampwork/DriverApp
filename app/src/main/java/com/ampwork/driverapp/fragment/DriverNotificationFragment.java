package com.ampwork.driverapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.SimpleLineDividerItemDecoration;
import com.ampwork.driverapp.adapter.NotificationsAdapter;
import com.ampwork.driverapp.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class DriverNotificationFragment extends Fragment {
    private static final String ARG_PARAM1 = "institute_key";
    private static final String ARG_PARAM2 = "section_type";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ArrayList<Notification> sectionNotificationArrayList = new ArrayList<>();
    private RecyclerView sectionNotificationListView;
    private NotificationsAdapter sectionAdapter;

    private TextView noDataTv;
    private LinearLayout progressBarLayout;



    public DriverNotificationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DriverNotificationFragment newInstance(String param1, String param2) {
        DriverNotificationFragment fragment = new DriverNotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_driver_notification, container, false);

        noDataTv = layout.findViewById(R.id.noDataTv);
        sectionNotificationListView = layout.findViewById(R.id.sectionNotificationListView);
        progressBarLayout = layout.findViewById(R.id.progressBarLayout);

        getSectionNotifications();

        return layout;
    }

    private void getSectionNotifications() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_NOTIFICATION_TB);
        databaseReference.child(mParam1).orderByChild("usergroup").equalTo(mParam2).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBarLayout.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    sectionNotificationArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        sectionNotificationArrayList.add(notification);
                    }
                    if (sectionNotificationArrayList.size() > 0) {
                        noDataTv.setVisibility(View.GONE);
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
                progressBarLayout.setVisibility(View.GONE);
                noDataTv.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displaySectionList() {
        sectionNotificationListView.setVisibility(View.VISIBLE);
        sectionAdapter = new NotificationsAdapter(getActivity(), sectionNotificationArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        sectionNotificationListView.setLayoutManager(mLayoutManager);
        sectionNotificationListView.addItemDecoration(new SimpleLineDividerItemDecoration(getActivity()));
        /* sectionNotificationListView.addItemDecoration(new EqualSpacingItemDecoration(40));*/
        sectionNotificationListView.setAdapter(sectionAdapter);
    }



}
