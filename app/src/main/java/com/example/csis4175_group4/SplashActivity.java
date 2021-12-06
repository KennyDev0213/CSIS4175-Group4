package com.example.csis4175_group4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth Auth;
    FirebaseUser User;
    DatabaseReference Ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        Ref = FirebaseDatabase.getInstance().getReference().child("Users");

        getSupportActionBar().hide();

        //TODO replace this method to initialize all the backend components first as needed then launch the app
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(User != null){
//                    Toast.makeText(SplashActivity.this, "first", Toast.LENGTH_SHORT).show();
                    Ref.child(User.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Intent main = new Intent(SplashActivity.this, PictureManagerActivity.class);
                                startActivity(main);
                                finish();
                            }else{
                                Intent main = new Intent(SplashActivity.this, UserProfileActivity.class);
                                startActivity(main);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
//                    Toast.makeText(SplashActivity.this, "second", Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(main);
                    finish();
                }
            }
        }, 3000);

//        finish();
    }
}