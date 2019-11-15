package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.receiver.ConnectivityReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private TextView profileTv, changePwdTv;
    private ImageView backImageView;

    PreferencesManager preferencesManager;
    String pref_old_pwd;
    String institute_key,driver_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferencesManager = new PreferencesManager(this);
        pref_old_pwd = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_PASSWORD);
        institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        driver_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_KEY);

        backImageView = findViewById(R.id.backImageView);
        profileTv = findViewById(R.id.profileTv);
        changePwdTv = findViewById(R.id.changePwdTv);


        profileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        changePwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectivityReceiver.isConnected()) {
                    showChangePassword();
                }
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

        final TextInputLayout oldPasswordEdtLayout = view.findViewById(R.id.oldPasswordEdtLayout);
        final TextInputLayout newPasswordEdtLayout = view.findViewById(R.id.newPasswordEdtLayout);
        final TextInputLayout confirmPasswordEdtLayout = view.findViewById(R.id.confirmPasswordEdtLayout);

        final TextInputEditText oldPasswordEdt = view.findViewById(R.id.oldPasswordEdt);
        final TextInputEditText newPasswordEdt = view.findViewById(R.id.newPasswordEdt);
        final TextInputEditText confirmPasswordEdt = view.findViewById(R.id.confirmPasswordEdt);
        Button successBtn = view.findViewById(R.id.successBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        oldPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    oldPasswordEdtLayout.setError(null);
                }

            }
        });

        newPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    newPasswordEdtLayout.setError(null);
                }

            }
        });

        confirmPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    confirmPasswordEdtLayout.setError(null);
                }

            }
        });


        final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setView(view)
                .setCancelable(false)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_pwd = oldPasswordEdt.getText().toString();
                String new_pwd = newPasswordEdt.getText().toString();
                String confirm_pwd = confirmPasswordEdt.getText().toString();

                if (old_pwd.isEmpty() || old_pwd.length()<6) {
                    oldPasswordEdtLayout.setError(getResources().getString(R.string.helper_pwd_txt));
                } else if (new_pwd.isEmpty() || new_pwd.length()<6) {
                    newPasswordEdtLayout.setError(getResources().getString(R.string.helper_pwd_txt));
                } else if (confirm_pwd.isEmpty() || confirm_pwd.length()<6) {
                    confirmPasswordEdtLayout.setError(getResources().getString(R.string.helper_pwd_txt));
                } else if (!old_pwd.equalsIgnoreCase(pref_old_pwd)) {
                    oldPasswordEdtLayout.setError("Old password is wrong");
                } else if (!new_pwd.equalsIgnoreCase(confirm_pwd)) {
                    confirmPasswordEdtLayout.setError("Password did not match");
                } else {
                    dialog.dismiss();
                    if(!old_pwd.equalsIgnoreCase(new_pwd)) {
                        callChangePwdApi(new_pwd);
                    }
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void callChangePwdApi(String new_pwd) {

        final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Resetting the password...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_DRIVERS_TB);
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("password",new_pwd);
        databaseReference.child(institute_key).child(driver_key).updateChildren(objectMap).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "Password changed succesfully..", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "Password did not changed..", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
