package com.example.csis4175_group4.fragments.groupmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csis4175_group4.GroupManagerActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailFragment extends Fragment implements MemberListAdapter.ItemClickListener{

    private String currentUserRole;
    private List<Member> memberList;
    private EditText txtViewGroupNameHeader;
    private RecyclerView recyclerView;
    private MemberListAdapter memberListAdapter;
    private GroupViewModel groupSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Groups;
    private DatabaseReference mFirebaseDatabase_Group_Members;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

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

        boolean isGroupOwner = false;
        currentUserRole = "";
        memberList = new ArrayList<>();
        memberListAdapter = new MemberListAdapter(memberList);
        memberListAdapter.setListener(this);

        txtViewGroupNameHeader = view.findViewById(R.id.txtViewGroupNameHeader);

        recyclerView = view.findViewById(R.id.recyclerViewMemberList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter((memberListAdapter));


        groupSharedViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Groups = mFirebaseInstance.getReference("Groups");
        mFirebaseDatabase_Group_Members = mFirebaseInstance.getReference("Groups").child(""+groupSharedViewModel.getSelectedGroupId().getValue()).child("members");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        groupSharedViewModel.getSelectedGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                Log.d("GroupDetailFragment", "Group ID: " + groupSharedViewModel.getSelectedGroupId().getValue());
                Log.d("GroupDetailFragment", "Group Name: " + groupSharedViewModel.getSelectedGroup().getValue().getName());

                txtViewGroupNameHeader.setText(groupSharedViewModel.getSelectedGroup().getValue().getName());

                mFirebaseDatabase_Group_Members.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child : snapshot.getChildren()) {
                            Member member = child.getValue(Member.class);
                            memberList.add(member);
                        }
                        memberListAdapter.setMemberList(memberList);


                        // check validation if current user is general group member
                        // if the user is the general user, the user cannot any operations such as deletion and update in the group.
                        currentUserRole = "";
                        if(groupSharedViewModel.isGroupOwner()) {
                            currentUserRole = "owner";
                        } else {
                            for (int i = 0; i < memberList.size(); i++) {
                                if (memberList.get(i).getUid().equals(mFirebaseUser.getUid())) {
                                    currentUserRole = memberList.get(i).getRole();
                                    break;
                                }
                            }
                        }
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

            if(currentUserRole.equals("general") || currentUserRole.equals("")) {
                Toast.makeText(GroupDetailFragment.this.getContext(),
                        "You cannot change the group name because you are general member in this group.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            mFirebaseDatabase_Groups
                    .child(""+groupSharedViewModel.getSelectedGroupId().getValue()).child("name") //group name
                    .setValue(txtViewGroupName.getText().toString());

            NavHostFragment.findNavController(GroupDetailFragment.this)
                    .navigate(R.id.action_GroupDetailFragment_to_GroupListFragment);
        });

        FloatingActionButton fabAddMember = view.findViewById(R.id.fabAddMember);
        fabAddMember.setOnClickListener((View v) -> {

            if(currentUserRole.equals("general") || currentUserRole.equals("")) {
                Toast.makeText(GroupDetailFragment.this.getContext(),
                        "You cannot add the group member because you are general member in this group.",
                        Toast.LENGTH_LONG).show();
                return;
            }


            Bundle bundle= new Bundle();
//            bundle.putInt("NUMBER_OF_MEMBER", memberList.size());
            Navigation.findNavController(v).navigate(R.id.action_GroupDetailFragment_to_newMemberFragment, bundle);
        });

        Button btnClose = view.findViewById(R.id.btnMemberClose);
        btnClose.setOnClickListener((View v) -> {
            Navigation.findNavController(v).navigate(R.id.action_GroupDetailFragment_to_GroupListFragment);
        });
    }

    @Override
    public void onListItemDelete(Member member, int position) {
        Log.d("GroupListFragment", "Delete Position: " + memberList.get(position).getUid());
        Log.d("GroupListFragment", "Delete Member name: " + member.getUid());

        // general member cannot delete other member
        if( (member.getUid().equals(mFirebaseUser.getUid()) == false) &&
                (currentUserRole.equals("general") || currentUserRole.equals(""))){
            Toast.makeText(GroupDetailFragment.this.getContext(),
                    "You cannot remove other group member because you are general member in this group.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        memberList.remove(position);
        memberListAdapter.setMemberList(memberList);

        mFirebaseDatabase_Group_Members.child(member.getUid()).removeValue();

        Group group = groupSharedViewModel.getSelectedGroup().getValue();
        HashMap<String, Member> memberList = group.getMembers();
        memberList.remove(member.getUid());
        group.setMembers(memberList);
        groupSharedViewModel.setSelectedGroup(group);
    }
}