package com.ampwork.driverapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.model.BusLog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsActivity extends AppCompatActivity {

    private TextView profileTv,changePwdTv;
    private ImageView backImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backImageView = findViewById(R.id.backImageView);
        profileTv = findViewById(R.id.profileTv);
        changePwdTv = findViewById(R.id.changePwdTv);




        profileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(SettingsActivity.this,ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        changePwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePassword();
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void showChangePassword() {
        View view = LayoutInflater.from(this).inflate(R.layout.change_password_layout, null);
        TextView oldPasswordEdt = view.findViewById(R.id.oldPasswordEdt);
        TextView newPasswordEdt = view.findViewById(R.id.newPasswordEdt);
        TextView confirmPasswordEdt = view.findViewById(R.id.confirmPasswordEdt);
        Button successBtn = view.findViewById(R.id.successBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);


        final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
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
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
