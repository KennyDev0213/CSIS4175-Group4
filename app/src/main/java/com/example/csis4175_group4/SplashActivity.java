package com.example.csis4175_group4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class
SplashActivity extends AppCompatActivity {
    FirebaseAuth Auth;
    FirebaseUser User;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();

        getSupportActionBar().hide();

        //TODO replace this method to initialize all the backend components first as needed then launch the app
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(User != null){
                    Intent main = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }else{
                    Intent main = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(main);
                    finish();
                }

            }
        }, 3000);

//        finish();
    }
}