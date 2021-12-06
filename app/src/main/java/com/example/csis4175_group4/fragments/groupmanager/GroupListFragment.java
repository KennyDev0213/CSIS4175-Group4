package com.example.csis4175_group4.fragments.groupmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csis4175_group4.GroupManagerActivity;
import com.example.csis4175_group4.LoginActivity;
import com.example.csis4175_group4.MainActivity;
import com.example.csis4175_group4.R;
import com.example.csis4175_group4.fragments.albummanager.Album;
import com.example.csis4175_group4.fragments.albummanager.AlbumListFragment;
import com.example.csis4175_group4.viewmodels.AppViewModel;
import com.example.csis4175_group4.viewmodels.ForgetPassword;
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
                    for(int i = 0; i < userGroupList.size(); i++) {
                        if (userGroupList.get(i).equals(group.getId())) { // check owner of group
                            groupList.add(group);
                        }
                    }

                    //check if current user is a member of group in Groups DB.
                    //If user is member, add the group into groupList for making group for album
                    HashMap<String, Member> members = group.getMembers();
                    for(Member m : members.values()) {
                        if(m.getUid().equals(mFirebaseUser.getUid()) &&
                                !groupList.contains(group.getId())) {
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

        Button btn = view.findViewById(R.id.btnCloseGroupList);
        btn.setOnClickListener((View v) -> {
            Intent intent=new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onListItemClick(Group group, int position) {
        groupSharedViewModel.setSelectedGroup(group);
        groupSharedViewModel.setSelectedGroupId(groupList.get(position).getId());

        // check owner of group
        groupSharedViewModel.setIsGroupOwner(false);
        for(int i = 0; i < userGroupList.size(); i++) {
            if (userGroupList.get(i).equals(group.getId())) {
                groupSharedViewModel.setIsGroupOwner(true);
                break;
            }
        }

    }

    @Override
    public void onListItemDelete(Group group, int position) {

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

        // check validation if current user is general group member
        // if the user is the general user, the user cannot delete the group.
        HashMap<String, Member> members = group.getMembers();
        for(Member m : members.values()) {
            if(m.getUid().equals(mFirebaseUser.getUid()) &&
                m.getRole().equals("general")) {
                Toast.makeText(GroupListFragment.this.getContext(),
                        "You cannot delete the group because you are general member in this group.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        groupSharedViewModel.setSelectedGroup(group);
        groupSharedViewModel.setSelectedGroupId(groupList.get(position).getId());

        // check owner of group
        groupSharedViewModel.setIsGroupOwner(false);
        for(int i = 0; i < userGroupList.size(); i++) {
            if (userGroupList.get(i).equals(group.getId())) {
                groupSharedViewModel.setIsGroupOwner(true);
                break;
            }
        }

        groupList.remove(position);
        groupListAdapter.setGroupList(groupList);

        mFirebaseDatabase_Group.child(group.getId()).removeValue();

        Map<String, Object> userGroup = new HashMap<>();
        userGroup.put("id", group.getId());
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups").child(group.getId()).removeValue();
    }
}