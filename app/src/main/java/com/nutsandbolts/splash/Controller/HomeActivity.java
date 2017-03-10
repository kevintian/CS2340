package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nutsandbolts.splash.Model.WaterCondition;
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {


    /*
    Widgets we will need to define listeners for
    */
    private Button signOutButton;
    private Button editProfileButton;
    private Button submitWaterReportButton;
    private Button viewWaterReportsButton;
    private Button submitQualityReportButton;
    private Button viewQualityReportsButton;
    private Button viewMapButton;


    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        signOutButton = (Button) findViewById(R.id.sign_out_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                returnToWelcomeScreen();
            }
        });

        editProfileButton = (Button) findViewById(R.id.edit_profile_button);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        submitWaterReportButton = (Button) findViewById(R.id.submit_water_report_button);

        submitWaterReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWaterReport();
            }
        });

        viewWaterReportsButton = (Button) findViewById(R.id.view_water_reports_button);

        viewWaterReportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewWaterReports();
            }
        });

        submitQualityReportButton = (Button) findViewById(R.id.submit_quality_report_button);

        submitQualityReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWaterQualityReport();
            }
        });

        viewQualityReportsButton = (Button) findViewById(R.id.view_quality_reports_button);

        viewQualityReportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewWaterQualityReports();
            }
        });

        viewMapButton = (Button) findViewById(R.id.view_map_button);

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMap();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // RegisteredUser is signed in
                    Log.d("Authentication", "onAuthStateChanged:signed_in:"
                        + user.getUid());
                    returnToWelcomeScreen();
                } else {
                    // RegisteredUser is signed out
                    Log.d("Authentication", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    /**
     * Brings user back to welcome screen
     */
    private void returnToWelcomeScreen() {
        Intent welcomeIntent  = new Intent(HomeActivity
            .this, WelcomeActivity.class);
        startActivity(welcomeIntent);
    }

    /**
     * Brings user to submit water source report screen
     */
    private void submitWaterReport() {
        Intent submitWaterReportIntent = new Intent(HomeActivity
                .this, SubmitWaterReportActivity.class);
        startActivity(submitWaterReportIntent);
    }

    /**
     * Brings user to water source reports overview screen
     */
    private void viewWaterReports() {
        Intent viewWaterReportsIntent = new Intent(HomeActivity
                .this, ViewWaterReportsActivity.class);
        startActivity(viewWaterReportsIntent);
    }

    /**
     * Brings user to submit water quality report screen
     */
    private void submitWaterQualityReport() {
        Intent submitWaterQualityReportIntent = new Intent(HomeActivity
                .this, SubmitQualityReportActivity.class);
        startActivity(submitWaterQualityReportIntent);
    }

    /**
     * Brings user to water quality reports overview screen
     */
    private void viewWaterQualityReports() {
        Intent viewWaterQualityReportsIntent = new Intent(HomeActivity
                .this, ViewQualityReportActivity.class);
        startActivity(viewWaterQualityReportsIntent);
    }

    /**
     * Brings user back to edit screen
     */
    private void editProfile() {
        Intent editIntent = new Intent(HomeActivity.this, EditProfileActivity.class);
        editIntent.putExtra("isEdit", true);
        startActivity(editIntent);
    }

    /**
     * Brings user back to map
     */
    private void viewMap() {
        Intent viewMapIntent = new Intent(HomeActivity
                .this, MapActivity.class);
        startActivity(viewMapIntent);
    }
}
