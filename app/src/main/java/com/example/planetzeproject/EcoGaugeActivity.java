package com.example.planetzeproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EcoGaugeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    BarChart barChart;
    LineChart lineChart;
    List<BarEntry> barEntryList;
    List<String> xValues, barValues;
    Button totalWeekly, totalMonthly, totalYearly, emissionWeekly, emissionMonthly, emissionYearly;
    TextView totalEmissionText;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ecogauge);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.tracker);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tracker) {
                startActivity(new Intent(EcoGaugeActivity.this, EcoTrackerActivity.class));
                return true;
            } else if (id == R.id.gauge) {
                return true;
            } else if (id == R.id.logout) {
                FirebaseAuth.getInstance().signOut();

                // Clear any locally stored user data (if necessary)
                SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to WelcomeActivity
                Intent intent = new Intent(EcoGaugeActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                startActivity(intent);
                finish();

                return true;
            } else {
                return false;
            }
        });

        barChart = findViewById(R.id.barchart);
        barEntryList = new ArrayList<>();
        setBarValues();
        setUpBarChart();

        lineChart = findViewById(R.id.linechart);
        lineChart.getAxisRight().setDrawLabels(false);
        setUpWeeklyLineX();
        setUpWeeklyLineY();
        setUpWeeklyLineChart();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize the Button
        totalWeekly = findViewById(R.id.weekly);
        totalMonthly = findViewById(R.id.monthly);
        totalYearly = findViewById(R.id.yearly);

        emissionWeekly= findViewById(R.id.weekly2);
        emissionMonthly = findViewById(R.id.monthly2);
        emissionYearly = findViewById(R.id.yearly2);
        totalEmissionText = findViewById(R.id.totalemission);

        // Checks if a user is not logged in, if not send user back to login page
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Set OnClickListener for the button
        totalWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the TextView's text
                totalEmissionText.setText("Your weekly emission is!");
            }
        });

        totalMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the TextView's text
                totalEmissionText.setText("Your monthly emission is!");
            }
        });

        totalYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the TextView's text
                totalEmissionText.setText("Your annual emission is!");
            }
        });

        emissionWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpWeeklyLineX();
                setUpWeeklyLineY();
                setUpWeeklyLineChart();
            }
        });

        emissionMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpMonthlyLineX();
                setUpMonthlyLineY();
                setUpMonthlyLineChart();
            }
        });

        emissionYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpYearlyLineX();
                setUpYearlyLineY();
                setUpYearlyLineChart();
            }
        });

    }

    private void setBarValues() {
        barEntryList.add(new BarEntry(1,300));
        barEntryList.add(new BarEntry(2,400));
        barEntryList.add(new BarEntry(3,200));
        barEntryList.add(new BarEntry(4,600));
    }

    private void setUpBarChart() {
        BarDataSet barDataSet = new BarDataSet(barEntryList, "");
        barDataSet.setColor(0xFF6200EE);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Disable description and legend
        barChart.getDescription().setEnabled(false);

        // Set up the X-axis labels
        List<String> barValues = Arrays.asList("", "Transportation", "Energy Use",
                "Food", "Shopping");
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(barValues)); // Set custom labels
        xAxis.setLabelCount(barValues.size()); // Ensure all labels are shown
        xAxis.setGranularity(1f); // Ensure 1-to-1 mapping of labels
        xAxis.setGranularityEnabled(true);

        // Remove gridlines for X-axis
        xAxis.setDrawGridLines(false); // Hides vertical gridlines

        // Remove gridlines for Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Hides horizontal gridlines
        barChart.getAxisRight().setDrawGridLines(false); // Hides gridlines on the right Y-axis

        barChart.invalidate(); // Refresh the chart
    }

    private void setUpWeeklyLineChart() {
        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 10f));
        entries1.add(new Entry(1, 10f));
        entries1.add(new Entry(2, 15f));
        entries1.add(new Entry(3, 45f));
        entries1.add(new Entry(4, 45f));
        entries1.add(new Entry(5, 45f));
        entries1.add(new Entry(6, 45f));

        LineDataSet dataSet1 = new LineDataSet(entries1, null);
        dataSet1.setColor(Color.BLACK);

        lineChart.getDescription().setEnabled(false); // Removes description label
        lineChart.getLegend().setEnabled(false); // Hides legend

        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private void setUpWeeklyLineX() {
        xValues = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(7);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
    }

    private void setUpWeeklyLineY(){
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
        yAxis.setDrawGridLines(false); // Hides horizontal gridlines
        lineChart.getAxisRight().setDrawGridLines(false); // Hides gridlines on the right Y-axis
    }

    private void setUpMonthlyLineChart() {
        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 10f));
        entries1.add(new Entry(1, 10f));
        entries1.add(new Entry(2, 15f));
        entries1.add(new Entry(3, 45f));

        LineDataSet dataSet1 = new LineDataSet(entries1, "");
        dataSet1.setColor(Color.BLACK);

        lineChart.getDescription().setEnabled(false); // Removes description label
        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private void setUpMonthlyLineX() {
        xValues = Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(4);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
    }

    private void setUpMonthlyLineY(){
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
        yAxis.setDrawGridLines(false); // Hides horizontal gridlines
        lineChart.getAxisRight().setDrawGridLines(false); // Hides gridlines on the right Y-axis
    }

    private void setUpYearlyLineChart() {
        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 10f));
        entries1.add(new Entry(1, 10f));
        entries1.add(new Entry(2, 15f));
        entries1.add(new Entry(3, 45f));

        LineDataSet dataSet1 = new LineDataSet(entries1, "");
        dataSet1.setColor(Color.BLACK);

        lineChart.getDescription().setEnabled(false); // Removes description label
        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private void setUpYearlyLineX() {
        xValues = Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(4);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
    }

    private void setUpYearlyLineY(){
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
        yAxis.setDrawGridLines(false); // Hides horizontal gridlines
        lineChart.getAxisRight().setDrawGridLines(false); // Hides gridlines on the right Y-axis
    }
}