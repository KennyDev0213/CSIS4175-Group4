package com.example.csis4175_group4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.example.csis4175_group4.utility.MenuAdaptor;
import com.example.csis4175_group4.utility.Option;
import com.example.csis4175_group4.viewmodels.AppViewModel;

public class MainActivity extends AppCompatActivity {

    AppViewModel appView;

    RecyclerView menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appView = new ViewModelProvider(this).get(AppViewModel.class);

        //initialize the recycler view (menu)
        menu = findViewById(R.id.main_menu);

        Resources r = getResources();
        String[] Titles = r.getStringArray(R.array.option_texts);

        //activity intents, what will be used when an option is selected
        final Intent[] options_actvities = {
                new Intent(MainActivity.this, PhotoCamera.class), //Camera Activity
                new Intent(MainActivity.this, PictureManagerActivity.class), //Photos Manager
                new Intent(MainActivity.this, GroupManagerActivity.class), //Group Manager
                new Intent(MainActivity.this, AlbumManagerActivity.class) //Album Manager
        };

        //the option classes to initialize
        final Option[] options = {
                new Option(Titles[0], R.drawable.ic_baseline_camera_alt_24, options_actvities[0]), //Camera option
                new Option(Titles[1], R.drawable.ic_baseline_image_24, options_actvities[1]), //Photos option
                new Option(Titles[2], R.drawable.ic_baseline_family_group_24, options_actvities[2]), //Group option
                new Option(Titles[3], R.drawable.ic_baseline_photo_album_24, options_actvities[3])
        };

        menu.setAdapter(new MenuAdaptor(this, options));
        menu.setLayoutManager(new LinearLayoutManager(this));
    }
}