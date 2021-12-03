package com.example.csis4175_group4.fragments.groupmanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.csis4175_group4.R;
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

public class NewMemberFragment extends Fragment {

    List<User> userList;
    private GroupViewModel groupSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Group_Members;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

    public NewMemberFragment() {
        // Required empty public constructor
    }

    public static NewMemberFragment newInstance(String param1, String param2) {
        return new NewMemberFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_new_member, container, false);


        userList = new ArrayList<>();
        groupSharedViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Group_Members = mFirebaseInstance.getReference("Groups").child(""+groupSharedViewModel.getSelectedGroupId().getValue()).child("members");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");


        // get user list
        mFirebaseDatabase_Users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot child: snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    Log.d("NEWMEMBERFRAG", "userList.add " + user.getUsername());
                    userList.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        //get number of member
//        int numberOfMember = 0;
//        if(getArguments() != null) {
//            numberOfMember = getArguments().getInt("NUMBER_OF_MEMBER");
//            Log.d("NEWMEMBERFRAG", "The number of member is " + numberOfMember);
//        }

        EditText txtViewNewMemberEmail = view.findViewById(R.id.txtViewNewMemberEmail);
        ImageButton imgBtnSearchMember = view.findViewById(R.id.imgBtnSearchMember);
        RadioGroup rdoRoleGroup = view.findViewById(R.id.rdoRoleGroup);
        Button btnAddMember = view.findViewById(R.id.btnAddMember);

        // Search user by email from the users info registered in the Firebase
        imgBtnSearchMember.setOnClickListener((View v) -> {

        });

        // Add member
//        int nextMemberId = numberOfMember;
        btnAddMember.setOnClickListener((View v) -> {

            String inputEmail = txtViewNewMemberEmail.getText().toString().trim();

            HashMap<String, Member> members = groupSharedViewModel.getSelectedGroup().getValue().getMembers();
            for(Member m : members.values()) {
                if(inputEmail.equals(m.getEmail())) {
                    Toast.makeText(this.getContext(), "The email is duplicated. please enter other email.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String inputRole;
            int selectedRdoId =  rdoRoleGroup.getCheckedRadioButtonId();
            if(selectedRdoId != -1) {
                if(selectedRdoId == R.id.rdoAdmin)
                    inputRole = "admin";
                else
                    inputRole = "general";
            } else {
                Toast.makeText(this.getContext(), "Please select the role", Toast.LENGTH_SHORT).show();
                return;
            }


            Log.d("NEWMEMBERFRAG", "userList.size " + userList.size());

            for(int i = 0; i < userList.size(); i++) {
                if (inputEmail.equals(userList.get(i).getEmail())) {
                    String userId = userList.get(i).getUid();

                    // add group info into Users of Firebase
                    Map<String, Object> userGroup = new HashMap<>();
                    userGroup.put(groupSharedViewModel.getSelectedGroup().getValue().getId(),
                            groupSharedViewModel.getSelectedGroup().getValue().getId());
                    mFirebaseDatabase_Users.child(userId).child("groups").updateChildren(userGroup);


                    // add member into group of Firebase
                    String key = mFirebaseDatabase_Group_Members.push().getKey();
                    Member member = new Member(key, userId, inputEmail, inputRole);
                    Map<String, Object> memberValues = member.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();

                    childUpdates.put(key, memberValues);
                    mFirebaseDatabase_Group_Members.updateChildren(childUpdates);
                    break;
                }
            }

            NavHostFragment.findNavController(NewMemberFragment.this)
                    .navigate(R.id.action_newMemberFragment_to_GroupDetailFragment);
        });
    }
}