package com.example.planetzeproject;
import java.util.List;

public class SliderQuestion implements Question{ // To follow D of SOLID xd
    private int questionNumber;
    private String questionText;
    private List<String> answer;
    public SliderQuestion(){}

    public SliderQuestion(int questionNumber, String questionText, List<String> answer){
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.answer = answer;
    }

    public Question getQuestion(){
        return this;
    }
}
