package com.example.eventdy.MainActivities;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import java.util.Objects;

public class SearchFragment extends Fragment {

    //Base and events URL
    private final String baseURL = "https://eventdy.herokuapp.com" , getEventURL = baseURL+"/events";

    //Main view
    View view;

    //Volley request queue
    RequestQueue requestQueue;

    //Shared preferences
    SharedPreferences sharedPreferences;

    //Initializing the views
    EditText byCategoryEditText, byTitleEditText;
    Button searchButton;
    ProgressBar progressBar;

    //Recycler View related stuff
    RecyclerView recyclerView;
    EventsAdapter eventsAdapter;

    //Swipe refresh layout
    SwipeRefreshLayout swipeRefreshLayout;

    //array list for the recycler view
    ArrayList<Events> events;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing the request queue
        requestQueue = VolleySingleton.getInstance(requireContext()).getRequestQueue();

        //Initializing the shared preference
        sharedPreferences = requireActivity().getSharedPreferences("User",MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        //Swipe refresh layout
        swipeRefreshLayout = view.findViewById(R.id.swipe_search);

        //Referencing the views
        byTitleEditText = view.findViewById(R.id.et_title_search);
        byCategoryEditText = view.findViewById(R.id.et_category_search);
        searchButton = view.findViewById(R.id.btn_search_search);
        recyclerView = view.findViewById(R.id.rv_events_list_search);
        progressBar = view.findViewById(R.id.pb_search_parent);

        //Recycler view stuff
        events = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsAdapter = new EventsAdapter(events,getContext());
        recyclerView.setAdapter(eventsAdapter);

        //setting the event listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEvent();
            }
        });

        //setting the refresh listener on swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchEvent();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void searchEvent(){
        String category = byCategoryEditText.getText().toString(), title = byTitleEditText.getText().toString();

        if(category.equals("") && title.equals("")){
            Toast.makeText(requireContext(), "Please fill the search boxes!", Toast.LENGTH_SHORT).show();
        }else {
            String mainURL = getEventURL +"?search=" + title +"&" + "category=" + category;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(mainURL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        //Function for loading the data into recycler view
                        loadingData(response);
                    } catch (JSONException exception) {
                        Toast.makeText(requireContext(),"Please refresh",Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            progressBar.setVisibility(View.VISIBLE);
            requestQueue.add(jsonArrayRequest);
        }


    }

    //Function for loading the data into the recycler view
    private void loadingData(JSONArray response) throws JSONException {

        int i, j = events.size();
        events.clear();
        for(i=0;i<response.length();i++){
            JSONObject jsonObject = response.getJSONObject(i);
            events.add(i,new Events(jsonObject.getString("title"),jsonObject.getString("category"),jsonObject.getString("_id")));
        }

        //Updating the recycler view adapter
        eventsAdapter.notifyDataSetChanged();
    }
}