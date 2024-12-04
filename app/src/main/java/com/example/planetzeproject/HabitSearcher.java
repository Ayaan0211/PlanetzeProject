package com.example.planetzeproject;

import java.util.List;

public abstract class HabitSearcher {
    protected List<String> habits;
    //Returns an ArrayList<String> of habits that match
    public abstract List<String> search();
}
