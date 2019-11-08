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

public class GeneralNotificationFragment extends Fragment {

    private static final String ARG_PARAM1 = "institute_key";


    // TODO: Rename and change types of parameters
    private String mParam1;




    private ArrayList<Notification> generalNotificationArrayList = new ArrayList<>();
    private RecyclerView generalNotificationListView;
    private NotificationsAdapter generalAdapter;

    private TextView noDataTv;
    private LinearLayout progressBarLayout;




    public GeneralNotificationFragment() {
        // Required empty public constructor
    }

    public static GeneralNotificationFragment newInstance(String param1) {
        GeneralNotificationFragment fragment = new GeneralNotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View layout = inflater.inflate(R.layout.fragment_general_notification, container, false);


        noDataTv = layout.findViewById(R.id.noDataTv);
        generalNotificationListView = layout.findViewById(R.id.generalNotificationListView);
        progressBarLayout = layout.findViewById(R.id.progressBarLayout);

        getGeneralNotifications();

        return layout;
    }


    private void getGeneralNotifications() {

        //  String institute_key = preferencesManager.getStringValue(AppConstants.PREF_USER_INSTITUTE_KEY);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_NOTIFICATION_TB);
        databaseReference.child(mParam1).orderByChild("usergroup").equalTo("all").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
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

        //preferencesManager.setBooleanValue(AppConstants.PREF_NOTIFICATION_ARRIVED,false);

        generalNotificationListView.setVisibility(View.VISIBLE);
        generalAdapter = new NotificationsAdapter(getActivity(), generalNotificationArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        generalNotificationListView.setLayoutManager(mLayoutManager);
        generalNotificationListView.addItemDecoration(new SimpleLineDividerItemDecoration(getActivity()));
        /* generalNotificationListView.addItemDecoration(new EqualSpacingItemDecoration(40));*/
        generalNotificationListView.setAdapter(generalAdapter);
    }

}
