package com.example.csis4175_group4.fragments.groupmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csis4175_group4.R;
import com.example.csis4175_group4.viewmodels.AppViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter{
    private List<Group> groupList;
    private ItemClickListener mListener;

    private FirebaseUser mFirebaseUser;

    interface ItemClickListener {
        void onListItemClick(Group group, int position);
        void onListItemDelete(Group group, int position);
    }

    public ItemClickListener getListener() {
        return mListener;
    }

    public void setListener(ItemClickListener mListener) {
        this.mListener = mListener;
    }

    public GroupListAdapter(List<Group> groupList) {
        this.groupList = groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_list, parent, false);
        GroupViewHolder groupViewHolder = new GroupViewHolder(view);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
        Group curGroup = groupList.get(position);

        String memberRole = "";
        HashMap<String, Member> members = curGroup.getMembers();
        for(Member m : members.values()) {
            if(m.getUid().equals(mFirebaseUser.getUid())) {
                memberRole = m.getRole();
                break;
            }
        }
        if(memberRole.equals("")) {
            groupViewHolder.txtViewGroupName.setText(curGroup.getName() + "\n(Group owner)");
        } else {
            groupViewHolder.txtViewGroupName.setText(curGroup.getName() + "\n(Group member:" + memberRole + ")");
        }

        groupViewHolder.imgBtnGroupDetail.setOnClickListener(view ->{
            mListener.onListItemClick(curGroup, position);
            Navigation.findNavController(view).navigate(R.id.action_GroupListFragment_to_GroupDetailFragment);
        });

        groupViewHolder.imgBtnGroupDelete.setOnClickListener(view ->{
            mListener.onListItemDelete(curGroup, position);
            Navigation.findNavController(view).navigate(R.id.GroupListFragment);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView txtViewGroupName;
        private ImageButton imgBtnGroupDetail;
        private ImageButton imgBtnGroupDelete;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewGroupName = itemView.findViewById(R.id.txtViewGroupName);
            imgBtnGroupDetail = itemView.findViewById(R.id.imgBtnGroupDetail);
            imgBtnGroupDelete = itemView.findViewById(R.id.imgBtnGroupDelete);
        }
    }
}
