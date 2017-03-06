package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nutsandbolts.splash.Model.WaterCondition;
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

import java.util.Date;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ChildEventListener mWaterSourceReportsChildEventListener;
    private DatabaseReference mWaterSourceReportsRef;

    @Override
    public void onBackPressed() {
        mWaterSourceReportsRef.removeEventListener(mWaterSourceReportsChildEventListener);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //        LatLng sydney = new LatLng(-34, 151);
        //        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent submitWaterReportIntent = new Intent(MapActivity
                        .this, SubmitWaterReportActivity.class);
                submitWaterReportIntent.putExtra("latitude", latLng.latitude);
                submitWaterReportIntent.putExtra("longitude", latLng.longitude);
                mWaterSourceReportsRef.removeEventListener(mWaterSourceReportsChildEventListener);
                startActivity(submitWaterReportIntent);
            }
        });

        mWaterSourceReportsRef = FirebaseDatabase.getInstance().getReference().child("water-source-reports");
        mWaterSourceReportsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Get current report information
                Date date = new Date((long) dataSnapshot.child("date-time").getValue());
                String reporterName = (String) dataSnapshot.child("reporter-name").getValue();
                long reportID = (long) dataSnapshot.child("report-id").getValue();
                /*Firebase saves nondecimal numbers as longs automatically, so use method
                     to convert the number if it is a long*/
                //TODO: Create a constructor to create WaterSourceReport from a dataSnapshot
                double latitude = ViewWaterReportsActivity.convertDouble(dataSnapshot.child("latitude").getValue());
                double longitude = ViewWaterReportsActivity.convertDouble(dataSnapshot.child("longitude").getValue());
                WaterType type = WaterType.valueOf((String) dataSnapshot.child("water-type").getValue());
                WaterCondition condition = WaterCondition.valueOf((String) dataSnapshot.child("water-condition").getValue());

                //Add to arraylist
                WaterSourceReport waterSourceReport = new WaterSourceReport(date, reporterName, reportID, latitude, longitude, type, condition);
                LatLng waterSourceReportPosition = new LatLng(waterSourceReport.getLatitude(), waterSourceReport.getLongitude());
                mMap.addMarker(new MarkerOptions().position(waterSourceReportPosition).title(waterSourceReport.getWaterType() + ", " + waterSourceReport.getWaterCondition()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(waterSourceReportPosition));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mWaterSourceReportsRef.addChildEventListener(mWaterSourceReportsChildEventListener);

    }
}
