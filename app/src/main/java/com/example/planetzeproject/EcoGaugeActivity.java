package com.example.planetzeproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EcoGaugeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    BarChart barChart;
    LineChart lineChart;
    List<BarEntry> barEntryList;
    List<String> xValues;
    Button Yearly, Monthly, Weekly, Refresh;
    TextView totalEmissionText;
    BottomNavigationView bottomNavigationView;
    final double globalC02 = 4000;
    final double nationalC02 = 15220;
    public HashMap<String, String> spinnerData = new HashMap<>();
    public HashMap<String, String> textInputData = new HashMap<>();
    private TextView textSelectedDate;
    private String selectedDate;
    public Calendar calendar;
    Double barTransportation;
    Double barEnergy;
    Double barFood;
    Double barTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ecogauge);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        textSelectedDate = findViewById(R.id.text_selected_date);
        textSelectedDate.setOnClickListener((v -> openDatePicker()));

        barChart = findViewById(R.id.barchart);
        lineChart = findViewById(R.id.linechart);
        totalEmissionText = findViewById(R.id.totalemission);
        Weekly = findViewById(R.id.weekly);
        Monthly = findViewById(R.id.monthly);
        Yearly = findViewById(R.id.yearly);
        Refresh = findViewById(R.id.refresh);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.tracker);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tracker) {
                startActivity(new Intent(EcoGaugeActivity.this, EcoTrackerActivity.class));
                finish();
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

        //setBarValues();
        //setUpBarChart();

        Refresh.setOnClickListener(view -> {
            retrieveFromFirebase();
            TextView nationalEmissionsTextView = findViewById(R.id.compnational);
            nationalEmissionsTextView.setText("National Average: " + nationalC02 / 365 + " kg");
            TextView globalEmissionsTextView = findViewById(R.id.compglobal);
            globalEmissionsTextView.setText("Global Average: " + globalC02 / 365 + " kg");
        });
        Weekly.setOnClickListener(view -> {
            calculateWeeklyEmissions();
            setUpWeeklyLineX();
            setUpWeeklyLineY();
            setUpWeeklyLineChart();
        });
        Monthly.setOnClickListener(view -> {
            setUpMonthlyLineX();
            setUpMonthlyLineY();
            setUpMonthlyLineChart();
        });
        Yearly.setOnClickListener(view -> {
            setUpYearlyLineX();
            setUpYearlyLineY();
            setUpYearlyLineChart();
        });
    }

    public void openDatePicker() {
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EcoGaugeActivity.this,
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    // Format the selected date
                    selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth1;
                    textSelectedDate.setText(selectedDate); // Update the TextView with the selected date
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void retrieveFromFirebase() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, redirect to login page
            Toast.makeText(EcoGaugeActivity.this, "Please log in first.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
            Intent intent = new Intent(EcoGaugeActivity.this, LoginActivity.class);
            startActivity(intent);
            return; // Stop execution if no user is logged in
        }

        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(EcoGaugeActivity.this, "Please select a date to delete data.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userID = currentUser.getUid(); // User ID
        DatabaseReference userEcoDataRef = databaseReference.child("users").child(userID).child("ecoTrackerData").child(selectedDate);

        userEcoDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the data exists
                if (dataSnapshot.exists()) {
                    // Access CO2 values from the snapshot
                    Double totalco2 = dataSnapshot.child("totalCo2").getValue(Double.class);

                    if (totalco2 != null && totalco2 != 0) {
                        TextView emissionsTextView = findViewById(R.id.compuser);
                        emissionsTextView.setText("Total CO2 Emissions Today: " + totalco2 + " kg");
                        emissionsTextView.setText("Total CO2 Emissions Today: " + totalco2 + " kg");
                        barTransportation = dataSnapshot.child("co2Transport").getValue(Double.class);
                        barEnergy = dataSnapshot.child("co2Consumption").getValue(Double.class);
                        barFood = dataSnapshot.child("co2Food").getValue(Double.class);
                        barTotal = totalco2;
                        setBarValues();
                        setUpBarChart();
                    } else {
                        // Handle case where the totalCO2 is zero or null
                        TextView emissionsTextView = findViewById(R.id.compuser);
                        emissionsTextView.setText("No CO2 data available for today.");
                    }
                } else {
                    // Handle case where data doesn't exist for the selected date
                    TextView emissionsTextView = findViewById(R.id.compuser);
                    emissionsTextView.setText("No data available for the selected date: " + selectedDate);
                    clearGraphs();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Toast.makeText(EcoGaugeActivity.this, "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void clearGraphs() {
        barChart.clear();

        barChart.invalidate();
    }

    private void calculateWeeklyEmissions() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(EcoGaugeActivity.this, "Please log in first.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EcoGaugeActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        // Calculate the dates for the current week (from Sunday to Saturday)
        List<String> weekDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Parse the selected date to a Calendar instance
        Calendar selectedCalendar = Calendar.getInstance();
        try {
            selectedCalendar.setTime(dateFormat.parse(selectedDate));
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date selected.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Calculate the dates for the week starting from the selected date (backward for 7 days)
        for (int i = 0; i < 7; i++) {
            weekDates.add(dateFormat.format(selectedCalendar.getTime()));
            selectedCalendar.add(Calendar.DAY_OF_YEAR, -1); // Move one day backward
        }

        // Reverse the list to have the dates in ascending order
        Collections.reverse(weekDates);

        // Retrieve and sum CO2 emissions for each day of the week
        List<Double> dailyEmissions = new ArrayList<>();

        for (String date : weekDates) {
            String userID = currentUser.getUid(); // User ID
            DatabaseReference userEcoDataRef = databaseReference
                    .child("users")
                    .child(userID)
                    .child("ecoTrackerData")
                    .child(date);

            userEcoDataRef.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Double dailyCO2 = dataSnapshot.child("totalCo2").getValue(Double.class);
                        if (dailyCO2 != null) {
                            dailyEmissions.add(dailyCO2);
                        } else {
                            dailyEmissions.add(0.0);
                        }
                    }
                    else{
                        dailyEmissions.add(0.0);
                    }

                    if (dailyEmissions.size() == 7) { // Once all data is fetched
                        double totalWeeklyEmissions = 0;
                        for (Double emission : dailyEmissions) {
                            totalWeeklyEmissions += emission;
                        }

                        // Display total weekly emissions
                        TextView emissionsTextView = findViewById(R.id.totalemission);
                        emissionsTextView.setText("Total Weekly CO2 Emissions: " + dailyEmissions + " kg" + date.getClass());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EcoGaugeActivity.this, "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setBarValues() {
        barEntryList = new ArrayList<>();
        barEntryList.add(new BarEntry(1, barTransportation.floatValue()));
        barEntryList.add(new BarEntry(2, barEnergy.floatValue()));
        barEntryList.add(new BarEntry(3, barFood.floatValue()));
        barEntryList.add(new BarEntry(4, barTotal.floatValue()));
    }

    private void setUpBarChart() {
        BarDataSet barDataSet = new BarDataSet(barEntryList, "");
        barDataSet.setColor(0xFF6200EE);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        List<String> barValues = Arrays.asList("", "Transportation", "Energy Use", "Food", "Total");
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(barValues));
        xAxis.setLabelCount(barValues.size());
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
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

    private void setUpWeeklyLineY() {
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

    private void setUpMonthlyLineY() {
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

    private void setUpYearlyLineY() {
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
        yAxis.setDrawGridLines(false); // Hides horizontal gridlines
        lineChart.getAxisRight().setDrawGridLines(false); // Hides gridlines on the right Y-axis
    }
}