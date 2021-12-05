package com.example.csis4175_group4.fragments.albummanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.csis4175_group4.AlbumManagerActivity;
import com.example.csis4175_group4.R;
import com.example.csis4175_group4.fragments.groupmanager.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewAlbumFragment extends Fragment {
    List<String> userGroupList;
    List<Group> groupList;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Album;
    private DatabaseReference mFirebaseDatabase_Groups;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;


    private static final String TAG = AlbumManagerActivity.class.getSimpleName();

    public interface UserListCallback {
        void onCallback(List<String> list);
    }
    public void readUserListData(NewAlbumFragment.UserListCallback callback) {
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups") //current user's group lists
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child: snapshot.getChildren()) {
                            userGroupList.add(child.getKey()); //group id
                            Log.d("AlbumGroupFragment", "userGroupList: " + child.getKey());
                        }

                        callback.onCallback(userGroupList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public interface GroupListCallback {
        void onCallback(List<Group> list);
    }
    public void readGroupListData(NewAlbumFragment.GroupListCallback callback) {
        mFirebaseDatabase_Groups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    Group group = child.getValue(Group.class);

                    //check if a group of Groups is in group of current user
                    //means current user can access to own group or admin group
                    Log.d("AlbumGroupFragment", "userGroupList2: " + userGroupList.contains(group.getId()));
                    Log.d("AlbumGroupFragment", "group.getId2: " + group.getId());
                    for(int i = 0; i < userGroupList.size(); i++) {
                        if (userGroupList.get(i).equals(group.getId())) { // check owner of group
                            Log.d("AlbumGroupFragment", "userGroupList.get(i): " + userGroupList.get(i));
                            groupList.add(group);
                        }
                    }
                }
                callback.onCallback(groupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

        userGroupList = new ArrayList<>();
        groupList = new ArrayList<>();
        
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Groups = mFirebaseInstance.getReference("Groups");
        mFirebaseDatabase_Album = mFirebaseInstance.getReference("Albums");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");

        EditText txtViewNewAlbumName = view.findViewById(R.id.txtViewNewAlbumName);
        EditText txtViewNewAlbumContents = view.findViewById(R.id.txtViewNewAlbumContents);
        Button btnAddNewAlbum = view.findViewById(R.id.btnAddNewAlbum);

        // set group name into spinner from user group lists
        readUserListData(new NewAlbumFragment.UserListCallback() {
            @Override
            public void onCallback(List<String> list) {
                userGroupList = list;

                readGroupListData(new NewAlbumFragment.GroupListCallback() {
                    @Override
                    public void onCallback(List<Group> list) {
                        groupList = list;

                        List<String> spinnerData = new ArrayList<>();;
                        for(int i=0; i < groupList.size(); i++) {
                            for(int j=0; j < userGroupList.size(); j++) {
                                if (userGroupList.get(j).equals(groupList.get(i).getId()))
                                    spinnerData.add(groupList.get(i).getName());
                            }
                        }

                        Spinner spnMyGroup = view.findViewById(R.id.spnMyGroup);
                        spnMyGroup.setAdapter(new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_dropdown_item_1line,
                                spinnerData));
                    }
                });
            }
        });

        btnAddNewAlbum.setOnClickListener((View v) -> {

            // get selected group from spinner to add group id into an album of Albums of Firebase
            Spinner spnMyGroup = view.findViewById(R.id.spnMyGroup);
            String selectionGroupName = spnMyGroup.getSelectedItem().toString();
            String selectionGroupId = "";
            for(int i=0; i < groupList.size(); i++) {
                if (selectionGroupName.equals(groupList.get(i).getName())) {
                    selectionGroupId = groupList.get(i).getId();
                    break;
                }
            }


            String key = mFirebaseDatabase_Album.push().getKey();

            // add group into groups of Firebase
            Album album = new Album(key, txtViewNewAlbumName.getText().toString(),
                                    txtViewNewAlbumContents.getText().toString(), selectionGroupId, new HashMap<String, Photo>());
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