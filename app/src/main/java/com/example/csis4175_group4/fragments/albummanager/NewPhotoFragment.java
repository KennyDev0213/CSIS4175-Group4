package com.example.csis4175_group4.fragments.albummanager;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.csis4175_group4.AlbumManagerActivity;
import com.example.csis4175_group4.R;
import com.example.csis4175_group4.fragments.groupmanager.Member;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPhotoFragment extends Fragment {

    static final int REQUEST_IMAGE_SELECT = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imgViewNewPhoto;
    private Uri filePath;
    private String fileName;

    private AlbumViewModel albumSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Photos;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = AlbumManagerActivity.class.getSimpleName();

    public NewPhotoFragment() {
        // Required empty public constructor
    }

    public static NewPhotoFragment newInstance(String param1, String param2) {
        return new NewPhotoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root_view = inflater.inflate(R.layout.fragment_new_photo, container, false);

        albumSharedViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Photos = mFirebaseInstance.getReference("Albums").child(albumSharedViewModel.getSelectedAlbumId().getValue()).child("photos");

        imgViewNewPhoto = root_view.findViewById(R.id.imgViewNewPhoto);
        EditText txtViewNewPhotoComment = root_view.findViewById(R.id.txtViewNewPhotoComment);

        ImageButton imgBtnSearchPhoto = root_view.findViewById(R.id.imgBtnSearchPhoto);
        imgBtnSearchPhoto.setOnClickListener((View view) -> {

            //By taking picture
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            try {
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            } catch (ActivityNotFoundException e) {
//                // display error state to the user
//            }

            //By choosing file
            Intent choosingPictureIntent = new Intent();
            choosingPictureIntent.setType("image/*");
            choosingPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(choosingPictureIntent,
                    "Select a image."), REQUEST_IMAGE_SELECT);

        });

        Button btnAddNewPhoto = root_view.findViewById(R.id.btnAddNewPhoto);
        btnAddNewPhoto.setOnClickListener((View view) -> {

            //upload image to Google Firebase Storage
            uploadImageToFirebaseStorage();

            String key = mFirebaseDatabase_Photos.push().getKey();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String photoDateTime = formatter.format(now);

            Photo photo = new Photo(key, fileName, photoDateTime, txtViewNewPhotoComment.getText().toString().trim());
            Map<String, Object> photoValues = photo.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(key, photoValues);
            mFirebaseDatabase_Photos.updateChildren(childUpdates);

            Navigation.findNavController(view).navigate(R.id.action_newPhotoFragment_to_albumDetailFragment);
        });

        Button btnCloseNewPhoto = root_view.findViewById(R.id.btnCloseNewPhoto);
        btnCloseNewPhoto.setOnClickListener((View view) -> {
            Navigation.findNavController(view).navigate(R.id.action_newPhotoFragment_to_albumDetailFragment);
        });

        return root_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //By taking picture
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imgViewNewPhoto.setImageBitmap(imageBitmap);
//        }

        //By choosing file
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));

            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(
                        this.getContext().getContentResolver(), filePath);
                imgViewNewPhoto.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setTitle("is uploading...");
            progressDialog.show();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date now = new Date();
            fileName = formatter.format(now) + ".jpg";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://csis4175-group4.appspot.com").child("images/" + fileName);
            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        }
                    });
        } else {
            Toast.makeText(this.getContext(), "Select image first.", Toast.LENGTH_SHORT).show();
        }
    }
}