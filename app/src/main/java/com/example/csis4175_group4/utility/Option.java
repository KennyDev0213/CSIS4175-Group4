package com.example.csis4175_group4.utility;

import android.content.Intent;
import android.graphics.Picture;

public class Option {
    //option class for the main menu
    private String title;
    private Picture thumb;
    private Intent activity;

    public Option(String title, Picture thumb, Intent activity){
        this.title = title;
        this.thumb = thumb;
        this.activity = activity;
    }
}
