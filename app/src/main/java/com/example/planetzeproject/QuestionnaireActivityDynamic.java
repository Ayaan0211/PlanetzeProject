package com.example.planetzeproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


public class QuestionnaireActivityDynamic extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_dynamic);

        // Getting data
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        List<Question> questions = new ArrayList<>();
        LoadQuestions.loadQuestions(db, questions);
        List<String> data = questions.get(0).getAnswers();
        //List<String> data = Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4");

        // Reference to Spinner
        Spinner spinner = findViewById(R.id.spinner);

        // Custom Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.questionspinner, data) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Inflate dropdown layout
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
                }

                // Bind data to dropdown view
                TextView textView = (TextView) convertView;
                textView.setText(getItem(position));
                return convertView;
            }
        };

        // Set the layout for the spinner items
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Bind adapter to spinner
        spinner.setAdapter(adapter);

        //Attach OnItemSelectedListener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String answer = adapter.getItem(position);
                saveAnswer(db, "test", 1, answer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Idk what to do here
            }
        });
    }

    public void saveAnswer(FirebaseDatabase db, String user, int qnum, String answer){ //Pass db as arg so we don't need to set it every time
        DatabaseReference uref = db.getReference("Users");
        uref.child(user).child(String.valueOf(qnum)).setValue(answer);
    }
}