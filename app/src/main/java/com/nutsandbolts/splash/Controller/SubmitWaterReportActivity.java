package com.nutsandbolts.splash.Controller;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nutsandbolts.splash.Model.UserType;
import com.nutsandbolts.splash.Model.WaterCondition;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

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
    private Location submittedLocation;
    private WaterType waterType;
    private WaterCondition waterCondition;

    /*
    Variables to hold data generate by GPS
     */
    private Location currentLocation;
    private boolean signalFound;


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
        submitButton = (Button) findViewById(R.id.submit_water_report_button);

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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
