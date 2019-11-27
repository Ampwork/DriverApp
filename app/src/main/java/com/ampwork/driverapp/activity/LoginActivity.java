package com.ampwork.driverapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.ampwork.driverapp.MyApplication;
import com.ampwork.driverapp.model.InstituteDetail;
import com.ampwork.driverapp.receiver.ConnectivityReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    private EditText phoneEdt, passwordEdt;
    private TextInputLayout instituteAutoComTvLayout, phoneEdtLayout, passwordEdtLayout;
    private AutoCompleteTextView instituteAutoComTv;
    private Button loginBtn;
    private ImageView logo;


    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    PreferencesManager preferencesManager;

    ArrayList<InstituteDetail> instituteDetailArrayList = new ArrayList<InstituteDetail>();
    private ArrayAdapter<String> instituteAdapter;

    String fcm_token = "";
    private static final String TAG = "loginactivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferencesManager = new PreferencesManager(this);

        logo = findViewById(R.id.logo);
        instituteAutoComTv = findViewById(R.id.instituteAutoComTv);
        phoneEdt = findViewById(R.id.phoneEdt);
        passwordEdt = findViewById(R.id.passwordEdt);

        instituteAutoComTvLayout = findViewById(R.id.instituteAutoComTvLayout);
        phoneEdtLayout = findViewById(R.id.phoneEdtLayout);
        passwordEdtLayout = findViewById(R.id.passwordEdtLayout);

        loginBtn = findViewById(R.id.loginBtn);

        getFcmToken();

        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.logo_black);
        Bitmap b = bitmapDrawable.getBitmap();
        Bitmap smallImag = Bitmap.createScaledBitmap(b, 512, 256, false);
        logo.setImageBitmap(smallImag);

        instituteAutoComTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    instituteAutoComTvLayout.setError(null);
                    instituteAutoComTvLayout.setErrorEnabled(false);
                }

            }
        });

        phoneEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    phoneEdtLayout.setError(null);
                    phoneEdtLayout.setErrorEnabled(false);
                }
            }
        });

        passwordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    passwordEdtLayout.setError(null);
                    passwordEdtLayout.setErrorEnabled(false);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectivityReceiver.isConnected()) {
                    validateUser();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(view, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

        if (ConnectivityReceiver.isConnected()) {
            getIntitutesList();
        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.loginBtn), getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void getIntitutesList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("institute_tb");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                instituteDetailArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InstituteDetail instituteDetail = snapshot.getValue(InstituteDetail.class);
                    instituteDetail.setKey(snapshot.getKey());
                    instituteDetailArrayList.add(instituteDetail);
                }
                setInstitutesAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Data Base connection error..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setInstitutesAdapter() {
        String[] institute_array = new String[instituteDetailArrayList.size()];
        for (int i = 0; i < instituteDetailArrayList.size(); i++) {
            institute_array[i] = instituteDetailArrayList.get(i).getName();
        }

        instituteAdapter = new ArrayAdapter<>(LoginActivity.this, R.layout.dropdown_menu_popup_item, institute_array);
        instituteAutoComTv.setAdapter(instituteAdapter);

    }


    private void validateUser() {

        if (!validate()) {
            return;
        }

        loginBtn.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying Login...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        callLoginApi();
                    }
                }, 2000);

    }

    private void callLoginApi() {

        String institute_name = instituteAutoComTv.getText().toString();
        String institute_key = "";
        String institute_type;

        for (int i = 0; i < instituteDetailArrayList.size(); i++) {
            InstituteDetail instituteDetail = instituteDetailArrayList.get(i);
            if (instituteDetail.getName().equalsIgnoreCase(institute_name.toString())) {
                institute_key = instituteDetail.getKey();
                institute_type = instituteDetail.getType();
            }
        }

        String phone = phoneEdt.getText().toString();
        final String password = passwordEdt.getText().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("drivers_tb/" + institute_key);

        final String finalInstitute_key = institute_key;
        databaseReference.orderByChild("driverPhone").equalTo(phone).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                loginBtn.setEnabled(true);
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                if (result != null) {
                    Set<String> keyset = result.keySet();
                    String driverKey = null;
                    for (String keyName : keyset) {
                        driverKey = keyName;

                    }

                    Object driverObject = result.get(driverKey);
                    Map<String, Object> object = (Map<String, Object>) driverObject;
                    if (password.equalsIgnoreCase(object.get(AppConstant.PREF_DRIVER_PASSWORD).toString())) {
                        if ((boolean) object.get(AppConstant.PREF_STR_LOGGEDIN)) {
                            Toast.makeText(LoginActivity.this, "User is already loggedin..", Toast.LENGTH_SHORT).show();
                        } else {
                            saveData(object, driverKey, finalInstitute_key);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                loginBtn.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login Failed..", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String instituteName = instituteAutoComTv.getText().toString();
        String phone = phoneEdt.getText().toString();
        String pwd = passwordEdt.getText().toString();

        if (instituteName.isEmpty()) {
            instituteAutoComTvLayout.setError(getResources().getString(R.string.institute_error_text));
            valid = false;
        } else if (phone.length() < 10) {
            phoneEdtLayout.setError("Enter 10 digit number");
            valid = false;
        } else if (pwd.isEmpty() || pwd.length() < 6) {
            passwordEdtLayout.setError("Enter atleast 6 characters");
            valid = false;
        }

        return valid;
    }

    private void saveData(Map<String, Object> object, String driverKey, String institute_key) {

        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_KEY, driverKey);
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY, institute_key);

        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_ID, object.get(AppConstant.PREF_DRIVER_ID).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_NAME, object.get(AppConstant.PREF_DRIVER_NAME).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_PHONE, object.get(AppConstant.PREF_DRIVER_PHONE).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_BG, object.get(AppConstant.PREF_DRIVER_BG).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_DL, object.get(AppConstant.PREF_DRIVER_DL).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_EMERGENCY_PHONE, object.get(AppConstant.PREF_DRIVER_EMERGENCY_PHONE).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_GENDER, object.get(AppConstant.PREF_DRIVER_GENDER).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_INSTITUTE, object.get(AppConstant.PREF_DRIVER_INSTITUTE).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_PASSWORD, object.get(AppConstant.PREF_DRIVER_PASSWORD).toString());

        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIPS, object.get(AppConstant.PREF_STR_TOTAL_TRIPS).toString());
        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_TOTAL_TRIP_DISTANCE, object.get(AppConstant.PREF_STR_TRIP_DISTANCE).toString());


        preferencesManager.setBooleanValue(AppConstant.PREF_IS_LOGGEDIN, true);


        // update fcm token
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_DRIVERS_TB).child(institute_key);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(AppConstant.PREF_STR_FCMTOKEN, fcm_token);
        objectMap.put(AppConstant.PREF_STR_LOGGEDIN, true);
        databaseReference.child(driverKey).updateChildren(objectMap);


        Intent intent = new Intent(LoginActivity.this, BusStatusActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcm_token = task.getResult().getToken();
                        preferencesManager.setStringValue(AppConstant.PREF_DRIVER_FCMTOKEN, fcm_token);

                    }
                });

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            finish();
            startActivity(getIntent());
        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.loginBtn), getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG);
            snackbar.show();

        }

    }
}
