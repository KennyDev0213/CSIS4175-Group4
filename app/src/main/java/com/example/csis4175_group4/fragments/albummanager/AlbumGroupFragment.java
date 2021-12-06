package com.example.csis4175_group4.fragments.albummanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csis4175_group4.AlbumManagerActivity;
import com.example.csis4175_group4.R;
import com.example.csis4175_group4.fragments.groupmanager.Group;
import com.example.csis4175_group4.fragments.groupmanager.GroupListFragment;
import com.example.csis4175_group4.fragments.groupmanager.Member;
import com.example.csis4175_group4.fragments.groupmanager.NewGroupFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumGroupFragment extends Fragment {

    private AlbumViewModel albumSharedViewModel;

    List<String> userGroupList;
    List<Group> groupList;
    String currentAlbumGroupId;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Album;
    private DatabaseReference mFirebaseDatabase_Groups;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = AlbumManagerActivity.class.getSimpleName();

    public interface UserListCallback {
        void onCallback(List<String> list);
    }
    public void readUserListData(AlbumGroupFragment.UserListCallback callback) {
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups") //current user's group lists
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userGroupList.clear();

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
    public void readGroupListData(AlbumGroupFragment.GroupListCallback callback) {
        mFirebaseDatabase_Groups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();

                for(DataSnapshot child : snapshot.getChildren()) {
                    Group group = child.getValue(Group.class);

                    //check if a group of Groups is in group of current user
                    //means current user can access to own group or admin of the group
                    Log.d("AlbumGroupFragment", "userGroupList2: " + userGroupList.contains(group.getId()));
                    Log.d("AlbumGroupFragment", "group.getId2: " + group.getId());
                    for(int i = 0; i < userGroupList.size(); i++) {
                        if (userGroupList.get(i).equals(group.getId())) { // check owner of group
                            Log.d("AlbumGroupFragment", "userGroupList.get(i): " + userGroupList.get(i));
                            groupList.add(group);
                        }
                    }

                    //check if current user is a member of group in Groups DB.
                    //If user is member, add the group into groupList for making group for album
                    HashMap<String, Member> members = group.getMembers();
                    for(Member m : members.values()) {
                        if(m.getUid().equals(mFirebaseUser.getUid()) &&
                            !groupList.contains(group.getId())) {
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

    public AlbumGroupFragment() {
        // Required empty public constructor
    }

    public static AlbumGroupFragment newInstance(String param1, String param2) {
        return new AlbumGroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_album_group, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumSharedViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);

        userGroupList = new ArrayList<>();
        groupList = new ArrayList<>();
        currentAlbumGroupId = "";

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Album = mFirebaseInstance.getReference("Albums").child(albumSharedViewModel.getSelectedAlbumId().getValue());
        mFirebaseDatabase_Groups = mFirebaseInstance.getReference("Groups");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");

        // set group name into spinner from user group lists
        readUserListData(new AlbumGroupFragment.UserListCallback() {
            @Override
            public void onCallback(List<String> list) {
                userGroupList = list;

                readGroupListData(new AlbumGroupFragment.GroupListCallback() {
                    @Override
                    public void onCallback(List<Group> list) {
                        groupList = list;

                        List<String> spinnerData = new ArrayList<>();;
                        for(int i=0; i < groupList.size(); i++) {
                            spinnerData.add(groupList.get(i).getName());
                        }

                        Spinner spnMyGroup = view.findViewById(R.id.spnMyGroup);
                        spnMyGroup.setAdapter(new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_dropdown_item_1line,
                                spinnerData));

                        mFirebaseDatabase_Album.child("groupid").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(!task.isSuccessful()) {
                                    Log.e(TAG, "Fail to fetch data from Firebase.", task.getException());
                                } else {
                                    Log.d(TAG, "task.getResult().getValue().toString(): " + String.valueOf(task.getResult().getValue()));

                                    currentAlbumGroupId = String.valueOf(task.getResult().getValue());
                                    String currentAlbumGroupName = "";
                                    for(int i=0; i < groupList.size(); i++) {
                                        if (currentAlbumGroupId.equals(groupList.get(i).getId())) {
                                            currentAlbumGroupName = groupList.get(i).getName();
                                            break;
                                        }
                                    }

                                    final int selectionIndex = spinnerData.indexOf(currentAlbumGroupName);
                                    TextView txtViewCurrentGroup = view.findViewById(R.id.txtViewCurrentGroup);
                                    if(selectionIndex == -1) {
                                        txtViewCurrentGroup.setText("There is no any group. Please select a group below.");
                                    } else {
                                        txtViewCurrentGroup.setText(currentAlbumGroupName);
                                    }

                                    Log.d("AlbumGroupFragment", "selectionIndex: " + selectionIndex);
                                    if(selectionIndex >= 0) {
                                        spnMyGroup.setSelection(selectionIndex);
                                    } else {
                                        spnMyGroup.setPrompt("Select a group for the album.");
                                        //spnMyGroup.setSelection(0);
                                    }

                                }
                            }
                        });

                    }
                });
            }
        });

        Button btnApplyAlbumGroup = view.findViewById(R.id.btnApplyAlbumGroup);
        btnApplyAlbumGroup.setOnClickListener((View v) -> {

            // check validation for group owner, not admin
            // group owner can only change group of the album to other group
            if (userGroupList.contains(currentAlbumGroupId) == false) {

                Toast.makeText(this.getContext(),
                        "You're not group owner that was made this group.\nGroup owner can only change the group.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // add group id into an album of Albums of Firebase
            Spinner spnMyGroup = view.findViewById(R.id.spnMyGroup);
            String selectionGroupName = spnMyGroup.getSelectedItem().toString();
            String selectionGroupId = "";
            for(int i=0; i < groupList.size(); i++) {
                if (selectionGroupName.equals(groupList.get(i).getName())) {
                    selectionGroupId = groupList.get(i).getId();
                    break;
                }
            }

            mFirebaseDatabase_Album.child("groupid").setValue(selectionGroupId);

            NavHostFragment.findNavController(AlbumGroupFragment.this)
                    .navigate(R.id.action_albumGroupFragment_to_albumDetailFragment);
        });

        Button btnCloseAlbumGroup = view.findViewById(R.id.btnCloseAlbumGroup);
        btnCloseAlbumGroup.setOnClickListener((View v) -> {
            Navigation.findNavController(v).navigate(R.id.action_albumGroupFragment_to_albumDetailFragment);
        });
    }
}