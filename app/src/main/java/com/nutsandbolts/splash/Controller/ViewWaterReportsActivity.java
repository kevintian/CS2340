package com.nutsandbolts.splash.Controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
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
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This shows the water reports list
 *
 * @author Jinni Xia
 */
public class ViewWaterReportsActivity extends AppCompatActivity {

    private DatabaseReference mWaterSourceReportsRef;
    private ChildEventListener mWaterSourceReportsListener;
    private ArrayAdapter<WaterSourceReport> mArrayAdapter;

    /**
     * looks for when back is pressed
     * Code inspection
     */
    @Override
    public void onBackPressed() {
        mWaterSourceReportsRef.removeEventListener(mWaterSourceReportsListener);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_water_reports);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference fireBaseRef = db.getReference();
        mWaterSourceReportsRef = fireBaseRef.child("water-source-reports");
        final ListView listView = (ListView) findViewById(R.id.report_list);
        final ArrayList<WaterSourceReport> reports = new ArrayList<>();

        mArrayAdapter = new WaterSourceArrayAdapter(ViewWaterReportsActivity.this, 0, reports);
        listView.setAdapter(mArrayAdapter);

        mWaterSourceReportsListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                WaterSourceReport waterSourceReport =
                        WaterSourceReport.buildWaterSourceReportFromSnapShot(dataSnapshot);
                reports.add(waterSourceReport);
                mArrayAdapter.notifyDataSetChanged();

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
        mWaterSourceReportsRef.addChildEventListener(mWaterSourceReportsListener);

    }


    //Create a custom array adapter class to use the xml layout we created
    private final class WaterSourceArrayAdapter extends ArrayAdapter<WaterSourceReport> {
        final Context context;
        final List<WaterSourceReport> sourceReports;

        /**
         * Constructor, called on creation
         *
         * @param context  An Context object of current state of the application/object
         * @param resource An int representation of the amount of water sources
         * @param objects  An Array List of all of the Water Source reports
         */
        private WaterSourceArrayAdapter(Context context, int resource,
                                       List<WaterSourceReport> objects) {
            //We keep resource as a variable for clarity's sake and for future code edits
            super(context, resource, objects);

            this.context = context;
            this.sourceReports = objects;
        }


        /**
         * Called when rendering the list, gets the View displayed
         *
         * @param position    int representation of which report in the source reports
         * @param convertView a View used to convert display
         * @param parents     a ViewGroup that contains other views
         * @return view
         */
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parents) {
            //Get the report we are displaying
            WaterSourceReport report = sourceReports.get(position);

            //Get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.water_source_report_layout, null);

            TextView reportID = (TextView) view.findViewById(R.id.report_id);
            TextView reporterName = (TextView) view.findViewById(R.id.reporter_name);
            TextView waterType = (TextView) view.findViewById(R.id.water_type);
            TextView waterCondition = (TextView) view.findViewById(R.id.water_condition);
            TextView location = (TextView) view.findViewById(R.id.location);

            //Create the location text
            //Four decimal place is accurate up to 11m -> used to identify parcels of land
            DecimalFormat twoDecimalPlaces = new DecimalFormat("#.0000");
            String loc = twoDecimalPlaces.format(report.getLatitude())
                    + ",\n" + twoDecimalPlaces.format(report.getLongitude());

            //Set text views
            reportID.setText("Report " + report.getReportID());
            reporterName.setText("Reported by: " + report.getReporterName());
            waterType.setText("Water Type: " + report.getWaterType());
            waterCondition.setText("Water Condition: " + report.getWaterCondition());
            location.setText(loc);

            return view;
        }
    }
}
