package com.example.csis4175_group4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private String STATE = "LOGIN";

    private TextView title;

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

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

        //Initialize all the components
        this.title = findViewById(R.id.login_title);

        this.usernameInput = findViewById(R.id.username_input);
        this.emailInput = findViewById(R.id.email_input);
        this.passwordInput = findViewById(R.id.password_input);
        this.confirmPasswordInput = findViewById(R.id.conf_password_input);

        this.loginBtn = findViewById(R.id.logIn_btn);
        this.signupBtn = findViewById(R.id.signUp_btn);
        this.forgotBtn = findViewById(R.id.pass_reset_btn);

        //add login functionality
        this.loginBtn.setOnClickListener( view -> {
            if(this.STATE.equals("LOGIN")){
                //TODO request information from the db and then log in
            }else{
                //TODO verify the user input then sign them in
            }
        });

        //add signup functionality
        this.signupBtn.setOnClickListener( view -> {
            switchState();
        });

        //add a way for user to recover forgotten information
        this.forgotBtn.setOnClickListener(view -> {
            //TODO implement password reset
        });
    }

    //switch states from login to sign up to prevent making a whole new activity just for a sign in
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
        this.signupBtn.setText(R.string.goto_signup_btn);
        this.loginBtn.setText(R.string.login_btn);

        findViewById(R.id.username_label).setVisibility(View.INVISIBLE);
        this.usernameInput.setVisibility(View.INVISIBLE);
        findViewById(R.id.confirm_password_label).setVisibility(View.INVISIBLE);
        this.confirmPasswordInput.setVisibility(View.INVISIBLE);
        findViewById(R.id.pass_reset_btn).setVisibility(View.VISIBLE);
    }

    private void changeToSignUp(){
        this.STATE = "SIGNUP";
        this.title.setText(R.string.title_activity_signup);
        this.signupBtn.setText(R.string.goto_login_btn);
        this.loginBtn.setText(R.string.signup_btn);

        findViewById(R.id.username_label).setVisibility(View.VISIBLE);
        this.usernameInput.setVisibility(View.VISIBLE);
        findViewById(R.id.confirm_password_label).setVisibility(View.VISIBLE);
        this.confirmPasswordInput.setVisibility(View.VISIBLE);
        findViewById(R.id.pass_reset_btn).setVisibility(View.INVISIBLE);
    }
}