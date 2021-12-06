package com.example.csis4175_group4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;
    CircleImageView profileImageView;
    EditText inputUsername, inputCity,inputCountry, inputProfession;
    Button btnSave;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profileImageView = findViewById(R.id.profile_image);
        inputUsername = findViewById(R.id.inputUsername);
        inputCity = findViewById(R.id.inputCity);
        inputCountry = findViewById(R.id.inputCountry);
        inputProfession = findViewById(R.id.inputProfession);
        btnSave = findViewById(R.id.btnSave);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageRef = FirebaseStorage.getInstance().getReference().child("ProfileImage");

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveData();
            }
        });
    }

    private void SaveData() {
        String username = inputUsername.getText().toString();
        String city = inputCity.getText().toString();
        String country = inputCountry.getText().toString();
        String profession = inputProfession.getText().toString();

        if(username.length()<0){
            showError(inputUsername,"Username is invalid");
        }else if(city.length()<0){
            showError(inputCity,"City is invalid");
        }else if(country.length()<0){
            showError(inputCountry,"Country is invalid");
        }else if(profession.length()<0){
            showError(inputProfession,"Profession is invalid");
        }else{
            if(imageUri!=null){
                storageRef.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        should start Storage and Realtime Database in Firebase else task wont success
                        if(task.isSuccessful()){
                            storageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(UserProfileActivity.this,"pass ", Toast.LENGTH_SHORT).show();
                                    HashMap hashMap=new HashMap();
                                    hashMap.put("username",username);
                                    hashMap.put("city",city);
                                    hashMap.put("country",country);
                                    hashMap.put("profession",profession);
                                    hashMap.put("profileImage",uri.toString());
                                    hashMap.put("status","offline");

                                    mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {

                                            Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(UserProfileActivity.this, "Setup profile complete", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }

                    }
                });
            }else{
                HashMap hashMap=new HashMap();
                hashMap.put("username",username);
                hashMap.put("city",city);
                hashMap.put("country",country);
                hashMap.put("profession",profession);
                hashMap.put("status","offline");

                mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
                        startActivity(intent);
//                        Toast.makeText(UserProfileActivity.this, "Setup profile complete", Toast.LENGTH_SHORT).show();
                        Toast.makeText(UserProfileActivity.this,"fail", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


//
//                storageRef.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
////                        Toast.makeText(UserProfileActivity.this,"pass ", Toast.LENGTH_SHORT).show();
//                        if(task.isSuccessful()){
//                            storageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    HashMap hashMap=new HashMap();
//                                    hashMap.put("username",username);
//                                    hashMap.put("city",city);
//                                    hashMap.put("country",country);
//                                    hashMap.put("profession",profession);
//                                    hashMap.put("profileImage",uri.toString());
//                                    hashMap.put("status","offline");
//
//                                    mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
//                                        @Override
//                                        public void onSuccess(Object o) {
//
//                                            Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
//                                            startActivity(intent);
//                                            Toast.makeText(UserProfileActivity.this, "Setup profile complete", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(UserProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            });
//                        }
//
//                    }
//                });
            }

        }

    }

    private void showError(EditText field, String s) {
        field.setError(s);
        field.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK&&data!=null){
            imageUri=data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}
