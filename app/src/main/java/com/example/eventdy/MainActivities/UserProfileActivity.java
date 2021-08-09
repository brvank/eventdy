package com.example.eventdy.MainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class UserProfileActivity extends AppCompatActivity {
    //Required variables
    String ID = "_id" , baseURL = "https://eventdy.herokuapp.com" , getProfileURL = baseURL + "/profile/";
    String id;

    //Volley request queue
    RequestQueue requestQueue;

    //Shared preferences
    SharedPreferences sharedPreferences;

    //Recycler view stuff
    RecyclerView RVeventsCreated, RVeventsJoined;
    ArrayList<Events> eventsCreated , eventsJoined;
    EventsAdapter eventsCreatedAdapter, eventsJoinedAdapter;
    ProgressBar progressBar;

    //Initializing the views
    TextView userNameTextView, userBioTextView, userEmailTextView, dateJoinedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Volley request queue
        requestQueue = VolleySingleton.getInstance(UserProfileActivity.this).getRequestQueue();

        //Initializing the shared preferences
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);

        //Getting the user id from the intent
        Intent intent = getIntent();
        id = intent.getExtras().getString(ID);

        getSupportActionBar().setTitle("Profile");

        //Referencing the views
        userNameTextView = findViewById(R.id.tv_username_user_profile);
        userBioTextView = findViewById(R.id.tv_bio_user_profile);
        userEmailTextView = findViewById(R.id.tv_email_user_profile);
        dateJoinedTextView = findViewById(R.id.tv_date_joined_user_profile);
        progressBar = findViewById(R.id.pb_profile);

        //Recycler view stuff
        RVeventsCreated = findViewById(R.id.rv_events_created_profile);
        RVeventsJoined = findViewById(R.id.rv_events_joined_profile);
        eventsCreated = new ArrayList<>();
        eventsJoined = new ArrayList<>();

        getUserProfile();
    }

    private void getUserProfile(){
        JsonObjectRequest getProfileRequest = new JsonObjectRequest(getProfileURL + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                createProfile(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UserProfileActivity.this, "Unable to fetch the profile!", Toast.LENGTH_SHORT).show();
            }
        }){
            //Creating the header for authorized network access by passing the cookies to the server
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> cookie = new HashMap<>();
                cookie.put("cookie",sharedPreferences.getString("cookie",""));
                return cookie;
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        requestQueue.add(getProfileRequest);
    }

    private void createProfile(JSONObject response){
        String userName , userBio, userEmail, dateJoined;
        try {
            userName = response.getString("username");
            dateJoined = response.getString("date");
            userEmail = response.getString("email");

            getSupportActionBar().setTitle(userName);

            //assigning the views with required values
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);
            dateJoinedTextView.setText(dateJoined);

        }catch (JSONException e){
            int i= 0;
        }
        //TODO : bio is risky
        //userBio = response.getString("bio");
        try{
            userBio = response.getString("bio");
            userBioTextView.setText(userBio);
        }catch (JSONException e){
            Toast.makeText(UserProfileActivity.this, "Unable to fetch the complete profile!", Toast.LENGTH_SHORT).show();
        }


        try{
            //getting the events created array
            JSONArray eventsCreatedArray = (JSONArray) response.get("eventsCreated");
            int i;
            for(i=0;i<eventsCreatedArray.length();i++){
                JSONObject jsonObject = eventsCreatedArray.getJSONObject(i);
                eventsCreated.add(i,new Events(jsonObject.getString("title"),"",jsonObject.getString("_id")));
            }
            RVeventsCreated.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            RVeventsCreated.setAdapter(new EventsAdapter(eventsCreated,getApplicationContext()));
        }catch (JSONException e){
            Toast.makeText(UserProfileActivity.this, "No events created by the user!", Toast.LENGTH_SHORT).show();
        }

        try{
            //getting the events joined array
            JSONArray eventsJoinedArray = (JSONArray) response.get("eventsAttended");
            int i;
            for(i=0;i<eventsJoinedArray.length();i++){
                JSONObject jsonObject = eventsJoinedArray.getJSONObject(i);
                eventsJoined.add(i,new Events(jsonObject.getString("title"),"",jsonObject.getString("_id")));
                Log.i("events joined", String.valueOf(i));
            }
            RVeventsJoined.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            RVeventsJoined.setAdapter(new EventsAdapter(eventsJoined,getApplicationContext()));
        }catch (JSONException e){
            Toast.makeText(UserProfileActivity.this, "No events joined by the user!", Toast.LENGTH_SHORT).show();
        }
        //TODO: to add the refresh options in this activity with stable one

    }
}