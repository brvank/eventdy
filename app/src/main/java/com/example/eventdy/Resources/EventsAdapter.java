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
import com.example.eventdy.R;

import java.util.ArrayList;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.holder> {

    ArrayList<Events> eventsList;
    //auxiliary objects for creating intent
    private String ID="_id", viewDetailsText = "View Details";
    private Context context;

    public EventsAdapter(ArrayList<Events> eventsList, Context context) {
        this.eventsList = eventsList;
        //context to create intent for each activity
        this.context = context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating the layout for recycler view for each unit
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.event_layout,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(holder holder, int position) {

        String id;
        //assigning each part with respective data
        holder.eventName.setText(eventsList.get(position).getEventTitle());
        holder.eventCategory.setText(eventsList.get(position).getEventCategory());
        holder.viewEventDetails.setText(viewDetailsText);

        //getting the ID of the event to pass into the bundle
        id = eventsList.get(position).getEventId();

        //setting on click listener to the 'view details' tab
        holder.viewEventDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ID,id);
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public static class holder extends RecyclerView.ViewHolder{

        //for referencing the views from event layout
        TextView eventName, eventCategory, viewEventDetails;

        public holder(View itemView) {
            super(itemView);
            //instantiating the views from event layout via item view
            eventName = itemView.findViewById(R.id.tv_event_title_layout);
            eventCategory = itemView.findViewById(R.id.tv_event_category_layout);
            viewEventDetails = itemView.findViewById(R.id.tv_view_details_layout);
        }
    }
}
