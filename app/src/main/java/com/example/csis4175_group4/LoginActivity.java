package com.example.csis4175_group4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private String STATE = "LOGIN";

    private TextView title;

    private String username;
    private String email;
    private String password;
    private String confPassword;

    private Button loginBtn;
    private Button signupBtn;
    private Button forgotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.title = findViewById(R.id.login_title);

        this.loginBtn = findViewById(R.id.logIn_btn);
        this.signupBtn = findViewById(R.id.signUp_btn);
        this.forgotBtn = findViewById(R.id.pass_reset_btn);

        this.loginBtn.setOnClickListener( view -> {

        });

        this.signupBtn.setOnClickListener( view -> {
            switchState();
        });

        this.forgotBtn.setOnClickListener(view -> {

        });
    }

    private void switchState(){
        if(this.STATE.equals("LOGIN")){
            changeToSignUp();
        } else if(this.STATE.equals("SIGNUP")) {
            changeToLogin();
        } else {

        }
    }

    private void changeToLogin(){
        this.STATE = "LOGIN";
        this.title.setText(R.string.title_activity_login);
    }

    private void changeToSignUp(){
        this.STATE = "SIGNUP";
        this.title.setText(R.string.title_activity_signup);
    }
}