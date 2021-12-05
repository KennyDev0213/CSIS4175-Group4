package com.example.csis4175_group4.fragments.albummanager;

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

public class AlbumListAdapter extends RecyclerView.Adapter{
    private List<Album> albumList;
    private ItemClickListener mListener;

    interface ItemClickListener {
        void onListItemClick(Album album, int position);
        void onListItemDelete(Album album, int position);
    }

    public ItemClickListener getListener() {
        return mListener;
    }

    public void setListener(ItemClickListener mListener) {
        this.mListener = mListener;
    }

    public AlbumListAdapter(List<Album> albumList) {
        this.albumList = albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_album_list, parent, false);
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);

        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
        Album curAlbum = albumList.get(position);
        albumViewHolder.txtViewAlbumName.setText(curAlbum.getTitle());
        albumViewHolder.imgBtnAlbumDetail.setOnClickListener(view ->{
            mListener.onListItemClick(curAlbum, position);
            Navigation.findNavController(view).navigate(R.id.action_albumListFragment_to_albumDetailFragment);
        });

        albumViewHolder.imgBtnAlbumDelete.setOnClickListener(view ->{
            mListener.onListItemDelete(curAlbum, position);
            Navigation.findNavController(view).navigate(R.id.albumListFragment);
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        private TextView txtViewAlbumName;
        private ImageButton imgBtnAlbumDetail;
        private ImageButton imgBtnAlbumDelete;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewAlbumName = itemView.findViewById(R.id.txtViewAlbumName);
            imgBtnAlbumDetail = itemView.findViewById(R.id.imgBtnAlbumDetail);
            imgBtnAlbumDelete = itemView.findViewById(R.id.imgBtnAlbumDelete);
        }
    }
}
