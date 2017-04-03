package com.nutsandbolts.splash.Controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nutsandbolts.splash.Model.WaterQualityReport;
import com.nutsandbolts.splash.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for view quality reports screen
 */
public class ViewQualityReportActivity extends AppCompatActivity {

    private DatabaseReference mWaterQualityReportsRef;
    private ChildEventListener mWaterQualityReportsListener;
    private ArrayAdapter<WaterQualityReport> mArrayAdapter;

    @Override
    public void onBackPressed() {
        mWaterQualityReportsRef.removeEventListener(mWaterQualityReportsListener);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_water_reports);


        final FirebaseDatabase instance = FirebaseDatabase.getInstance();
        final DatabaseReference reference = instance.getReference();
        mWaterQualityReportsRef = reference.child("water-quality-reports");
        final ListView listView = (ListView) findViewById(R.id.report_list);
        final ArrayList<WaterQualityReport> reports = new ArrayList<WaterQualityReport>();

        mArrayAdapter = new WaterQualityArrayAdapter(ViewQualityReportActivity.this, 0, reports);
        listView.setAdapter(mArrayAdapter);

        mWaterQualityReportsListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                try {
                WaterQualityReport waterQualityReport = WaterQualityReport
                        .buildWaterQualityReportFromSnapShot(dataSnapshot);
                reports.add(waterQualityReport);
                mArrayAdapter.notifyDataSetChanged();
//                } catch (NullPointerException e) {
//                    Log.d("Datasnapshot error", dataSnapshot.toString());
//                }
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
        mWaterQualityReportsRef.addChildEventListener(mWaterQualityReportsListener);

    }


    //Create a custom array adapter class to use the xml layout we created
    private class WaterQualityArrayAdapter extends ArrayAdapter<WaterQualityReport> {
        private final Context context;
        private final List<WaterQualityReport> qualityReports;

        /**
         * Constructor, called on creation
         *
         * @param context  An Context object of current state of the application/object
         * @param resource An int representation of the amount of water quality reports
         * @param objects  An Array List of all of the Water quality reports
         */
        WaterQualityArrayAdapter(Context context, int resource,
                                 List<WaterQualityReport> objects) {
            super(context, resource, objects);

            this.context = context;
            this.qualityReports = objects;
        }


        /**
         * Called when rendering the list, gets the View displayed
         *
         * @param position    int representation of which report in the quality reports
         * @param convertView a View used to convert display
         * @param parents     a ViewGroup that contains other views
         * @return view
         */
        public View getView(int position, View convertView, @NonNull ViewGroup parents) {
            //Get the report we are displaying
            WaterQualityReport report = qualityReports.get(position);

            //Get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity
                    .LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.water_quality_report_layout, null);

            TextView reportID = (TextView) view.findViewById(R.id.report_id);
            TextView reporterName = (TextView) view.findViewById(R.id.reporter_name);
            TextView waterQuality = (TextView) view.findViewById(R.id.water_quality);
            TextView virusPPM = (TextView) view.findViewById(R.id.virusPPM);
            TextView contaminantPPM = (TextView) view.findViewById(R.id.contaminantPPM);
            TextView location = (TextView) view.findViewById(R.id.location);

            //Create the location text
            //Four decimal place is accurate up to 11m -> used to identify parcels of land
            DecimalFormat twoDecimalPlaces = new DecimalFormat("#.0000");
            String loc = twoDecimalPlaces.format(report.getLatitude())
                    + ",\n" + twoDecimalPlaces.format(report.getLongitude());

            //Set textviews
            reportID.setText("Report " + report.getReportID());
            reporterName.setText("Reported by: " + report.getReporterName());
            waterQuality.setText("Water Type: " + report.getWaterQuality().toString());
            virusPPM.setText("Virus PPM: " + report.getVirusPPM());
            contaminantPPM.setText("Contaminant PPM: " + report.getContaminantPPM());

            location.setText(loc);

            return view;
        }
    }
}
