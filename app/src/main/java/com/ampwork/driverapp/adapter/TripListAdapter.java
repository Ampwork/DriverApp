package com.ampwork.driverapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.model.BusLog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder> {

    Context context;
    ArrayList<BusLog> busLogArrayList;

    public TripListAdapter(Context context, ArrayList<BusLog> arrayList) {
        this.context = context;
        this.busLogArrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       final BusLog busLog = busLogArrayList.get(position);

       String trip_type = AppConstant.PREF_STR_DROP ;

       if(busLog.getDirection().equalsIgnoreCase("1")){
           trip_type = AppConstant.PREF_STR_PICKUP;
        }
       holder.routeNameTv.setText(busLog.getRouteName() + " - " +  trip_type);
       holder.tripDistanceTv.setText("Distance : " + busLog.getTripDistance() + " km");
       holder.tripDurationTv.setText("Duration : " + busLog.getTripDuration() + " min");
       holder.tripDateTv.setText(busLog.getDate());

       holder.mainLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               showLogDetail(busLog);
           }
       });




    }

    @Override
    public int getItemCount() {
        return busLogArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView routeNameTv,tripDistanceTv, tripDurationTv,tripDateTv;
        LinearLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            routeNameTv = itemView.findViewById(R.id.routeNameTv);
            tripDistanceTv = itemView.findViewById(R.id.tripDistanceTv);
            tripDurationTv = itemView.findViewById(R.id.tripDurationTv);
            tripDateTv = itemView.findViewById(R.id.tripDateTv);
        }
    }


    private void showLogDetail(BusLog logDetail) {

        View view = LayoutInflater.from(context).inflate(R.layout.trip_status_layout, null);
        TextView routeNameTv = view.findViewById(R.id.routeNameTv);
        TextView tripTypeTv = view.findViewById(R.id.tripTypeTv);
        TextView tripDepartTv = view.findViewById(R.id.tripDepartTv);
        TextView tripArrivalTv = view.findViewById(R.id.tripArrivalTv);
        TextView tripstausTv = view.findViewById(R.id.tripstausTv);
        TextView tripDistanceTv = view.findViewById(R.id.tripDistanceTv);
        TextView tripDurationTv = view.findViewById(R.id.tripDurationTv);
        TextView stopsCoveredTv = view.findViewById(R.id.stopsCoveredTv);
        Button successBtn = view.findViewById(R.id.successBtn);

        routeNameTv.setText("RouteName : " + logDetail.getRouteName());
        if (logDetail.getDirection().equalsIgnoreCase("1")) {
            tripTypeTv.setText("Trip Type : " + AppConstant.PREF_STR_PICKUP);
        } else {
            tripTypeTv.setText("Trip Type : " + AppConstant.PREF_STR_DROP);
        }
        tripDepartTv.setText("Trip Started Time : " + logDetail.getDepartTime());
        tripArrivalTv.setText("Trip Ended Time : " + logDetail.getArrivedTime());
        if (logDetail.getTripCompleted().equalsIgnoreCase("1")) {
            tripstausTv.setText("Trip Completed : " + "Yes");
            stopsCoveredTv.setText("All stops covered : Yes");
        } else if (logDetail.getTripCompleted().equalsIgnoreCase("0")) {
            tripstausTv.setText("Trip Completed : " + "Yes");
            stopsCoveredTv.setText("All stops covered : No");
        } else if (logDetail.getTripCompleted().equalsIgnoreCase("-1")) {
            tripstausTv.setText("Trip Completed : " + "No");
            stopsCoveredTv.setText("All stops covered : No");
        }

        tripDistanceTv.setText("Distance Covered : " + logDetail.getTripDistance() + " kms");
        tripDurationTv.setText("Time Taken : " + logDetail.getTripDuration() + " minutes");


        final AlertDialog dialog = new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setView(view)
                .setCancelable(false)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


}
