package com.example.csis4175_group4.fragments.groupmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csis4175_group4.R;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter{
    List<Group> groupList;

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

        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
        Group curGroup = groupList.get(position);
        groupViewHolder.txtViewGroupName.setText(curGroup.getName());
        groupViewHolder.imgBtnGroupDetail.setOnClickListener(view ->{
            Navigation.findNavController(view).navigate(R.id.action_GroupListFragment_to_GroupDetailFragment);
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
