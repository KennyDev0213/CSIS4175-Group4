package com.example.csis4175_group4.fragments.albummanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.csis4175_group4.AlbumManagerActivity;
import com.example.csis4175_group4.MainActivity;
import com.example.csis4175_group4.R;

import com.example.csis4175_group4.fragments.groupmanager.Group;
import com.example.csis4175_group4.fragments.groupmanager.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class AlbumListFragment extends Fragment implements AlbumListAdapter.ItemClickListener {

    List<String> userGroupList;
    List<Group> groupList;
    List<Album> albumList;
    RecyclerView recyclerView;
    AlbumListAdapter albumListAdapter;
    private AlbumViewModel albumSharedViewModel;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase_Albums;
    private DatabaseReference mFirebaseDatabase_Users;
    private DatabaseReference mFirebaseDatabase_Groups;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = AlbumManagerActivity.class.getSimpleName();

    public interface UserListCallback {
        void onCallback(List<String> list);
    }
    public void readUserListData(AlbumListFragment.UserListCallback callback) {
        mFirebaseDatabase_Users.child(mFirebaseUser.getUid()).child("groups") //current user's group lists
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userGroupList.clear();

                        for(DataSnapshot child: snapshot.getChildren()) {
                            userGroupList.add(child.getKey()); //group id
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
    public void readGroupListData(AlbumListFragment.GroupListCallback callback) {
        mFirebaseDatabase_Groups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();

                for(DataSnapshot child : snapshot.getChildren()) {
                    Group group = child.getValue(Group.class);

                    //check if a group of Groups is in group of current user
                    //means current user can access to own group or admin of the group
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
                callback.onCallback(groupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface AlbumListCallback {
        void onCallback(List<Album> list);
    }
    public void readAlbumListData(AlbumListFragment.AlbumListCallback callback) {
        mFirebaseDatabase_Albums.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                albumList.clear();

                for(DataSnapshot child : snapshot.getChildren()) {
                    Album album = child.getValue(Album.class);

                    //check if a album of Albums is in album of current user
                    //means current user can access to own album or admin of the group
                    for(int i = 0; i < groupList.size(); i++) {
                        if (groupList.get(i).getId().equals(album.getGroupId())) { // check owner of album
                            albumList.add(album);
                        }
                    }
                }
                albumListAdapter.setAlbumList(albumList);
                callback.onCallback(albumList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public AlbumListFragment() {
        // Required empty public constructor
    }

    public static AlbumListFragment newInstance() {
        return new AlbumListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_album_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumSharedViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);

        albumList = new ArrayList<>();
        groupList = new ArrayList<>();
        albumListAdapter = new AlbumListAdapter(albumList);
        albumListAdapter.setListener(this);

        recyclerView = view.findViewById(R.id.recyclerViewAlbumList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter((albumListAdapter));

        userGroupList = new ArrayList<>();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase_Albums = mFirebaseInstance.getReference("Albums");
        mFirebaseDatabase_Users = mFirebaseInstance.getReference("Users");
        mFirebaseDatabase_Groups = mFirebaseInstance.getReference("Groups");

        //After getting user data, get album data because of async issue
        readUserListData(new AlbumListFragment.UserListCallback() {
            @Override
            public void onCallback(List<String> list) {
                userGroupList = list;

                readGroupListData(new AlbumListFragment.GroupListCallback() {
                    @Override
                    public void onCallback(List<Group> list) {
                        groupList = list;

                        readAlbumListData(new AlbumListFragment.AlbumListCallback() {
                            @Override
                            public void onCallback(List<Album> list) {
                                albumList = list;
                                albumListAdapter.setAlbumList(albumList);
                            }
                        });
                    }
                });
            }
        });

        FloatingActionButton fabAddAlbum = view.findViewById(R.id.fabAddAlbum);
        fabAddAlbum.setOnClickListener((View v) -> {
            Bundle bundle= new Bundle();
            Navigation.findNavController(v).navigate(R.id.action_albumListFragment_to_newAlbumFragment, bundle);
        });

        Button btn = view.findViewById(R.id.btnCloseAlbumList);
        btn.setOnClickListener((View v) -> {
            Intent intent=new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onListItemClick(Album album, int position) {
        albumSharedViewModel.setSelectedAlbum(album);
        albumSharedViewModel.setSelectedAlbumId(albumList.get(position).getId());
    }

    @Override
    public void onListItemDelete(Album album, int position) {
        // check validation for group owner, not admin
        // group owner can only change group of the album to other group
        if (userGroupList.contains(album.getGroupId()) == false) {

            Toast.makeText(this.getContext(),
                    "You're not group owner that was made this group.\nGroup owner can only delete the album.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        albumSharedViewModel.setSelectedAlbum(album);
        albumSharedViewModel.setSelectedAlbumId(albumList.get(position).getId());

        albumList.remove(position);
        albumListAdapter.setAlbumList(albumList);

        mFirebaseDatabase_Albums.child(album.getId()).removeValue();
    }
}