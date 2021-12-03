package com.example.csis4175_group4.utility;

import android.content.Intent;

public class Option {
    //option class for the main menu
    private String title;
    private int thumb;
    private Intent activity;

    public Option(String title, int thumb, Intent activity){
        this.title = title;
        this.thumb = thumb;
        this.activity = activity;
    }
}
