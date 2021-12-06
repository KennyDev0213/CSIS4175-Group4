package com.example.csis4175_group4.fragments.groupmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csis4175_group4.GroupManagerActivity;
import com.example.csis4175_group4.R;
import com.example.csis4175_group4.fragments.albummanager.Album;
import com.example.csis4175_group4.fragments.albummanager.AlbumListFragment;
import com.example.csis4175_group4.viewmodels.AppViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class GroupListFragment extends Fragment implements GroupListAdapter.ItemClickListener{

    List<String> userGroupList;
    List<Group> groupList;
    List<Album> allAlbumLists;

    RecyclerView recyclerView;
    GroupListAdapter groupListAdapter;
    private GroupViewModel groupSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Group;
    private DatabaseReference mFirebaseDatabase_Users;
    private DatabaseReference mFirebaseDatabase_Albums;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = GroupManagerActivity.class.getSimpleName();

    public interface UserListCallback {
        void onCallback(List<String> list);
    }
    public void readUserListData(UserListCallback callback) {
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups") //current user's group lists
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userGroupList.clear();

                        for(DataSnapshot child: snapshot.getChildren()) {
                            userGroupList.add(child.getKey()); //group id
                            Log.d("GroupListFragment", "userGroupList: " + child.getKey());
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
    public void readGroupListData(GroupListCallback callback) {
        mFirebaseDatabase_Group.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();

                for(DataSnapshot child : snapshot.getChildren()) {
                    Group group = child.getValue(Group.class);

                    //check if a group of Groups is in group of current user
                    //means current user can access to own group or admin group
                    Log.d("GroupListFragment", "userGroupList2: " + userGroupList.contains(group.getId()));
                    Log.d("GroupListFragment", "group.getId2: " + group.getId());
                    for(int i = 0; i < userGroupList.size(); i++) {
                        if (userGroupList.get(i).equals(group.getId())) { // check owner of group
                            Log.d("GroupListFragment", "userGroupList.get(i): " + userGroupList.get(i));
                            groupList.add(group);
                        }
                    }
                }
                groupListAdapter.setGroupList(groupList);
                callback.onCallback(groupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface AllAlbumListCallback {
        void onCallback(List<Album> list);
    }

    public void readAllAlbumListData(GroupListFragment.AllAlbumListCallback callback) {
        mFirebaseDatabase_Albums.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allAlbumLists.clear();

                for(DataSnapshot child : snapshot.getChildren()) {
                    Album album = child.getValue(Album.class);
                    allAlbumLists.add(album);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public GroupListFragment() {
        // Required empty public constructor
    }

    public static GroupListFragment newInstance() {
        return new GroupListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_group_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupSharedViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);

        groupList = new ArrayList<>();
        groupListAdapter = new GroupListAdapter(groupList);
        groupListAdapter.setListener(this);

        recyclerView = view.findViewById(R.id.recyclerViewGroupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter((groupListAdapter));

        userGroupList = new ArrayList<>();
        allAlbumLists = new ArrayList<>();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Group = mFirebaseInstance.getReference("Groups");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");
        mFirebaseDatabase_Albums = mFirebaseInstance.getReference("Albums");


        //After getting user data, get group data because of async issue
        readUserListData(new UserListCallback() {
            @Override
            public void onCallback(List<String> list) {
                userGroupList = list;

                readGroupListData(new GroupListCallback() {
                    @Override
                    public void onCallback(List<Group> list) {
                        groupList = list;
                        groupListAdapter.setGroupList(groupList);

                        readAllAlbumListData(new AllAlbumListCallback() {
                            @Override
                            public void onCallback(List<Album> list) {
                                allAlbumLists = list;
                            }
                        });
                    }
                });
            }
        });



        FloatingActionButton fabAddGroup = view.findViewById(R.id.fabAddGroup);
        fabAddGroup.setOnClickListener((View v) -> {
                Bundle bundle= new Bundle();
//                bundle.putInt("NUMBER_OF_GROUP", groupList.size());
                Navigation.findNavController(v).navigate(R.id.action_GroupListFragment_to_newGroupFragment, bundle);
        });
    }

    @Override
    public void onListItemClick(Group group, int position) {
        Log.d("GroupListFragment", "Click Position: " + position);
        Log.d("GroupListFragment", "Click Group name: " + group.getName());
        groupSharedViewModel.setSelectedGroup(group);
        groupSharedViewModel.setSelectedGroupId(groupList.get(position).getId());
    }

    @Override
    public void onListItemDelete(Group group, int position) {
        Log.d("GroupListFragment", "Delete Position: " + groupList.get(position).getId());
        Log.d("GroupListFragment", "Delete Group name: " + group.getName());
        Log.d("GroupListFragment", "mFirebaseUser user id: " + mFirebaseUser.getUid());
        Log.d("GroupListFragment", "mFirebaseUser user group id: " + mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups").child(group.getId()));
        Log.d("GroupListFragment", "mFirebaseUser group id: " + mFirebaseDatabase_Group.child(group.getId()));


        // check validation if there is album using the group id that is going to be deleted
        // if there is a album, cannot remove the group
        for(int i = 0; i < allAlbumLists.size(); i++) {
            if (group.getId().equals(allAlbumLists.get(i).getGroupId())) {

                Toast.makeText(GroupListFragment.this.getContext(),
                        "You cannot delete the group because there is a album using this group.\nPlease delete the album first.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }


        groupSharedViewModel.setSelectedGroup(group);
        groupSharedViewModel.setSelectedGroupId(groupList.get(position).getId());

        groupList.remove(position);
        groupListAdapter.setGroupList(groupList);

        mFirebaseDatabase_Group.child(group.getId()).removeValue();

        Map<String, Object> userGroup = new HashMap<>();
        userGroup.put("id", group.getId());
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups").child(group.getId()).removeValue();

//        //remove added group of the user by adding member
//        for(int i=0; i < userGroupList.size(); i++) {
//
//            Log.d("GroupListFragment", "==> Delete mFirebaseUser user group id: " + mFirebaseDatabase_Users.child(userGroupList.get(i))
//                    .child("groups").child(group.getId()).getKey());
//
//            if( (mFirebaseDatabase_Users.child(userGroupList.get(i))
//                    .child("groups").child(group.getId()).getKey()).equals(group.getId())) {
//                mFirebaseDatabase_Users.child(userGroupList.get(i))
//                        .child("groups").child(group.getId()).removeValue();
//            }
//        }
    }
}