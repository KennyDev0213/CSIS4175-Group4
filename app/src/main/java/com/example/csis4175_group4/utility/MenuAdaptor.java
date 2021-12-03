package com.example.csis4175_group4.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csis4175_group4.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdaptor extends RecyclerView.Adapter<MenuAdaptor.ViewHolder> {

    private Context context;
    private Option[] options;

    public MenuAdaptor(Context context, Option[] data){
        this.context = context;
        options = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        ImageView thumb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.opt_text);
            thumb = itemView.findViewById(R.id.opt_img);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.recyclerview_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return options.length;
    }
}
