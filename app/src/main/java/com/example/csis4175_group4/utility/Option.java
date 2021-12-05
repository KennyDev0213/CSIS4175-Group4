package com.example.csis4175_group4.utility;

import android.content.Intent;

public class Option {
    //option class for the main menu
    private final String title;
    private final int thumb;
    private final Intent activity;

    public Option(String title, int thumb, Intent activity){
        this.title = title;
        this.thumb = thumb;
        this.activity = activity;
    }

    public String getTitle(){
        return title;
    }

    public int getThumb(){
        return thumb;
    }

    public Intent getActivity(){
        return activity;
    }
}
