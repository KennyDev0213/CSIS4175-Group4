package com.example.csis4175_group4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText signupInputEmail;
    private EditText signupInputPassword;
    private EditText PasswordConfirm;
    private EditText UserNametxt;

    private Button btnSignUp;
    private Button btnLinkToLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        signupInputEmail = (EditText) findViewById(R.id.email_input);
        signupInputPassword = (EditText) findViewById(R.id.password_input);
        PasswordConfirm = findViewById(R.id.conf_password_input);
        UserNametxt = findViewById(R.id.username_input);

        btnSignUp = (Button) findViewById(R.id.SignUp_btn);
        btnLinkToLogIn = (Button) findViewById(R.id.Login_btn);

        btnLinkToLogIn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm(){
        String email = signupInputEmail.getText().toString().trim();
        String password = signupInputPassword.getText().toString().trim();
        if(!checkUsername()){
            return;
        }
        if(!checkEmail()){
            return;
        }
        if(!checkPassword()){
            return;
        }
        if(!confirmPassword()){
            return;
        }



        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignupActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    FirebaseUser user = auth.getCurrentUser();
                                    if(user == null) {
                                    } else {
                                        auth.getCurrentUser().sendEmailVerification();
                                        Toast.makeText(getApplicationContext(),
                                                "Verification email sent",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    }

                                    finish();
                                }
                            }
                        });
        Toast.makeText(getApplicationContext(),
                "You are successfully registered!",
                Toast.LENGTH_SHORT).show();

    }

    private boolean checkEmail() {
        String email = signupInputEmail.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {
            signupInputEmail.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputEmail);
            return false;
        }
        return true;
    }

    private boolean checkPassword() {
        String password = signupInputPassword.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {
            signupInputPassword.setError(getString(R.string.err_msg_required));
            requestFocus(signupInputPassword);
            return false;
        }
        return true;
    }
    private boolean checkUsername() {
        String username = UserNametxt.getText().toString();
        if (username.isEmpty()) {
            UserNametxt.setError(getString(R.string.err_msg_required));
            requestFocus(UserNametxt);
            return false;
        }
        return true;
    }

    private boolean confirmPassword() {
        String password = signupInputPassword.getText().toString().trim();
        String confirmP = PasswordConfirm.getText().toString().trim();

        if (!password.equals(confirmP)) {
            PasswordConfirm.setError(getString(R.string.err_msg_required));
            requestFocus(PasswordConfirm);
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