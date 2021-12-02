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

public class MemberListAdapter extends RecyclerView.Adapter{
    private List<Member> memberList;
    private ItemClickListener mListener;

    interface ItemClickListener {
        void onListItemDelete(Member member, int position);
    }

    public ItemClickListener getListener() {
        return mListener;
    }

    public void setListener(ItemClickListener mListener) {
        this.mListener = mListener;
    }

    public MemberListAdapter(List<Member> memberList) {
        this.memberList = memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_member_list, parent, false);
        MemberViewHolder memberViewHolder = new MemberViewHolder(view);

        return memberViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MemberViewHolder groupViewHolder = (MemberViewHolder) holder;
        Member curMember = memberList.get(position);
        groupViewHolder.txtViewMemberName.setText(curMember.getUserId());
        groupViewHolder.txtViewMemberRole.setText(curMember.getRole());
        groupViewHolder.imgBtnMemberDelete.setOnClickListener(view ->{
            mListener.onListItemDelete(curMember, position);
            Navigation.findNavController(view).navigate(R.id.GroupDetailFragment);
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {

        private TextView txtViewMemberName;
        private TextView txtViewMemberRole;
        private ImageButton imgBtnMemberDelete;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewMemberName = itemView.findViewById(R.id.txtViewMemberName);
            txtViewMemberRole = itemView.findViewById(R.id.txtViewMemberRole);
            imgBtnMemberDelete = itemView.findViewById(R.id.imgBtnMemberDelete);
        }
    }
}
