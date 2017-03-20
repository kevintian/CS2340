package com.nutsandbolts.splash.Controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nutsandbolts.splash.Model.WaterCondition;
import com.nutsandbolts.splash.Model.WaterQuality;
import com.nutsandbolts.splash.Model.WaterQualityReport;
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

import java.util.Date;

/**
 * Activity to submit Water Quality Reports
 */
public class SubmitQualityReportActivity extends AppCompatActivity implements LocationListener {

    /*
   Widgets we will need to define listeners for
   */
    private EditText latitudeText;
    private EditText longitudeText;
    private EditText virusPPMText;
    private EditText contaminantPPMText;
    private Button gpsButton;
    private Spinner waterQualitySpinner;
    private Button submitButton;

    /*
    Water quality report variables
     */
    private double latitude;
    private double longitude;
    private WaterQuality waterQuality = WaterQuality.SAFE;
    private int virusPPM = 0;
    private int contaminantPPM = 0;

    /*
    WaterQualityReport model
     */
    private WaterQualityReport waterQualityReport;

    /*
    Variables to enable GPS functionality
     */
    private boolean signalFound;
    private double gpsLatitude;
    private double gpsLongitude;
    private LocationManager locationManager;

    /*
    FirebaseUser object to get credentials of user when submitting
     */
    private FirebaseUser firebaseUser;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int INITIAL_REQUEST = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_quality_report);

        /*
        Request GPS Permissions
         */
        if (Build.VERSION.SDK_INT >= 23 && PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        /*
        Get widgets from view
         */
        latitudeText = (EditText) findViewById(R.id.water_source_latitude_edit_text);
        longitudeText = (EditText) findViewById(R.id.water_source_longitude_edit_text);
        virusPPMText = (EditText) findViewById(R.id.water_virusPPM_edit_text);
        contaminantPPMText = (EditText) findViewById(R.id.water_contaminantPPM_edit_text);
        gpsButton = (Button) findViewById(R.id.generate_from_gps_button);
        waterQualitySpinner = (Spinner) findViewById(R.id.water_quality_spinner);
        submitButton = (Button) findViewById(R.id.upload_water_report_button);

        /*
        Get latitude and longitude if report was created from MapActivity
         */
        Intent intent = this.getIntent();

        if (intent != null) {
            latitude = intent.getDoubleExtra("latitude", 0);
            latitudeText.setText(Double.toString(latitude));
            longitude = intent.getDoubleExtra("longitude", 0);
            longitudeText.setText(Double.toString(longitude));
        }

        /*
          Set up the adapter to display the allowable water types in the spinner
         */
        ArrayAdapter<String> waterQualityAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, WaterQuality.values());
        waterQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterQualitySpinner.setAdapter(waterQualityAdapter);

        /*
        Get RegisteredUser Data
         */
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Date date = new Date(System.currentTimeMillis());
        waterQualityReport = new WaterQualityReport(date, System.currentTimeMillis(), firebaseUser.getDisplayName(), firebaseUser.getUid(), latitude, longitude, virusPPM, contaminantPPM, waterQuality);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    latitude = Double.parseDouble(latitudeText.getText().toString());
                    longitude = Double.parseDouble(longitudeText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(),
                            "Enter Valid Latitude and Longitude", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    virusPPM = Integer.parseInt(virusPPMText.getText().toString());
                    contaminantPPM = Integer.parseInt(contaminantPPMText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(),
                            "Invalid PPM", Toast.LENGTH_SHORT).show();
                    return;
                }

                waterQuality = (WaterQuality) waterQualitySpinner.getSelectedItem();

                waterQualityReport.setLatitude(latitude);
                waterQualityReport.setLongitude(longitude);
                waterQualityReport.setWaterQuality(waterQuality);
                waterQualityReport.setVirusPPM(virusPPM);
                waterQualityReport.setContaminantPPM(contaminantPPM);
                waterQualityReport.writeToDatabase();
                Intent homeIntent = new Intent(SubmitQualityReportActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signalFound) {
                    latitude = gpsLatitude;
                    longitude = gpsLongitude;
                    latitudeText.setText(Double.toString(latitude));
                    longitudeText.setText(Double.toString(longitude));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "GPS Signal Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //getting setting up LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    500,   // Interval in milliseconds
                    10, this);
        } catch (SecurityException e) {
            //            Toast.makeText(getBaseContext(), "Security exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        Toast.makeText(getBaseContext(), "GPS turned on", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "GPS turned off", Toast.LENGTH_SHORT).show();
    }
}
