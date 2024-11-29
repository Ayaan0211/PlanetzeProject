package com.example.planetzeproject;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;

public class LoadQuestions{
    public static void loadQuestions(FirebaseDatabase db, List<Question> questions){
        DatabaseReference qref = db.getReference("Questions");
        qref.addListenerForSingleValueEvent( new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dSS){
                for (DataSnapshot qSS:dSS.getChildren()){
                    Question q = qSS.getValue(SliderQuestion.class);
                    if (q != null){
                        questions.add(q);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError dbError){
                System.out.println("Firebase Error");
            }
        });
    }
}