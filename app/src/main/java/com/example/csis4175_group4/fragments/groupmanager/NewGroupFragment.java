package com.example.csis4175_group4.fragments.groupmanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.csis4175_group4.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewGroupFragment extends Fragment {

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Group;
    private DatabaseReference mFirebaseDatabase_Users;
    private FirebaseUser mFirebaseUser;

    public NewGroupFragment() {
        // Required empty public constructor
    }

    public static NewGroupFragment newInstance(String param1, String param2) {
        return new NewGroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_new_group, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        //get number of group()
//        int numberOfGroup = 0;
//        if(getArguments() != null) {
//            numberOfGroup = getArguments().getInt("NUMBER_OF_GROUP");
//            Log.d("NEWGROUPFRAG", "The number of group is " + numberOfGroup);
//        }

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Group = mFirebaseInstance.getReference("Groups");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");

        EditText txtViewNewGroupName = view.findViewById(R.id.txtViewNewGroupName);
        Button btnAddNewGroup = view.findViewById(R.id.btnAddNewGroup);

        btnAddNewGroup.setOnClickListener((View v) -> {
            String groupid = mFirebaseDatabase_Group.push().getKey();

            // add group into Groups of Firebase
            Group group = new Group(groupid, txtViewNewGroupName.getText().toString(), new HashMap<String, Member>());
            Map<String, Object> groupValues = group.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(groupid, groupValues);
            mFirebaseDatabase_Group.updateChildren(childUpdates);

            // add current user into group as member(admin) of the group
            String userid = mFirebaseUser.getUid();
            mFirebaseDatabase_Users.child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        String email = task.getResult().child("email").getValue().toString();
                        Member member = new Member(userid, email, "admin");
                        Map<String, Object> memberValues = member.toMap();
                        Map<String, Object> childMemberUpdates = new HashMap<>();

                        childMemberUpdates.put(userid, memberValues);
                        mFirebaseDatabase_Group.child(groupid).child("members").updateChildren(childMemberUpdates);
                    }
                    else {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                }
            });


            // add group id into user of Users of Firebase
            Map<String, Object> userGroup = new HashMap<>();
            userGroup.put(groupid, groupid);
            mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups").updateChildren(userGroup);

            NavHostFragment.findNavController(NewGroupFragment.this)
                    .navigate(R.id.action_newGroupFragment_to_GroupListFragment);
        });
    }
}