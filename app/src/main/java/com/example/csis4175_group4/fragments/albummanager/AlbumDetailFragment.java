package com.example.csis4175_group4.fragments.albummanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.csis4175_group4.AlbumManagerActivity;
import com.example.csis4175_group4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailFragment extends Fragment implements PhotoListAdapter.ItemClickListener {

    List<String> userGroupList;
    Album albumData;
    List<Photo> photoList;
    RecyclerView recyclerView;
    PhotoListAdapter photoListAdapter;
    private AlbumViewModel albumSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Album;
    private DatabaseReference mFirebaseDatabase_Photos;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = AlbumManagerActivity.class.getSimpleName();

    public interface PhotoListCallback {
        void onCallback(List<Photo> list);
    }
    public void readPhotoListData(PhotoListCallback callback) {
        mFirebaseDatabase_Photos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoList.clear();

                for(DataSnapshot child : snapshot.getChildren()) {
                    Photo photo = child.getValue(Photo.class);
                    photoList.add(photo);
                }
                photoListAdapter.setPhotoList(photoList);
                callback.onCallback(photoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public AlbumDetailFragment() {
        // Required empty public constructor
    }

    public static AlbumDetailFragment newInstance(String param1, String param2) {
        return new AlbumDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_album_detail, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumSharedViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);

        userGroupList = new ArrayList<>();
        photoList = new ArrayList<>();
        photoListAdapter = new PhotoListAdapter(photoList);
        photoListAdapter.setListener(this);

        recyclerView = view.findViewById(R.id.recyclerViewPhotoList);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        recyclerView.setAdapter((photoListAdapter));

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Album = mFirebaseInstance.getReference("Albums").child(albumSharedViewModel.getSelectedAlbumId().getValue());
        mFirebaseDatabase_Photos = mFirebaseInstance.getReference("Albums").child(albumSharedViewModel.getSelectedAlbumId().getValue()).child("photos");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");

        albumData = albumSharedViewModel.getSelectedAlbum().getValue();

        readPhotoListData(new PhotoListCallback() {
            @Override
            public void onCallback(List<Photo> list) {
                photoList = list;
                photoListAdapter.setPhotoList(photoList);
            }
        });

        EditText txtViewDetailAlbumName = view.findViewById(R.id.txtViewDetailAlbumName);
        EditText txtViewDetailAlbumContents = view.findViewById(R.id.txtViewDetailAlbumContents);

        txtViewDetailAlbumName.setText(albumData.getTitle());
        txtViewDetailAlbumContents.setText(albumData.getContents());

        txtViewDetailAlbumName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) { //i:key code
                    Toast.makeText(view.getContext(),
                            txtViewDetailAlbumName.getText().toString().trim(),
                            Toast.LENGTH_SHORT).show();

                    mFirebaseDatabase_Album.child("title").setValue(txtViewDetailAlbumName.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        txtViewDetailAlbumContents.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) { //i:key code
                    Toast.makeText(view.getContext(),
                            txtViewDetailAlbumContents.getText().toString().trim(),
                            Toast.LENGTH_SHORT).show();

                    mFirebaseDatabase_Album.child("contents").setValue(txtViewDetailAlbumContents.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        Button btnAlbumGroup = view.findViewById(R.id.btnAlbumGroup);
        btnAlbumGroup.setOnClickListener((View v) -> {
            Navigation.findNavController(v).navigate(R.id.action_albumDetailFragment_to_albumGroupFragment);
        });

        Button btnCloseAlbumDetail = view.findViewById(R.id.btnCloseAlbumDetail);
        btnCloseAlbumDetail.setOnClickListener((View v) -> {
            Navigation.findNavController(v).navigate(R.id.action_albumDetailFragment_to_albumListFragment);
        });


        FloatingActionButton fabAddPhoto = view.findViewById(R.id.fabAddPhoto);
        fabAddPhoto.setOnClickListener((View v) -> {
            Bundle bundle= new Bundle();
            Navigation.findNavController(v).navigate(R.id.action_albumDetailFragment_to_newPhotoFragment, bundle);
        });
    }

    @Override
    public void onListItemUpdate(Photo photo, int position) {
        albumSharedViewModel.setSelectedPhoto(photo);
        albumSharedViewModel.setSelectedPhotoId(photoList.get(position).getId());

        photoList.set(position, photo);
        photoListAdapter.setPhotoList(photoList);

        mFirebaseDatabase_Photos.child(photo.getId()).setValue(photo);
    }

    @Override
    public void onListItemDelete(Photo photo, int position) {
        albumSharedViewModel.setSelectedPhoto(photo);
        albumSharedViewModel.setSelectedPhotoId(photoList.get(position).getId());

        photoList.remove(position);
        photoListAdapter.setPhotoList(photoList);

        mFirebaseDatabase_Photos.child(photo.getId()).removeValue();
    }
}