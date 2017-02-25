package com.nutsandbolts.splash.Controller;

import android.content.Intent;
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
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

import java.util.Date;

public class SubmitWaterReportActivity extends AppCompatActivity {

    /*
   Widgets we will need to define listeners for
   */
    private EditText latitudeText;
    private EditText longitudeText;
    private Button gpsButton;
    private Spinner waterTypeSpinner;
    private Spinner waterConditionSpinner;
    private Button submitButton;


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
    Flag to check if GPS signal was found
     */
    private boolean signalFound;
    private double gpsLongitude;
    private double gpsLongtide;

    /*
    FirebaseUser object to get credentials of user when submitting
     */
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_water_report);

        /*
        Get widgets from view
         */
        latitudeText = (EditText) findViewById(R.id.water_source_latitude_edit_text);
        longitudeText = (EditText) findViewById(R.id.water_source_longitude_edit_text);
        gpsButton = (Button) findViewById(R.id.generate_from_gps_button);
        waterTypeSpinner = (Spinner) findViewById(R.id.water_source_type_spinner);
        waterConditionSpinner = (Spinner) findViewById(R.id.water_source_condition_spinner);
        submitButton = (Button) findViewById(R.id.upload_water_report_button);

        /*
          Set up the adapter to display the allowable water types in the spinner
         */
        ArrayAdapter<String> waterTypeAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, WaterType.values());
        waterTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterTypeSpinner.setAdapter(waterTypeAdapter);

        /*
          Set up the adapter to display the allowable water conditions in the spinner
         */
        ArrayAdapter<String> waterConditionAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, WaterCondition.values());
        waterConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterConditionSpinner.setAdapter(waterConditionAdapter);

        /*
        Get RegisteredUser Data
         */
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Date date = new Date(System.currentTimeMillis());
        waterSourceReport = new WaterSourceReport(date, firebaseUser.getDisplayName(), firebaseUser.getUid(), latitude, longitude, waterType, waterCondition);

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

                waterType = (WaterType) waterTypeSpinner.getSelectedItem();
                waterCondition = (WaterCondition) waterConditionSpinner.getSelectedItem();

                Date date = new Date(System.currentTimeMillis());
                //TODO: replace firebaseUser.getDisplayName() with user's chosen values
                waterSourceReport.setDateTime(date);
                waterSourceReport.setLatitude(latitude);
                waterSourceReport.setLongitude(longitude);
                waterSourceReport.setWaterCondition(waterCondition);
                waterSourceReport.setWaterType(waterType);
                waterSourceReport.writeToDatabase();
                Intent homeIntent = new Intent(SubmitWaterReportActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}
