package com.nutsandbolts.splash.Controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nutsandbolts.splash.Model.SecurityLogEntry;
import com.nutsandbolts.splash.Model.SecurityLogType;
import com.nutsandbolts.splash.Model.WaterCondition;
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

import java.util.Date;
import java.util.Locale;

import static junit.framework.Assert.assertNotNull;

/**
 * Controller class for submit water report screen
 */
public class SubmitWaterReportActivity extends AppCompatActivity implements LocationListener {

    private static final int SDK_TWENTY_THREE = 23;
    private static final int MIN_TIME_INTERVAL = 500;
    /*
           Widgets we will need to define listeners for
           */
    private EditText latitudeText;
    private EditText longitudeText;
    private Spinner waterTypeSpinner;
    private Spinner waterConditionSpinner;


    /*
    Variables for location, water type and water condition of report
     */
    private double latitude;
    private double longitude;
    private WaterType waterType;
    private WaterCondition waterCondition;

    /*
    WaterSourceReport model
     */
    private WaterSourceReport waterSourceReport;

    /*
    Variables to enable GPS functionality
     */
    private boolean signalFound;
    private double gpsLatitude;
    private double gpsLongitude;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int INITIAL_REQUEST = 1337;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_water_report);


        /*
        Request GPS Permissions
         */
        requestPermissions();

        /*
        Get widgets from view
         */
        latitudeText = (EditText) findViewById(R.id.water_source_latitude_edit_text);
        longitudeText = (EditText) findViewById(R.id.water_source_longitude_edit_text);
        Button gpsButton = (Button) findViewById(R.id.generate_from_gps_button);
        waterTypeSpinner = (Spinner) findViewById(R.id.water_source_type_spinner);
        waterConditionSpinner = (Spinner) findViewById(R.id.water_source_condition_spinner);
        Button submitButton = (Button) findViewById(R.id.upload_water_report_button);

        /*
        Get latitude and longitude if report was created from MapActivity
         */
        Intent intent = this.getIntent();

        if (intent != null) {
            latitude = intent.getDoubleExtra("latitude", 0);
            latitudeText.setText(String.format(Locale.getDefault(), "%1$,f", latitude));
            longitude = intent.getDoubleExtra("longitude", 0);
            longitudeText.setText(String.format(Locale.getDefault(), "%1$,f", longitude));
        }

        /*
          Set up the adapter to display the allowable water types in the spinner
         */
        ArrayAdapter<WaterType> waterTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, WaterType.values());
        waterTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterTypeSpinner.setAdapter(waterTypeAdapter);

        /*
          Set up the adapter to display the allowable water conditions in the spinner
         */
        ArrayAdapter<WaterCondition> waterConditionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, WaterCondition.values());
        waterConditionAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        waterConditionSpinner.setAdapter(waterConditionAdapter);

        /*
        Get RegisteredUser Data
         */
        final FirebaseAuth instance = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = instance.getCurrentUser();

        Date date = new Date(System.currentTimeMillis());
        assertNotNull(firebaseUser);
        waterSourceReport = new WaterSourceReport(date, System.currentTimeMillis(),
                firebaseUser.getDisplayName(), firebaseUser.getUid(),
                latitude, longitude, waterType, waterCondition);

        setUpButtons(gpsButton, submitButton);

        // setting up LocationManager
        setUpLocationManager();
    }

    private void setUpLocationManager() {
        LocationManager locationManager = (LocationManager) getSystemService(Context
                .LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_INTERVAL,   // Interval in milliseconds
                    10, this);
        } catch (SecurityException e) {
            final Toast toast = Toast.makeText(getBaseContext(), "Security exception: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void setUpButtons(Button gpsButton, Button submitButton) {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Editable latitudeString = latitudeText.getText();
                    latitude = Double.parseDouble(latitudeString.toString());
                    final Editable longitudeString = longitudeText.getText();
                    longitude = Double.parseDouble(longitudeString.toString());
                } catch (NumberFormatException e) {
                    final Toast toast = Toast.makeText(getApplicationContext(),
                            "Enter Valid Latitude and Longitude", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } catch (IllegalArgumentException e2) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Latitude or Longitude is out of range.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                submitWaterReport();
                Intent homeIntent = new Intent(SubmitWaterReportActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signalFound) {
                    latitude = gpsLatitude;
                    longitude = gpsLongitude;
                    latitudeText.setText(String.format(Locale.getDefault(), "%1$,f", latitude));
                    longitudeText.setText(String.format(Locale.getDefault(), "%1$,f", longitude));
                } else {
                    final Toast toast = Toast.makeText(getApplicationContext(),
                            "GPS Signal Not Found", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions() {
        if ((Build.VERSION.SDK_INT >= SDK_TWENTY_THREE) && (PackageManager.PERMISSION_GRANTED
                != checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }
    }

    /**
     * Validates data required for a water source report and submits it to the database.
     *
     * @throws IllegalArgumentException if latitude or longitude is out of range.
     * @throws NumberFormatException    if latitude or longitude are not in proper format.
     */
    private void submitWaterReport() throws IllegalArgumentException {
        Editable latEditable = latitudeText.getText();
        Editable longEditable = longitudeText.getText();
        latitude = Double.parseDouble(latEditable.toString());
        longitude = Double.parseDouble(longEditable.toString());

        waterType = (WaterType) waterTypeSpinner.getSelectedItem();
        waterCondition = (WaterCondition) waterConditionSpinner.getSelectedItem();

        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        final FirebaseAuth instance = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = instance.getCurrentUser();
        assertNotNull(firebaseUser);
        waterSourceReport = new WaterSourceReport(date, currentTime, firebaseUser.getDisplayName(),
                firebaseUser.getUid(), latitude, longitude, waterType, waterCondition);
        SecurityLogEntry logEntry = new SecurityLogEntry(
                new Date(System.currentTimeMillis()),
                SecurityLogType.CREATE_REPORT,
                firebaseUser.getUid(),
                "Succesful water source report submission.");
        if (!waterSourceReport.isValidLocation()) {
            logEntry.setDetails("Invalid location entered by user.");
            throw new IllegalArgumentException("Latitude or Longitude is out of range.");
        }
        waterSourceReport.writeToDatabase();
        logEntry.writeToDatabase();
    }

    @Override
    public void onLocationChanged(Location location) {
        gpsLatitude = location.getLatitude();
        gpsLongitude = location.getLongitude();
        signalFound = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        final Toast toast = Toast.makeText(getBaseContext(), "Gps turned on", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        final Toast toast = Toast.makeText(getBaseContext(), "Gps turned off", Toast.LENGTH_SHORT);
        toast.show();
    }
}
