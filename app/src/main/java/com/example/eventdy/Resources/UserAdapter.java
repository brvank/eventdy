package com.example.eventdy.Resources;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventdy.MainActivities.EventActivity;
import com.example.eventdy.MainActivities.UserProfileActivity;
import com.example.eventdy.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.holder> {
    ArrayList<User> usersList ;
    private String ID="_id", profile="Profile";
    private Context context;

    public UserAdapter(ArrayList<User> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating the layout for recycler view for each unit
        LayoutInflater layoutInflater = LayoutInflater.from(context.getApplicationContext());
        View view = layoutInflater.inflate(R.layout.user_layout,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(UserAdapter.holder holder, int position) {
        String id;
        //assigning each part with respective data
        holder.userName.setText(usersList.get(position).getName());
        holder.userBio.setText(usersList.get(position).getBio());
        holder.profileTextView.setText(profile);

        //getting the ID of the user to pass into the bundle
        id = usersList.get(position).getId();

        //setting on click listener to the 'view details' tab
        holder.profileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ID,id);
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class holder extends RecyclerView.ViewHolder{

        //For referencing the views in the layout
        TextView userName, userBio, profileTextView;

        public holder(View itemView) {
            super(itemView);

            //Instantiating the views
            userName = itemView.findViewById(R.id.tv_user_name_layout);
            userBio = itemView.findViewById(R.id.tv_user_bio_layout);
            profileTextView = itemView.findViewById(R.id.tv_user_profile_layout);

        }
    }
}
