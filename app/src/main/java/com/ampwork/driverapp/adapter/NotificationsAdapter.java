package com.ampwork.driverapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.model.Notification;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    Context context;
    ArrayList<Notification> notificationArrayList;

    public NotificationsAdapter(Context context, ArrayList<Notification> notificationArrayList) {
        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_layout,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       final Notification notification = notificationArrayList.get(position);

       holder.messageTv.setText(notification.getMessage());
       holder.dateTv.setText(notification.getDate());

       holder.mainLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showDetail(notification);
           }
       });
    }

    private void showDetail(Notification notification) {

        View view = LayoutInflater.from(context).inflate(R.layout.notification_detail_layout, null);

        Button successBtn = view.findViewById(R.id.successBtn);
        TextView dateTv = view.findViewById(R.id.dateTv);
        TextView messageTv = view.findViewById(R.id.messageTv);

        dateTv.setText(notification.getDate());
        messageTv.setText(notification.getMessage());

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

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView messageTv,dateTv;
        RelativeLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            messageTv = itemView.findViewById(R.id.messageTv);
            dateTv = itemView.findViewById(R.id.dateTv);
        }

    }
}
