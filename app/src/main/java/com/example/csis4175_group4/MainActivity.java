package com.example.csis4175_group4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.csis4175_group4.viewmodels.AppViewModel;

public class MainActivity extends AppCompatActivity {

    AppViewModel appView;

    RecyclerView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appView = new ViewModelProvider(this).get(AppViewModel.class);

        if(!appView.isLoggedIn()){
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
        }

        menu = findViewById(R.id.main_menu);

    }
}