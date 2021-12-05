package com.example.csis4175_group4.utility;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.csis4175_group4.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdaptor extends RecyclerView.Adapter<MenuAdaptor.ViewHolder> {

    private final Context context; //the activity the menu is in
    private final Option[] options; //the options to be displayed

    public MenuAdaptor(Context context, Option[] data){
        this.context = context;
        options = data;
    }

    //option class
    public static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout;
        TextView text;
        ImageView thumb;
        private Intent activity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.opt_text);
            thumb = itemView.findViewById(R.id.opt_img);
            layout = itemView.findViewById(R.id.recyclerLayout);
        }

        //getters and setters for activity intent
        public void setActivity(Intent activityIntent){
            activity = activityIntent;
        }

        public Intent getActivityIntent(){
            return activity;
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
        holder.text.setText(options[position].getTitle());
        holder.thumb.setImageResource(options[position].getThumb());
        holder.setActivity(options[position].getActivity());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when the option view is tapped, start the activity that is binded to the view
                context.startActivity(holder.getActivityIntent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.length;
    }
}
