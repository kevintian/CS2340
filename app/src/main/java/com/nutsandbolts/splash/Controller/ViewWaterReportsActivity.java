package com.nutsandbolts.splash.Controller;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nutsandbolts.splash.Model.UserType;
import com.nutsandbolts.splash.Model.WaterCondition;
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewWaterReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_water_reports);


        DatabaseReference mWaterSourceReportsRef = FirebaseDatabase.getInstance().getReference().child("water-source-reports");
        final ListView listView = (ListView) findViewById(R.id.report_list);
        final ArrayList<WaterSourceReport> reports = new ArrayList<WaterSourceReport>();
        mWaterSourceReportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot report : dataSnapshot.getChildren()) {

                    //Get current report information
                    Date date = new Date((long) report.child("date-time").getValue());
                    String reporterName = (String) report.child("reporter-name").getValue();
                    long reportID = (long) report.child("report-id").getValue();
                    /*Firebase saves nondecimal numbers as longs automatically, so use method
                     to convert the number if it is a long*/
                    double latitude = convertDouble(report.child("latitude").getValue());
                    double longitude = convertDouble(report.child("longitude").getValue());
                    WaterType type = WaterType.valueOf((String)report.child("water-type").getValue());
                    WaterCondition condition = WaterCondition.valueOf((String)report.child("water-condition").getValue());

                    //Add to arraylist
                    reports.add(new WaterSourceReport(date, reporterName, reportID, latitude, longitude, type, condition));
                }

                ArrayAdapter<WaterSourceReport> arrayAdapter = new waterSourceArrayAdapter(ViewWaterReportsActivity.this, 0, reports);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Checks if the value is a long -> if so, converts to double
     * 
     * @param longValue - an object that is thought to be a long
     * @return double A double that is converted form the long param
     */
    private double convertDouble(Object longValue){
        double valueTwo = -1; // whatever to state invalid!

        if(longValue instanceof Long) {
            valueTwo = ((Long) longValue).doubleValue();
        } else if (longValue instanceof  Double) {
            valueTwo = (double) longValue;
        }

        return valueTwo;
    }

    //Create a custom array adapter class to use the xml layout we created
    private class waterSourceArrayAdapter extends ArrayAdapter<WaterSourceReport> {
        private Context context;
        private List<WaterSourceReport> sourceReports;

        /**
         * Constructor, called on creation
         *
         * @param context An Context object of current state of the application/object
         * @param resource An int representation of the amount of water sources
         * @param objects An Array List of all of the Water Source reports
         */
        public waterSourceArrayAdapter(Context context, int resource,
                                       ArrayList<WaterSourceReport> objects) {
            super(context, resource, objects);

            this.context = context;
            this.sourceReports = objects;
        }


        /**
         * Called when rendering the list, gets the View displayed
         *
         * @param position int representation of which report in the source reports
         * @param convertView a View used to convert display
         * @param parents a ViewGroup that contains other views
         */
        public View getView(int position, View convertView, ViewGroup parents) {
            //Get the report we are displaying
            WaterSourceReport report = sourceReports.get(position);

            //Get the inflater and inflate the XML layout or each item
            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.water_source_report_layout, null);

            TextView reportID = (TextView) view.findViewById(R.id.report_id);
            TextView reporterName = (TextView) view.findViewById(R.id.reporter_name);
            TextView waterType = (TextView) view.findViewById(R.id.water_type);
            TextView waterCondition = (TextView) view.findViewById(R.id.water_condition);
            TextView location = (TextView) view.findViewById(R.id.location);

            //Create the location text
            String loc = report.getLatitude() + ",\n" + report.getLongitude();

            //Set textviews
            reportID.setText("Report " + report.getReportID());
            reporterName.setText("Reported by: " + report.getReporterName());
            waterType.setText("Water Type: " + report.getWaterType().toString());
            waterCondition.setText("Water Condition: " + report.getWaterCondition().toString());
            location.setText(loc);

            return view;
        }
    }
}
