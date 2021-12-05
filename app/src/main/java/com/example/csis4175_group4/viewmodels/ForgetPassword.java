package com.example.csis4175_group4.viewmodels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.csis4175_group4.LoginActivity;
import com.example.csis4175_group4.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {


    private EditText emailEdtxt;
    private Button btnReset,btnBack;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        emailEdtxt=findViewById(R.id.email);
        btnReset=findViewById(R.id.btn_reset_password);
        btnBack=findViewById(R.id.btn_back);

        firebaseAuth=FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=emailEdtxt.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Your password is successfuly reseted",Toast.LENGTH_LONG).show();

                                Log.e("TAG","Successful");
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Password reset has been failed",Toast.LENGTH_LONG).show();
                                Log.e("TAG","Failed");
                            }
                        }
                    });

                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgetPassword.this, LoginActivity.class);
                startActivity(intent);
            }
        });



    }
}