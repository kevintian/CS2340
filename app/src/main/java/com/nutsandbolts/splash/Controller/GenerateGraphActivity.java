package com.nutsandbolts.splash.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nutsandbolts.splash.Model.WaterQualityReport;
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
    private Spinner locationRadiusSpinner;
    private Button generateButton;
    private GraphView graph;

    /*
    Water quality report variables
    */
    private String pollutionType;
    private int year;
    private double latitude;
    private double longitude;
    private double radius;


    private DatabaseReference mWaterQualityReportsRef;
    private ValueEventListener mWaterQualityReportsListener;

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
        locationRadiusSpinner = (Spinner) findViewById(R.id.location_radius_spinner);
        graph = (GraphView) findViewById(R.id.graph);

        /*
          Set up adapters to display the possible contaminant types in the  and possible radiuses
         */
        String[] pollutionTypes = {"Virus", "Contaminant"};
        ArrayAdapter<String> pollutionTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pollutionTypes);
        pollutionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pollutionTypeSpinner.setAdapter(pollutionTypeAdapter);

        String[] radiusDistances = {"10 miles", "25 miles", "50 miles"};
        final ArrayAdapter<String> locationRadiusAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, radiusDistances);
        locationRadiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationRadiusSpinner.setAdapter(locationRadiusAdapter);

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
                String radiusSelection = (String) locationRadiusSpinner.getSelectedItem();
                //Replaces all non digits with empty string, leaving only the int value
                radius = Double.parseDouble(radiusSelection.replaceAll("[\\D]", ""));


                generateGraph();
            }
        });
    }

    private void generateGraph() {
        mWaterQualityReportsRef = FirebaseDatabase.getInstance().getReference().child("water-quality-reports");
        final ArrayList<WaterQualityReport> allReports = new ArrayList<>();

        mWaterQualityReportsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot waterReportChildren : dataSnapshot.getChildren()) {
                    WaterQualityReport report = WaterQualityReport.buildWaterQualityReportFromSnapShot(waterReportChildren);
                    allReports.add(report);
                }

                double[] monthlyValues = parseReports(allReports);
                drawGraph(new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(1, monthlyValues[0]),
                        new DataPoint(2, monthlyValues[1]),
                        new DataPoint(3, monthlyValues[2]),
                        new DataPoint(4, monthlyValues[3]),
                        new DataPoint(5, monthlyValues[4]),
                        new DataPoint(6, monthlyValues[5]),
                        new DataPoint(7, monthlyValues[6]),
                        new DataPoint(8, monthlyValues[7]),
                        new DataPoint(9, monthlyValues[8]),
                        new DataPoint(10, monthlyValues[9]),
                        new DataPoint(11, monthlyValues[10]),
                        new DataPoint(12, monthlyValues[11]),
                }));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mWaterQualityReportsRef.addValueEventListener(mWaterQualityReportsListener);
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
        series.setDrawDataPoints(true);

        graph.removeAllSeries();
        graph.addSeries(series);

    }

    //Haversine formula to calculate distance - distance will be returned in miles
    private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    private double[] parseReports(ArrayList<WaterQualityReport> allReports) {
        //Array of size 12, where 0 = Jan, 1 = Feb, etc.
        double[] monthlyValues = new double[12];

        for (int i = 0; i < 12; i++) {
            int numReports = 0;
            double totalPPM = 0.0;
            for (WaterQualityReport report : allReports) {
                if (report.getDateTime().getMonth() == i
                        && report.getDateTime().getYear() + 1900 == year
                        && distFrom(report.getLatitude(), report.getLongitude(), latitude, longitude) <= radius ) {
                    if (pollutionType.equals("Contaminant")) {
                        totalPPM += report.getContaminantPPM();
                    } else {
                        totalPPM += report.getVirusPPM();
                    }
                    numReports++;
                }
            }

            if (numReports == 0) {
                monthlyValues[i] = 0;
            } else {
                monthlyValues[i] = totalPPM/numReports;
            }
        }
        return monthlyValues;
    }

}
