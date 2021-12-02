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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements GroupListAdapter.ItemClickListener{

    List<Group> groupList;
    RecyclerView recyclerView;
    GroupListAdapter groupListAdapter;
    private GroupViewModel groupSharedViewModel;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

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

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("group");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    Group group = child.getValue(Group.class);
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
            bundle.putInt("NUMBER_OF_GROUP", groupList.size());
            Navigation.findNavController(v).navigate(R.id.action_GroupListFragment_to_newGroupFragment, bundle);
        });
    }

    @Override
    public void onListItemClick(Group group, int position) {
        Log.d("GroupListFragment", "Click Position: " + position);
        Log.d("GroupListFragment", "Click Group name: " + group.getName());
        groupSharedViewModel.setSelectedGroup(group);
        groupSharedViewModel.setSelectedGroupId(position);
    }

    @Override
    public void onListItemDelete(Group group, int position) {
        Log.d("GroupListFragment", "Delete Position: " + position);
        Log.d("GroupListFragment", "Delete Group name: " + group.getName());

        groupSharedViewModel.setSelectedGroup(group);
        groupSharedViewModel.setSelectedGroupId(position);

        groupList.remove(position);
        groupListAdapter.setGroupList(groupList);
        mFirebaseDatabase.child(""+position).removeValue();
    }
}