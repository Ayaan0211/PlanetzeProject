package com.example.planetzeproject;

import android.content.Intent;
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
    List<String> xValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ecogauge);

        barChart = findViewById(R.id.barchart);
        barEntryList = new ArrayList<>();
        setBarValues();
        setUpBarChart();

        lineChart = findViewById(R.id.linechart);
        lineChart.getAxisRight().setDrawLabels(false);
        setUpLineX();
        setUpLineY();
        setUpLineChart();


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Checks if a user is not logged in, if not send user back to login page
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void setBarValues() {
        barEntryList.add(new BarEntry(1,300));
        barEntryList.add(new BarEntry(2,400));
        barEntryList.add(new BarEntry(3,200));
        barEntryList.add(new BarEntry(4,600));
    }

    private void setUpBarChart() {
        BarDataSet barDataSet = new BarDataSet(barEntryList, "");
        barDataSet.setColor(Color.GREEN);
        barDataSet.setValueTextSize(12f);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setUpLineChart() {
        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 10f));
        entries1.add(new Entry(1, 10f));
        entries1.add(new Entry(2, 15f));
        entries1.add(new Entry(3, 45f));
        entries1.add(new Entry(4, 45f));
        entries1.add(new Entry(5, 45f));
        entries1.add(new Entry(6, 45f));

        LineDataSet dataSet1 = new LineDataSet(entries1, "");
        dataSet1.setColor(Color.BLACK);

        LineData lineData = new LineData(dataSet1);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    private void setUpLineX() {
        xValues = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(7);
        xAxis.setGranularity(1f);
    }

    private void setUpLineY(){
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
    }
}