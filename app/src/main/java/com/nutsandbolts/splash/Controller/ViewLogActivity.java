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
import com.nutsandbolts.splash.Model.SecurityLogEntry;
import com.nutsandbolts.splash.Model.SecurityLogType;
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity to display the Security Log
 */
public class ViewLogActivity extends AppCompatActivity {

    private DatabaseReference mWaterSourceReportsRef;
    private ChildEventListener mWaterSourceReportsListener;
    private ArrayAdapter<SecurityLogEntry> mArrayAdapter;

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
        mWaterSourceReportsRef = fireBaseRef.child("security-log");
        final ListView listView = (ListView) findViewById(R.id.log_list);
        final ArrayList<SecurityLogEntry> reports = new ArrayList<>();

        mArrayAdapter = new LogArrayAdapter(ViewLogActivity.this, 0, reports);
        listView.setAdapter(mArrayAdapter);

        mWaterSourceReportsListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                SecurityLogEntry logEntry = new SecurityLogEntry(dataSnapshot);
                reports.add(logEntry);
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
    private final class LogArrayAdapter extends ArrayAdapter<SecurityLogEntry> {
        final Context context;
        final List<SecurityLogEntry> logs;

        /**
         * Constructor, called on creation
         *
         * @param context  An Context object of current state of the application/object
         * @param resource An int representation of the amount of water sources
         * @param objects  An Array List of all of the Water Source reports
         */
        private LogArrayAdapter(Context context, int resource,
                                List<SecurityLogEntry> objects) {
            //We keep resource as a variable for clarity's sake and for future code edits
            super(context, resource, objects);

            this.context = context;
            this.logs = objects;
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
            SecurityLogEntry logEntry = logs.get(position);

            //Get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.log_entry_layout, null);

            TextView logType = (TextView) view.findViewById(R.id.log_type);
            TextView logTimestamp = (TextView) view.findViewById(R.id.log_timestamp);
            TextView logUserUID = (TextView) view.findViewById(R.id.log_user_uid);
            TextView logDetails = (TextView) view.findViewById(R.id.log_details);

            //Set text views
            logType.setText(logEntry.getTypeString());
            logTimestamp.setText(logEntry.getTimestampString());
            logUserUID.setText(logEntry.getUserUID());
            logDetails.setText(logEntry.getDetails());

            return view;
        }
    }
}
