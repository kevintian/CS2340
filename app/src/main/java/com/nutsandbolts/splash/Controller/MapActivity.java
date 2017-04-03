package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

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
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.R;

/**
 * This activity will display a map showing the locations of nearby water sources
 *
 * @author Deb Banerji
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private WaterSourceReport waterSourceReport;

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
        FragmentManager fragManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fragManager.findFragmentById(R.id.map);
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

        // Add move the camera to Georgia Tech
        final LatLng GT_LOCATION = new LatLng(33.7756, -84.3963);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(GT_LOCATION));

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

        FirebaseDatabase splashDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootNode = splashDatabase.getReference();
        mWaterSourceReportsRef = rootNode.child("water-source-reports");
        mWaterSourceReportsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Add to arraylist
                waterSourceReport =
                        WaterSourceReport.buildWaterSourceReportFromSnapShot(dataSnapshot);
                double latitude = waterSourceReport.getLatitude();
                double longitude = waterSourceReport.getLongitude();
                LatLng waterSourceReportPosition = new LatLng(latitude, longitude);

                //Create the marketOptions object and customize it
                MarkerOptions options = new MarkerOptions();
                options = options.position(waterSourceReportPosition);
                options = options.title(waterSourceReport.getWaterType()
                        + ", " + waterSourceReport.getWaterCondition());
                mMap.addMarker(options);
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(waterSourceReportPosition));
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
