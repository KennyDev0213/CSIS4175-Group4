package com.example.csis4175_group4.fragments.albummanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.csis4175_group4.AlbumManagerActivity;
import com.example.csis4175_group4.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class PhotoListAdapter extends RecyclerView.Adapter {
    private List<Photo> photoList;
    private ItemClickListener mListener;

    private static final String TAG = AlbumManagerActivity.class.getSimpleName();

    interface ItemClickListener {
        void onListItemUpdate(Photo photo, int position);
        void onListItemDelete(Photo photo, int position);
    }

    public ItemClickListener getListener() {
        return mListener;
    }

    public void setListener(ItemClickListener mListener) {
        this.mListener = mListener;
    }

    public PhotoListAdapter(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photo_list, parent, false);
        PhotoViewHolder photoViewHolder = new PhotoViewHolder(view);

        return photoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
        Photo curPhoto = photoList.get(position);
        photoViewHolder.txtPhotoDate.setText(curPhoto.getDate());
        photoViewHolder.txtPhotoDesc.setText(curPhoto.getDesc());

        long size;
        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference
                = firebaseStorage.getReferenceFromUrl("gs://csis4175-group4.appspot.com")
                            .child("images").child(curPhoto.getFilename());
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(photoViewHolder.itemView.getContext())
                            .load(task.getResult())
                            .into(photoViewHolder.imgPhoto);
                } else {
                    Toast.makeText(photoViewHolder.itemView.getContext(),
                            "Fail to load image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        photoViewHolder.txtPhotoDesc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) { //i:key code
                    Toast.makeText(view.getContext(),
                            photoViewHolder.txtPhotoDesc.getText().toString().trim(),
                            Toast.LENGTH_SHORT).show();

                    curPhoto.setDesc(photoViewHolder.txtPhotoDesc.getText().toString().trim());
                    mListener.onListItemUpdate(curPhoto, position);

                    return true;
                }
                return false;
            }
        });

        photoViewHolder.imgBtnPhotoDelete.setOnClickListener((View view) ->{
            mListener.onListItemDelete(curPhoto, position);
            Navigation.findNavController(view).navigate(R.id.albumDetailFragment);
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPhoto;
        private TextView txtPhotoDate;
        private EditText txtPhotoDesc;
        private ImageButton imgBtnPhotoDelete;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            txtPhotoDate = itemView.findViewById(R.id.txtPhotoDate);
            txtPhotoDesc = itemView.findViewById(R.id.txtPhotoDesc);
            imgBtnPhotoDelete = itemView.findViewById(R.id.imgBtnPhotoDelete);
        }
    }
}
