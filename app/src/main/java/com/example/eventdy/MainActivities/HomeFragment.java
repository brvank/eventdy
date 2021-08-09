package com.example.eventdy.MainActivities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    //Base and events URL
    private final String baseURL = "https://eventdy.herokuapp.com" , getEventURL = baseURL+"/events";

    //Main view
    View view;

    //Volley request queue
    RequestQueue requestQueue;

    //Shared preferences
    SharedPreferences sharedPreferences;

    //array list for the recycler view
    ArrayList<Events> events = new ArrayList<>();
    ProgressBar progressBar;

    //loading button
    Button loadButton;
    int page = 1;

    public HomeFragment() {
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
        view = inflater.inflate(R.layout.fragment_home, container, false);

        loadButton = view.findViewById(R.id.btn_load_more_home);
        progressBar = view.findViewById(R.id.pb_home_parent);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        //Function for getting the event data from the server
        getData();

        return view;
    }

    private void getData(){

        String getURL = getEventURL + "?page=" + String.valueOf(page);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    //Function for loading the data into the recycler view
                    loadingData(response);
                    page++;
                } catch (JSONException exception) {
                    Toast.makeText(requireContext(),"Please refresh",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(),"Please refresh",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
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
        requestQueue.add(jsonArrayRequest);

    }

    //Function for loading the data into the recycler view
    private void loadingData(JSONArray response) throws JSONException {

        int i;
        for(i=0;i<response.length();i++){
            JSONObject jsonObject = response.getJSONObject(i);
            events.add(i,new Events(jsonObject.getString("title"),jsonObject.getString("category"),jsonObject.getString("_id")));
        }

        //Feeding data to recycler view
        RecyclerView recyclerView = view.findViewById(R.id.rv_events_list_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new EventsAdapter(events, getContext()));
    }
}