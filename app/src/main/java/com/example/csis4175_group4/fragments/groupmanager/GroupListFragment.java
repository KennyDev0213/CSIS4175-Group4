package com.example.csis4175_group4.fragments.groupmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csis4175_group4.GroupManagerActivity;
import com.example.csis4175_group4.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements GroupListAdapter.GroupDetailListener {

    List<Group> groupList;
    RecyclerView recyclerView;
    GroupListAdapter groupListAdapter;

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

        recyclerView = rootView.findViewById(R.id.recyclerViewGroupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        groupList = new ArrayList<>();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("group");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    Group group = child.getValue(Group.class);
                    groupList.add(group);
                }
                groupListAdapter = new GroupListAdapter(groupList);
                //groupListAdapter.setGroupDetailListener((GroupListAdapter.GroupDetailListener)this);
                recyclerView.setAdapter((groupListAdapter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    @Override
    public void onGroupDetailClick(Group group) {
        GroupDetailFragment groupDetailFragment = GroupDetailFragment.newInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_group_detail_container, groupDetailFragment).addToBackStack(null).commit();
    }
}