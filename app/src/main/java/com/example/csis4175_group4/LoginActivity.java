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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

//    private Boolean LoginState = false;

    private TextView title;

    private TextInputLayout emailInput, passwordInput;
//    private EditText usernameInput;
//    private EditText emailInput;
//    private EditText passwordInput;
//    private EditText confirmPasswordInput;

//    private String username;
//    private String email;
//    private String password;
//    private String confPassword;

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
        this.auth = FirebaseAuth.getInstance();

        this.emailInput = findViewById(R.id.inputEmail);
        this.passwordInput = findViewById(R.id.inputPassword);

        this.loginBtn = findViewById(R.id.logIn_btn);
        this.signupBtn = findViewById(R.id.signUp_btn);
        this.forgotBtn = findViewById(R.id.pass_reset_btn);

        //add login functionality
        this.loginBtn.setOnClickListener( view -> {
            submitForm();
        });

        //add signup functionality
        this.signupBtn.setOnClickListener( view -> {
            //launch the LoginActivity
            startActivity(new Intent(LoginActivity.this,SignupActivity.class));
        });

        //add a way for user to recover forgotten information
        this.forgotBtn.setOnClickListener(view -> {
            //TODO implement password reset
        });
    }

    public void submitForm(){
        String email = emailInput.getEditText().getText().toString().trim();
        String password = passwordInput.getEditText().getText().toString().trim();
        Boolean checkErr = true;
        while(checkErr){
            checkEmail();
            checkPassword();
            if(
                    checkEmail()&&
                    checkPassword()
            ){
                checkErr = false;
            }else{
                return;
            }
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
        String email = emailInput.getEditText().getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {
            emailInput.setError(getString(R.string.err_msg_email));
            return false;
        }
        emailInput.setError(null);
        return true;
    }

    private boolean checkPassword() {
        String password = passwordInput.getEditText().getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {
            passwordInput.setError(getString(R.string.err_msg_password));
//            requestFocus(passwordInput);
            return false;
        }
        passwordInput.setError(null);
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    private static boolean isPasswordValid(String password) {
        return (password.length() >= 6);
    }
//    private void requestFocus(View view) {
//        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//    }
}