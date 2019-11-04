package com.ampwork.driverapp.activity;

import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.PreferencesManager;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private ArrayList<String> driver_names = new ArrayList<>();
    PreferencesManager preferencesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferencesManager = new PreferencesManager(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if(preferencesManager.getBooleanValue(AppConstant.PREF_IS_LOGGEDIN)){
                    intent = new Intent(SplashActivity.this, BusStatusActivity.class);
                   /* if(preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)){
                        intent = new Intent(SplashActivity.this, BusStatusActivity.class);
                    }else {
                        intent = new Intent(SplashActivity.this, TripDetaiActivity.class);
                    }*/

                }else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        },2000);

    }

}