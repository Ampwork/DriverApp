package com.ampwork.driverapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ampwork.driverapp.MyApplication;
import com.ampwork.driverapp.R;
import com.ampwork.driverapp.Util.AppConstant;
import com.ampwork.driverapp.Util.AppUtility;
import com.ampwork.driverapp.Util.PreferencesManager;
import com.ampwork.driverapp.model.BusLog;
import com.ampwork.driverapp.model.Fuel;
import com.ampwork.driverapp.receiver.ConnectivityReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FuelEntryActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private TextInputLayout fuelQtyEdtLayout, amountEdtLayout, odometerEdtLayout;
    private EditText driverNameEdt, busNameEdt, busNumberEdt, fuelQtyEdt, amountEdt, odometerEdt, previousOdometerEdt;
    private Button saveBtn, cancelBtn;
    private ImageView backImageView;
    private TextView fuelReadingTv, mileageTv;

    private String driver_id, driver_name, bus_name, bus_number, fuel_qty, amount, date, previous_reading,
            current_odometer, initial_odometer, total_distance;
    String pref_total_fuel, pref_total_distance, institute_key, bus_key;
    private Fuel previousFuelData, currentFuelData;
    int diff = 0, initial_distance = 0;
    double total_fuel = 0;


    PreferencesManager preferencesManager;
    ProgressDialog progressDialog;
    private static final String TAG = "FuelEntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_entry);
        preferencesManager = new PreferencesManager(FuelEntryActivity.this);
        initViews();
        bindData();

        fuelQtyEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    fuelQtyEdtLayout.setError(null);
                  /*  fuelQtyEdtLayout.setHintEnabled(true);
                    fuelQtyEdtLayout.setHelperText(getResources().getString(R.string.helper_txt_fuel));*/
                }
            }
        });
        amountEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    amountEdtLayout.setError(null);
                  /*  amountEdtLayout.setHintEnabled(true);
                    amountEdtLayout.setHelperText(getResources().getString(R.string.helper_txt_amount));*/
                }
            }
        });
        odometerEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {
                    odometerEdtLayout.setError(null);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    saveData();
                } else {
                    final Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.saveBtn), getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();

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

    private void saveData() {

        fuel_qty = fuelQtyEdt.getText().toString();
        amount = amountEdt.getText().toString();
        current_odometer = odometerEdt.getText().toString();
        date = AppUtility.getCurrentDate();

        if (fuel_qty.isEmpty() || Double.parseDouble(fuel_qty) < 1) {
            fuelQtyEdtLayout.setError(getResources().getString(R.string.helper_txt_fuel));

        } else if (amount.isEmpty() || Double.parseDouble(amount) < 1) {
            amountEdtLayout.setError(getResources().getString(R.string.helper_txt_amount));

        } else if (current_odometer.isEmpty() || Integer.parseInt(current_odometer) < 1) {
            odometerEdtLayout.setError(getResources().getString(R.string.error_txt_odometer));
        } else {
            saveBtn.setEnabled(false);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            validateFuelEntry(previousFuelData);
                        }
                    }, 2000);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);

        if (ConnectivityReceiver.isConnected()) {
            getLastFuelReading();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.saveBtn), getResources().getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.app_blue));
            snackbar.show();

        }
    }

    private void getLastFuelReadingCOpy() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("fuel_tb");

        databaseReference.child(institute_key).child(bus_key).orderByChild(AppConstant.PREF_BUS_NUMBER).equalTo(bus_number).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    HashMap<String, Fuel> result = (HashMap<String, Fuel>) dataSnapshot.getValue();
                    Set<String> keyset = result.keySet();
                    String key = null;
                    for (String keyName : keyset) {
                        key = keyName;
                    }

                    Object driverObject = result.get(key);
                    Map<String, Object> object = (Map<String, Object>) driverObject;
                    if (object != null) {

                        previousFuelData = new Fuel();
                        previousFuelData.setBusReading(object.get("busReading").toString());
                        previousFuelData.setOdometer(object.get("odometer").toString());
                        previousFuelData.setPreviousReading(object.get("previousReading").toString());

                        previousOdometerEdt.setText(previousFuelData.getOdometer());

                    }

                } else {
                    previousFuelData = null;
                    previousOdometerEdt.setText(initial_odometer);
                }


                preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, previousOdometerEdt.getText().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage().toString());
            }
        });


    }

    private void getLastFuelReading() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_FUEL_TB);

        databaseReference.child(institute_key).child(bus_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;

                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Fuel fuelData = snapshot.getValue(Fuel.class);

                        Double fuel_qty = Double.parseDouble(fuelData.getFuel());
                        total_fuel = total_fuel + fuel_qty;

                        if (initial_distance == 0) {
                            int bus_reading = Integer.parseInt(fuelData.getBusReading());
                            initial_distance = bus_reading;
                        }

                        if (i == dataSnapshot.getChildrenCount() - 1) {
                            if (fuelData != null) {
                                previousFuelData = fuelData;
                                previousOdometerEdt.setText(previousFuelData.getOdometer());

                                preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, fuelData.getBusReading());

                            }
                        }
                        // increment i
                        i = i + 1;
                    }

                } else {
                    previousFuelData = null;
                    previousOdometerEdt.setText(initial_odometer);
                    preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, initial_odometer);
                    fuelReadingTv.setText("00");
                    mileageTv.setText("00");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage().toString());
            }
        });


    }

    private void bindData() {

        pref_total_fuel = preferencesManager.getStringValue(AppConstant.PREF_TOTAL_FUEL);
        pref_total_distance = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE);

        institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
        bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);

        driver_id = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_ID);
        driver_name = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_NAME);
        bus_name = preferencesManager.getStringValue(AppConstant.PREF_BUS_NAME);
        bus_number = preferencesManager.getStringValue(AppConstant.PREF_BUS_NUMBER);
        initial_odometer = preferencesManager.getStringValue(AppConstant.PREF_INITIAL_ODOMETER);


        driverNameEdt.setText(driver_name);
        busNameEdt.setText(bus_name);
        busNumberEdt.setText(bus_number);

        String total_fuel = preferencesManager.getStringValue(AppConstant.PREF_TOTAL_FUEL);
        //String total_distance = preferencesManager.getStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE);
        String mileage = preferencesManager.getStringValue(AppConstant.PREF_MILEAGE);

        mileageTv.setText(mileage);
        if (Double.parseDouble(total_fuel) > 0) {
            fuelReadingTv.setText(total_fuel);
        }


    }

    private void initViews() {

        fuelQtyEdtLayout = findViewById(R.id.fuelQtyEdtLayout);
        amountEdtLayout = findViewById(R.id.amountEdtLayout);
        odometerEdtLayout = findViewById(R.id.odometerEdtLayout);

        driverNameEdt = findViewById(R.id.driverNameEdt);
        busNameEdt = findViewById(R.id.busNameEdt);
        busNumberEdt = findViewById(R.id.busNumberEdt);
        fuelQtyEdt = findViewById(R.id.fuelQtyEdt);
        amountEdt = findViewById(R.id.amountEdt);
        odometerEdt = findViewById(R.id.odometerEdt);
        previousOdometerEdt = findViewById(R.id.previousOdometerEdt);

        backImageView = findViewById(R.id.backImageView);

        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        fuelReadingTv = findViewById(R.id.fuelReadingTv);
        mileageTv = findViewById(R.id.mileageTv);
    }

    private void validateFuelEntry(Fuel fuelData) {

        if (fuelData != null) {
            total_distance = fuelData.getBusReading();
            previous_reading = fuelData.getOdometer();
        } else {
            total_distance = "0";
            previous_reading = initial_odometer;
        }

        if (Integer.parseInt(current_odometer) >= Integer.parseInt(previous_reading)) {
            diff = Integer.parseInt(current_odometer) - Integer.parseInt(previous_reading);
            saveCurrentFuelEntry();
        } else {

            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);
            materialAlertDialogBuilder.setTitle("Is new reading is less than previous reading?");
            materialAlertDialogBuilder.setCancelable(false);
            materialAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    diff = Integer.parseInt(current_odometer) - Integer.parseInt(initial_odometer);
                    dialog.dismiss();
                    saveCurrentFuelEntry();
                }
            });
            materialAlertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    saveBtn.setEnabled(true);
                }
            });
            materialAlertDialogBuilder.show();

        }
    }

    private void saveCurrentFuelEntry() {

        progressDialog = new ProgressDialog(FuelEntryActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting the fuel data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (diff >= 0) {

            total_distance = String.valueOf(Integer.parseInt(total_distance) + diff);

            currentFuelData = new Fuel(amount, bus_name, bus_number, total_distance, date, driver_id, driver_name, fuel_qty, current_odometer, previous_reading);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("fuel_tb");
            String fuel_key = databaseReference.push().getKey();
            databaseReference.child(institute_key).child(bus_key).child(fuel_key).setValue(currentFuelData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    callNextScreen();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    saveBtn.setEnabled(true);
                    Toast.makeText(FuelEntryActivity.this, "Data submit failed..", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            progressDialog.dismiss();
            saveBtn.setEnabled(true);
            odometerEdtLayout.setError("Enter valid odometer.");
            // Toast.makeText(this, "Enter valid odometer", Toast.LENGTH_SHORT).show();
        }

    }

    private void callNextScreen() {


        double mileage = 0;
        if(initial_distance>0) {
             mileage = Math.round((((Double.parseDouble(total_distance) - initial_distance) / total_fuel) * 100.0) / 100.0);
        }
        pref_total_fuel = String.valueOf(total_fuel + Double.parseDouble(fuel_qty));
        pref_total_distance = total_distance;

        preferencesManager.setStringValue(AppConstant.PREF_TOTAL_FUEL, pref_total_fuel);
        preferencesManager.setStringValue(AppConstant.PREF_MILEAGE, String.valueOf(mileage));
        preferencesManager.setStringValue(AppConstant.PREF_BUS_TOTAL_DISTANCE, pref_total_distance);
        preferencesManager.setStringValue(AppConstant.PREF_BUS_CURRENT_ODOMETER, current_odometer);

        if (!preferencesManager.getBooleanValue(AppConstant.PREF_IS_DRIVING)) {

            //update fueldata in Bustrack table.
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.PREF_STR_BUSTRACKING_TB);
            String institute_key = preferencesManager.getStringValue(AppConstant.PREF_DRIVER_INSTITUTE_KEY);
            String bus_key = preferencesManager.getStringValue(AppConstant.PREF_BUS_TRACKING_KEY);

            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put(AppConstant.PREF_STR_TOTAL_FUEL, pref_total_fuel);
            objectMap.put(AppConstant.PREF_STR_TOTAL_DISTANCE, pref_total_distance);
            objectMap.put(AppConstant.PREF_MILEAGE, String.valueOf(mileage));

            databaseReference.child(institute_key).child(bus_key).updateChildren(objectMap);

        }
        progressDialog.dismiss();
        saveBtn.setEnabled(true);
        Toast.makeText(FuelEntryActivity.this, "Data submitted..", Toast.LENGTH_SHORT).show();
        onBackPressed();

    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            getLastFuelReading();
        }

    }
}
