package com.example.csis4175_group4.fragments.groupmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
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

public class GroupDetailFragment extends Fragment implements MemberListAdapter.ItemClickListener{

    private List<Member> memberList;
    private EditText txtViewGroupNameHeader;
    private RecyclerView recyclerView;
    private MemberListAdapter memberListAdapter;
    private GroupViewModel groupSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase;

    private static final String TAG = GroupManagerActivity.class.getSimpleName();

    public GroupDetailFragment() {
        // Required empty public constructor
    }

    public static GroupDetailFragment newInstance() {
        return new GroupDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_group_detail, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        memberList = new ArrayList<>();
        memberListAdapter = new MemberListAdapter(memberList);
        memberListAdapter.setListener(this);

        txtViewGroupNameHeader = view.findViewById(R.id.txtViewGroupNameHeader);

        recyclerView = view.findViewById(R.id.recyclerViewMemberList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter((memberListAdapter));


        groupSharedViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("group").child(""+groupSharedViewModel.getSelectedGroupId().getValue()).child("members");

        groupSharedViewModel.getSelectedGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                Log.d("GroupDetailFragment", "Group ID: " + groupSharedViewModel.getSelectedGroupId().getValue());
                Log.d("GroupDetailFragment", "Group Name: " + groupSharedViewModel.getSelectedGroup().getValue().getName());

                txtViewGroupNameHeader.setText(groupSharedViewModel.getSelectedGroup().getValue().getName());
                mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child : snapshot.getChildren()) {
                            Member member = child.getValue(Member.class);
                            memberList.add(member);
                        }
                        memberListAdapter.setMemberList(memberList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        EditText txtViewGroupName = view.findViewById(R.id.txtViewGroupNameHeader);
        Button btnRenameGroup = view.findViewById(R.id.btnRenameGroup);
        btnRenameGroup.setOnClickListener((View v) -> {
            mFirebaseInstance.getReference("group")
                    .child(""+groupSharedViewModel.getSelectedGroupId().getValue()).child("name") //group name
                    .setValue(txtViewGroupName.getText().toString());

            NavHostFragment.findNavController(GroupDetailFragment.this)
                    .navigate(R.id.action_GroupDetailFragment_to_GroupListFragment);
        });
    }

    @Override
    public void onListItemClick(Member member, int position) {
        memberList.remove(position);
        memberListAdapter.setMemberList(memberList);
    }
}