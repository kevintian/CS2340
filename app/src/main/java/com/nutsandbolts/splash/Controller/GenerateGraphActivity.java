package com.nutsandbolts.splash.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nutsandbolts.splash.R;

import java.util.HashMap;

public class GenerateGraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_graph);

        GraphView graph = (GraphView) findViewById(R.id.graph);
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
                    return months[(int)value - 1];
                } else {
                    // show normal y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        //Add in data
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
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

        graph.addSeries(series);
    }
}
