package com.example.csis4175_group4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Boolean LoginState = false;

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

    private FirebaseAuth auth;

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
            if(!LoginState){
                //TODO request information from the db and then log in
                submitForm();
            }else{
                //TODO verify the user input then sign them in
            }
        });

        //add signup functionality
        this.signupBtn.setOnClickListener( view -> {
//            switchState();
            startActivity(new Intent(LoginActivity.this,SignupActivity.class));
        });

        //add a way for user to recover forgotten information
        this.forgotBtn.setOnClickListener(view -> {
            //TODO implement password reset
        });
    }


    //switch states from login to sign up to prevent making a whole new activity just for a sign in
//    private void switchState(){
//        if(this.STATE.equals("LOGIN")){
//            changeToSignUp();
//        } else if(this.STATE.equals("SIGNUP")) {
//            changeToLogin();
//        } else {
//
//        }
//    }
//
//    private void changeToLogin(){
//        this.STATE = "LOGIN";
//        this.title.setText(R.string.title_activity_login);
//        this.signupBtn.setText(R.string.goto_signup_btn);
//        this.loginBtn.setText(R.string.login_btn);
//
//        findViewById(R.id.username_label).setVisibility(View.INVISIBLE);
//        this.usernameInput.setVisibility(View.INVISIBLE);
//        findViewById(R.id.confirm_password_label).setVisibility(View.INVISIBLE);
//        this.confirmPasswordInput.setVisibility(View.INVISIBLE);
//        findViewById(R.id.pass_reset_btn).setVisibility(View.VISIBLE);
//    }
//
//    private void changeToSignUp(){
//        this.STATE = "SIGNUP";
//        this.title.setText(R.string.title_activity_signup);
//        this.signupBtn.setText(R.string.goto_login_btn);
//        this.loginBtn.setText(R.string.signup_btn);
//
//        findViewById(R.id.username_label).setVisibility(View.VISIBLE);
//        this.usernameInput.setVisibility(View.VISIBLE);
//        findViewById(R.id.confirm_password_label).setVisibility(View.VISIBLE);
//        this.confirmPasswordInput.setVisibility(View.VISIBLE);
//        findViewById(R.id.pass_reset_btn).setVisibility(View.INVISIBLE);
//    }
    public void submitForm(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(!checkEmail()){
            return;
        }
        if(!checkPassword()){
            return;
        }
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this,PictureManagerActivity.class));
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this,getString(R.string.auth_failed),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean checkEmail() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {
            emailInput.setError(getString(R.string.err_msg_required));
            requestFocus(emailInput);
            return false;
        }
        return true;
    }

    private boolean checkPassword() {
        String password = passwordInput.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {
            passwordInput.setError(getString(R.string.err_msg_required));
            requestFocus(passwordInput);
            return false;
        }
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    private static boolean isPasswordValid(String password) {
        return (password.length() >= 6);
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}