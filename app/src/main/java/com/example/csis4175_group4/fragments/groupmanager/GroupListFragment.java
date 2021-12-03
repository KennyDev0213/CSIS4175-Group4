package com.example.csis4175_group4.fragments.groupmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csis4175_group4.GroupManagerActivity;
import com.example.csis4175_group4.R;
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

    List<Group> groupList;
    RecyclerView recyclerView;
    GroupListAdapter groupListAdapter;
    private GroupViewModel groupSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Group;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = GroupManagerActivity.class.getSimpleName();

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

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Group = mFirebaseInstance.getReference("Groups");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");

        List<String> userGroupList = new ArrayList<>();
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()) {
                    userGroupList.add(child.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mFirebaseDatabase_Group.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    Group group = child.getValue(Group.class);

                    if(userGroupList.contains(group.getId()))
                        groupList.add(group);
                }
                groupListAdapter.setGroupList(groupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FloatingActionButton fabAddGroup = view.findViewById(R.id.fabAddGroup);
        fabAddGroup.setOnClickListener((View v) -> {
            Bundle bundle= new Bundle();
//            bundle.putInt("NUMBER_OF_GROUP", groupList.size());
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

        groupSharedViewModel.setSelectedGroup(group);
        groupSharedViewModel.setSelectedGroupId(groupList.get(position).getId());

        groupList.remove(position);
        groupListAdapter.setGroupList(groupList);

        mFirebaseDatabase_Group.child(group.getId()).removeValue();

        Map<String, Object> userGroup = new HashMap<>();
        userGroup.put("id", group.getId());
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups").child(group.getId()).removeValue();
    }
}