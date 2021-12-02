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

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

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


        //get number of group()
        int numberOfGroup = 0;
        if(getArguments() != null) {
            numberOfGroup = getArguments().getInt("NUMBER_OF_GROUP");
            Log.d("NEWGROUPFRAG", "The number of group is " + numberOfGroup);
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("group");

        EditText txtViewNewGroupName = view.findViewById(R.id.txtViewNewGroupName);
        Button btnAddNewGroup = view.findViewById(R.id.btnAddNewGroup);
        int nextGroupId = numberOfGroup;
        btnAddNewGroup.setOnClickListener((View v) -> {
            //String key = mFirebaseDatabase.push().getKey();
            String key = String.valueOf(nextGroupId);
            Group group = new Group(txtViewNewGroupName.getText().toString(), new ArrayList<Member>());
            Map<String, Object> groupValues = group.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(key, groupValues);
            mFirebaseDatabase.updateChildren(childUpdates);

            NavHostFragment.findNavController(NewGroupFragment.this)
                    .navigate(R.id.action_newGroupFragment_to_GroupListFragment);
        });
    }
}