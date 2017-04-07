package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nutsandbolts.splash.R;

/**
 * This activity shows up after signing in and sets up the interface for users to
 * navigate the app
 *
 * @author Jinni Xia
 */
public class HomeActivity extends AppCompatActivity {


    /*
    Widgets we will need to define listeners for
    */
    private ImageView submitQualityReportIcon;
    private ImageView viewQualityReportsIcon;
    private ImageView viewGraphIcon;
    private TextView submitQualityReportText;
    private TextView viewQualityReportsText;
    private TextView viewGraphText;

    //onCreate method is too long, cannot really be fixed because most of it
    // is setting up icons/text views that do something when clicked
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView signOutIcon = (ImageView) findViewById(R.id.sign_out_icon);

        signOutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                returnToWelcomeScreen();
            }
        });

        ImageView editProfileIcon = (ImageView) findViewById(R.id.edit_profile_icon);

        editProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        ImageView submitWaterReportIcon = (ImageView) findViewById(R.id.submit_water_report_icon);

        submitWaterReportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWaterReport();
            }
        });

        ImageView viewWaterReportsIcon = (ImageView) findViewById(R.id.view_water_reports_icon);

        viewWaterReportsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewWaterReports();
            }
        });

        submitQualityReportIcon = (ImageView) findViewById(R.id.submit_quality_report_icon);

        submitQualityReportText = (TextView) findViewById(R.id.submit_quality_report_text);

        submitQualityReportIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWaterQualityReport();
            }
        });

        submitQualityReportIcon.setVisibility(View.GONE);
        submitQualityReportText.setVisibility(View.GONE);

        viewQualityReportsIcon = (ImageView) findViewById(R.id.view_quality_report_icon);

        viewQualityReportsText = (TextView) findViewById(R.id.view_quality_reports_text);

        viewQualityReportsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewWaterQualityReports();
            }
        });

        viewQualityReportsIcon.setVisibility(View.GONE);
        viewQualityReportsText.setVisibility(View.GONE);

        ImageView viewMapIcon = (ImageView) findViewById(R.id.view_map_icon);

        viewMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMap();
            }
        });

        viewGraphIcon = (ImageView) findViewById(R.id.view_graph_icon);

        viewGraphText = (TextView) findViewById(R.id.view_graph_text);

        viewGraphIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGraph();
            }
        });

        viewGraphIcon.setVisibility(View.GONE);
        viewGraphText.setVisibility(View.GONE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        showQualityReportControls(firebaseUser);

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
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
     * Shows controls for water quality report if user is a worker/manager
     */
    private void showQualityReportControls(UserInfo user) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mRootRef = db.getReference();

        DatabaseReference referenceUsers = mRootRef.child("registered-users");
        DatabaseReference referenceId = referenceUsers.child(user.getUid());
        DatabaseReference mUserRef = referenceId.child("user-type");
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                changeVisibility(dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Changes display for home activity depending on user type
     * @param dataSnapshotValue value of DataSnapshot where we can get the user type from
     */
    private void changeVisibility(Object dataSnapshotValue) {
        String userType = dataSnapshotValue.toString();
//        Log.d("USERTYPE", userType);
        if ("WORKER".equals(userType) || "MANAGER".equals(userType)) {
            submitQualityReportIcon.setVisibility(View.VISIBLE);
            submitQualityReportText.setVisibility(View.VISIBLE);
        }
        if ("MANAGER".equals(userType)) {
            viewQualityReportsIcon.setVisibility(View.VISIBLE);
            viewQualityReportsText.setVisibility(View.VISIBLE);
            viewGraphIcon.setVisibility(View.VISIBLE);
            viewGraphText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Brings user back to welcome screen
     */
    private void returnToWelcomeScreen() {
        Intent welcomeIntent = new Intent(HomeActivity
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
     * Brings user to edit screen
     */
    private void editProfile() {
        Intent editIntent = new Intent(HomeActivity.this, EditProfileActivity.class);
        editIntent.putExtra("isEdit", true);
        startActivity(editIntent);
    }

    /**
     * Brings user to map
     */
    private void viewMap() {
        Intent viewMapIntent = new Intent(HomeActivity
                .this, MapActivity.class);
        startActivity(viewMapIntent);
    }

    /**
     * Brings user back to map
     */
    private void viewGraph() {
        Intent viewGraphIntent = new Intent(HomeActivity
                .this, GenerateGraphActivity.class);
        startActivity(viewGraphIntent);
    }
}
