package com.example.csis4175_group4.fragments.albummanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.csis4175_group4.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class NewAlbumFragment extends Fragment {

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Group;
    private DatabaseReference mFirebaseDatabase_Album;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

    public NewAlbumFragment() {
        // Required empty public constructor
    }

    public static NewAlbumFragment newInstance(String param1, String param2) {
        return new NewAlbumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_new_album, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Group = mFirebaseInstance.getReference("Groups");
        mFirebaseDatabase_Album = mFirebaseInstance.getReference("Albums");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");

        EditText txtViewNewAlbumName = view.findViewById(R.id.txtViewNewAlbumName);
        EditText txtViewNewAlbumContents = view.findViewById(R.id.txtViewNewAlbumContents);
        Button btnAddNewAlbum = view.findViewById(R.id.btnAddNewAlbum);

        btnAddNewAlbum.setOnClickListener((View v) -> {
            String key = mFirebaseDatabase_Album.push().getKey();

            // add group into groups of Firebase
            Album album = new Album(key, txtViewNewAlbumName.getText().toString(),
                                    txtViewNewAlbumContents.getText().toString(), "", new HashMap<String, Photo>());
            Map<String, Object> albumValues = album.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, albumValues);
            mFirebaseDatabase_Album.updateChildren(childUpdates);

            NavHostFragment.findNavController(NewAlbumFragment.this)
                    .navigate(R.id.action_newAlbumFragment_to_albumListFragment);
        });

        Button btnCloseNewAlbum = view.findViewById(R.id.btnCloseNewAlbum);
        btnCloseNewAlbum.setOnClickListener((View v) -> {
            Navigation.findNavController(view).navigate(R.id.action_newAlbumFragment_to_albumListFragment);
        });
    }
}