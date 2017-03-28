package com.nutsandbolts.splash.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nutsandbolts.splash.Model.WaterQuality;
import com.nutsandbolts.splash.Model.WaterType;
import com.nutsandbolts.splash.R;

import java.util.ArrayList;


public class GenerateGraphActivity extends AppCompatActivity {

    /*
   Widgets we will need to define listeners for
   */
    private Spinner pollutionTypeSpinner;
    private EditText yearText;
    private EditText longitudeText;
    private EditText latitudeText;
    private Button generateButton;
    private GraphView graph;

    /*
    Water quality report variables
     */
    private String pollutionType;
    private int year;
    private double latitude;
    private double longitude;
    private int contaminantPPM = 0;


    private DatabaseReference mWaterQualityReportsRef;
    private ChildEventListener mWaterQualityReportsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_graph);

        /*
        Get widgets from view
         */
        pollutionTypeSpinner = (Spinner) findViewById(R.id.pollution_type_spinner);
        yearText = (EditText) findViewById(R.id.graph_year_text);
        latitudeText = (EditText) findViewById(R.id.graph_latitude_text);
        longitudeText = (EditText) findViewById(R.id.graph_longitude_text);
        generateButton = (Button) findViewById(R.id.generate_graph);
        graph = (GraphView) findViewById(R.id.graph);

        /*
          Set up the adapter to display the possible contaminant types in the spinner
         */
        String[] pollutionTypes = {"Virus", "Contaminant"};
        ArrayAdapter<String> pollutionTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pollutionTypes);
        pollutionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pollutionTypeSpinner.setAdapter(pollutionTypeAdapter);


        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Get info from widgets
                */
                try {
                    year = Integer.parseInt(yearText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(),
                            "Enter Valid Year", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    latitude = Double.parseDouble(latitudeText.getText().toString());
                    longitude = Double.parseDouble(longitudeText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(),
                            "Enter Valid Latitude and Longitude", Toast.LENGTH_SHORT).show();
                    return;
                }
                pollutionType = (String) pollutionTypeSpinner.getSelectedItem();


                LineGraphSeries<DataPoint> graphPoints = generateDataPoints(pollutionType, year, latitude, longitude);
                drawGraph(graphPoints);
            }
        });
    }

    private LineGraphSeries<DataPoint> generateDataPoints(String pollutionType, int year, double latitude, double longitude) {
        mWaterQualityReportsRef = FirebaseDatabase.getInstance().getReference().child("water-quality-reports");
        mWaterQualityReportsListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {

                } catch (NullPointerException e) {
                    Log.d("Datasnapshot error", dataSnapshot.toString());
                }
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

        return new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(5, 7),
                new DataPoint(6, 6),
                new DataPoint(7, 7),
                new DataPoint(8, 6),
                new DataPoint(9, 7),
                new DataPoint(10, 6),
                new DataPoint(11, 7),
                new DataPoint(12, 6),
        });
    }

    private void drawGraph(LineGraphSeries<DataPoint> series) {
        //Make the graph visible
        graph.setVisibility(View.VISIBLE);


        GridLabelRenderer glr = graph.getGridLabelRenderer();

        //Set the x and y axis labels
        glr.setVerticalAxisTitle("PPM (Parts per million)");
        glr.setHorizontalAxisTitle("Month");

        //This ensures that increments are by 1
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(5);
        glr.setNumHorizontalLabels(5);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling

        // custom label formatter to show months
        glr.setLabelFormatter(new DefaultLabelFormatter() {
            String[] months = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun."
                    , "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show months instead of x
                    return months[(int) value - 1];
                } else {
                    // show normal y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        //Add in data
        graph.addSeries(series);
    }
}
