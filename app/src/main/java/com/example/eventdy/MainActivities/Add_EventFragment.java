package com.example.eventdy.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.eventdy.R;
import com.example.eventdy.Resources.Events;
import com.example.eventdy.Resources.EventsAdapter;
import com.example.eventdy.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Add_EventFragment extends Fragment {

    //Required URLs
    String baseURL = "https://eventdy.herokuapp.com" , getProfileURL = baseURL + "/profile";

    String userID;

    //Main view
    View view;

    //Volley request queue
    RequestQueue requestQueue;

    //Initializing the views
    ProgressBar progressBar;

    //Recycler View related stuff
    RecyclerView recyclerView;
    EventsAdapter eventsAdapter;

    //array list for the recycler view
    ArrayList<Events> events;

    //Shared preferences
    SharedPreferences sharedPreferences;

    public Add_EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Referencing the shared preferences
        sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);

        //Volley request queue
        requestQueue = VolleySingleton.getInstance(requireContext()).getRequestQueue();

        setHasOptionsMenu(true);

        //Getting the user id
        userID = sharedPreferences.getString("_id","00");

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                getCreatedEvents();
                break;
            case R.id.add:
                Intent intent = new Intent(getContext(),Add_EventScreenActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add__event, container, false);

        //Getting the views
        recyclerView = view.findViewById(R.id.rv_my_events_list_add);
        progressBar = view.findViewById(R.id.pb_add_parent);

        //Recycler view stuff
        events = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsAdapter = new EventsAdapter(events,getContext());
        recyclerView.setAdapter(eventsAdapter);


        getCreatedEvents();

        return view;
    }

    private void getCreatedEvents(){
        if(userID.equals("00")){
            Toast.makeText(requireContext(), "Do logout and then login!", Toast.LENGTH_SHORT).show();
        }else{
            JsonObjectRequest getProfile = new JsonObjectRequest(getProfileURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    updateEvents(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error while loading events!", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String , String> cookie = new HashMap<>();
                    cookie.put("cookie",sharedPreferences.getString("cookie",""));
                    return cookie;
                }
            };

            progressBar.setVisibility(View.VISIBLE);
            requestQueue.add(getProfile);
        }
    }

    private void updateEvents(JSONObject response){
        try {
            JSONArray eventsCreated = response.getJSONArray("eventsCreated");
            int i;
            events.clear();
            for(i=0;i<eventsCreated.length();i++){
                JSONObject jsonObject = eventsCreated.getJSONObject(i);
                events.add(i,new Events(jsonObject.getString("title"),"",jsonObject.getString("_id")));
            }

            //Updating the recycler view adapter
            eventsAdapter.notifyDataSetChanged();
        }catch (JSONException e){
            Toast.makeText(requireContext(), "Retry!", Toast.LENGTH_SHORT).show();
        }
    }
}