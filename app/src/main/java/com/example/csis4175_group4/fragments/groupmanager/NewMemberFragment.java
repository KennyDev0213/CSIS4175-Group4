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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.csis4175_group4.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewMemberFragment extends Fragment {

    private GroupViewModel groupSharedViewModel;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get number of member
        int numberOfMember = 0;
        if(getArguments() != null) {
            numberOfMember = getArguments().getInt("NUMBER_OF_MEMBER");
            Log.d("NEWMEMBERFRAG", "The number of member is " + numberOfMember);
        }

        groupSharedViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("group").child(""+groupSharedViewModel.getSelectedGroupId().getValue()).child("members");

        EditText txtViewNewMemberEmail = view.findViewById(R.id.txtViewNewMemberEmail);
        RadioGroup rdoRoleGroup = view.findViewById(R.id.rdoRoleGroup);
        Button btnAddMember = view.findViewById(R.id.btnAddMember);

        int nextMemberId = numberOfMember;
        btnAddMember.setOnClickListener((View v) -> {

            String role;
            int selectedRdoId =  rdoRoleGroup.getCheckedRadioButtonId();
            if(selectedRdoId != -1) {
                if(selectedRdoId == R.id.rdoAdmin)
                    role = "admin";
                else
                    role = "general";
            } else {
                Toast.makeText(this.getContext(), "Please select the role", Toast.LENGTH_SHORT).show();
                return;
            }

            //String key = mFirebaseDatabase.push().getKey();
            String key = String.valueOf(nextMemberId);
            Member member = new Member(txtViewNewMemberEmail.getText().toString(), role);
            Map<String, Object> memberValues = member.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(key, memberValues);
            mFirebaseDatabase.updateChildren(childUpdates);

            NavHostFragment.findNavController(NewMemberFragment.this)
                    .navigate(R.id.action_newMemberFragment_to_GroupDetailFragment);
        });
    }
}