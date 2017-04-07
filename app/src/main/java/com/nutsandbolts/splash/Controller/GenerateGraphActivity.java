package com.nutsandbolts.splash.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nutsandbolts.splash.Model.WaterQualityReport;
import com.nutsandbolts.splash.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

/**
 * This activity will take in a manager's input and return a graph showing the average
 * PPM values of the chosen pollutant for the chosen year.
 *
 * @author Kevin Tian
 */

public class GenerateGraphActivity extends AppCompatActivity {

    /*
   Widgets we will need to define listeners for
   */
    private Spinner pollutionTypeSpinner;
    private EditText yearText;
    private EditText longitudeText;
    private EditText latitudeText;
    private Spinner locationRadiusSpinner;
    private GraphView graph;

    /*
    Water quality report variables
    */
    private String pollutionType;
    private int year;
    private double latitude;
    private double longitude;
    private double radius;

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
        Button generateButton = (Button) findViewById(R.id.generate_graph);
        locationRadiusSpinner = (Spinner) findViewById(R.id.location_radius_spinner);
        graph = (GraphView) findViewById(R.id.graph);

        /*
          Set up adapters to display the possible contaminant types in the  and possible radii
         */
        String[] pollutionTypes = {"Virus", "Contaminant"};
        ArrayAdapter<String> pollutionTypeAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pollutionTypes);
        pollutionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pollutionTypeSpinner.setAdapter(pollutionTypeAdapter);

        String[] radiusDistances = {"10 miles", "25 miles", "50 miles"};
        final ArrayAdapter<String> locationRadiusAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, radiusDistances);
        locationRadiusAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationRadiusSpinner.setAdapter(locationRadiusAdapter);

        generateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                Get info from widgets
                */
                try {
                    Editable yearInput = yearText.getText();
                    year = Integer.parseInt(yearInput.toString());
                } catch (NumberFormatException e) {
                    Toast yearError = Toast.makeText(getApplicationContext(),
                            "Enter Valid Year", Toast.LENGTH_SHORT);
                    yearError.show();
                    return;
                }
                try {
                    Editable latitudeInput = latitudeText.getText();
                    latitude = Double.parseDouble(latitudeInput.toString());
                    Editable longitudeInput = longitudeText.getText();
                    longitude = Double.parseDouble(longitudeInput.toString());
                } catch (NumberFormatException e) {
                    Toast locationError = Toast.makeText(getApplicationContext(),
                            "Enter Valid Latitude and Longitude", Toast.LENGTH_SHORT);
                    locationError.show();
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
        FirebaseDatabase splashDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootNode = splashDatabase.getReference();
        DatabaseReference mWaterQualityReportsRef = rootNode.child("water-quality-reports");
        final Collection<WaterQualityReport> allReports = new ArrayList<>();

        ValueEventListener mWaterQualityReportsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot waterReportChildren : dataSnapshot.getChildren()) {
                    WaterQualityReport report = WaterQualityReport
                            .buildWaterQualityReportFromSnapShot(waterReportChildren);
                    allReports.add(report);
                }

                double[] monthlyValues = parseReports(allReports);

                drawGraph(new LineGraphSeries<>(new DataPoint[]{
                        //Add 1 because the calendar enum uses 0 for January
                        new DataPoint(Calendar.JANUARY + 1, monthlyValues[Calendar.JANUARY]),
                        new DataPoint(Calendar.FEBRUARY + 1, monthlyValues[Calendar.FEBRUARY]),
                        new DataPoint(Calendar.MARCH + 1, monthlyValues[Calendar.MARCH]),
                        new DataPoint(Calendar.APRIL + 1, monthlyValues[Calendar.APRIL]),
                        new DataPoint(Calendar.MAY + 1, monthlyValues[Calendar.MAY]),
                        new DataPoint(Calendar.JUNE + 1, monthlyValues[Calendar.JUNE]),
                        new DataPoint(Calendar.JULY + 1, monthlyValues[Calendar.JULY]),
                        new DataPoint(Calendar.AUGUST + 1, monthlyValues[Calendar.AUGUST]),
                        new DataPoint(Calendar.SEPTEMBER + 1, monthlyValues[Calendar.SEPTEMBER]),
                        new DataPoint(Calendar.OCTOBER + 1, monthlyValues[Calendar.OCTOBER]),
                        new DataPoint(Calendar.NOVEMBER + 1, monthlyValues[Calendar.NOVEMBER]),
                        new DataPoint(Calendar.DECEMBER + 1, monthlyValues[Calendar.DECEMBER]),
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
        Viewport graphSettings = graph.getViewport();
        graphSettings.setXAxisBoundsManual(true);
        graphSettings.setMinX(1);
        graphSettings.setMaxX(5);
        glr.setNumHorizontalLabels(5);

        graphSettings.setYAxisBoundsManual(true);
        graphSettings.setMinY(0);
        graphSettings.setMaxY(10);

        graphSettings.setScrollable(true); // enables horizontal scrolling
        graphSettings.setScrollableY(true); // enables vertical scrolling

        // custom label formatter to show months
        glr.setLabelFormatter(new DefaultLabelFormatter() {
            final String[] months = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun."
                    , "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show months instead of x
                    return months[(int) value - 1];
                } else {
                    // show normal y values
                    return super.formatLabel(value, false);
                }
            }
        });

        //Add in data
        series.setDrawDataPoints(true);

        graph.removeAllSeries();
        graph.addSeries(series);

    }


    /*This method is marked as "feature envy"
    This method should remain inside of GenerateGraph activity as it is only used to generate graph
    information. The feature envy comes from repeated use of the getter methods, which is due to the
    large amount of information that must be retrieved to draw the graph.
    */
    private double[] parseReports(Iterable<WaterQualityReport> allReports) {
        //Array of size 12, where 0 = Jan, 1 = Feb, etc.
        final int monthsInAYear = 12;
        double[] monthlyValues = new double[monthsInAYear];
        int[] dataPointsInEachMonth = new int[monthsInAYear];

        for (WaterQualityReport report : allReports) {
            if (report.isWithinBounds(year, latitude, longitude, radius)) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(report.getDateTime());

                //Java calendar has jan = 0
                int month = cal.get(Calendar.MONTH);

                if (("Contaminant").equals(pollutionType)) {
                    monthlyValues[month] += report.getContaminantPPM();
                } else {
                    monthlyValues[month] += report.getVirusPPM();
                }

                dataPointsInEachMonth[month]++;
            }
        }

        //Divide sum by total number of data points to get the average for that month
        for (int i = 0; i < monthsInAYear; i++) {
            if (dataPointsInEachMonth[i] != 0) {
                monthlyValues[i] = monthlyValues[i]/dataPointsInEachMonth[i];
            } else {
                monthlyValues[i] = 0;
            }
        }

        return monthlyValues;
    }

}
