package com.ampwork.driverapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private  TextInputEditText driverNameEdt,genderEdt,bloodgpEdt,dlEdt,phoneEdt,altphoneEdt,instituteEdt;
    private ImageView backImageView;
    PreferencesManager preferencesManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        preferencesManager = new PreferencesManager(this);

        backImageView = findViewById(R.id.backImageView);
        driverNameEdt = findViewById(R.id.driverNameEdt);
        genderEdt  = findViewById(R.id.genderEdt);
        bloodgpEdt  = findViewById(R.id.bloodgpEdt);
        dlEdt  = findViewById(R.id.dlEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        altphoneEdt  = findViewById(R.id.altphoneEdt);
        instituteEdt  = findViewById(R.id.instituteEdt);

        driverNameEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME));
        genderEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_GENDER));
        bloodgpEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_BG));
        dlEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_DL));
        phoneEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_PHONE));
        altphoneEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_EMERGENCY_PHONE));
        instituteEdt.setText(preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE));

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}
