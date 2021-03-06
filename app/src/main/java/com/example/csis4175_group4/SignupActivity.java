package com.example.csis4175_group4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.csis4175_group4.viewmodels.ForgetPassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class
SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser fUser;
    private DatabaseReference fDatabaseRef;

    private TextInputLayout inputEmail, inputPassword,inputConfirmPassword, inputUserName;

    private Button btnSignUp;
    private Button btnLinkToLogIn;
    private Button btnForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
        fDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputUserName = findViewById(R.id.inputUserName);
        btnForget=findViewById(R.id.pass_reset_btn);

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

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this, ForgetPassword.class);
                startActivity(intent);
            }
        });
    }

    private void submitForm(){
        String email = inputEmail.getEditText().getText().toString().trim();
        String password = inputPassword.getEditText().getText().toString().trim();
        String username = inputUserName.getEditText().getText().toString();

        //cleaned up some code here
        if(!checkUsername() || !checkEmail() || !checkPassword() || !confirmPassword()){
            return;
        }

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignupActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    if(fUser == null) {

                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                        hashMap.put("uid",fUser.getUid()); // add uid
                                        hashMap.put("username",username);
                                        hashMap.put("email",email); // add email
                                        fDatabaseRef.child(fUser.getUid()).updateChildren(hashMap);

                                        fUser.sendEmailVerification();
                                        Toast.makeText(getApplicationContext(),
                                                "Verification email sent",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    }
                                    finish();
                                }
                            }
                        });
//         Toast.makeText(getApplicationContext(),
//                 "You have successful registered!",
//                 Toast.LENGTH_SHORT).show();

    }

    private boolean checkEmail() {
        String email = inputEmail.getEditText().getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {
            inputEmail.setError(getString(R.string.err_msg_email));
            return false;
        }
        inputEmail.setError(null);
        return true;
    }

    private boolean checkPassword() {
        String password = inputPassword.getEditText().getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {
            inputPassword.setError(getString(R.string.err_msg_password));
            return false;
        }
        inputPassword.setError(null);
        return true;
    }
    private boolean checkUsername() {
        String username = inputUserName.getEditText().getText().toString();
        if (username.isEmpty()) {
            inputUserName.setError(getString(R.string.err_msg_username));
            return false;
        }
        inputUserName.setError(null);
        return true;
    }

    private boolean confirmPassword() {
        String password = inputPassword.getEditText().getText().toString().trim();
        String confirmP = inputConfirmPassword.getEditText().getText().toString().trim();

        if (!password.equals(confirmP)) {
            inputConfirmPassword.setError(getString(R.string.err_msg_confrimPassword));
            return false;
        }
        inputConfirmPassword.setError(null);
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isPasswordValid(String password) {
        return (password.length() >= 6);
    }
}
