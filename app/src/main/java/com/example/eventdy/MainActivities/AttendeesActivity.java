package com.example.eventdy.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.eventdy.R;
import com.example.eventdy.Resources.User;
import com.example.eventdy.Resources.UserAdapter;
import com.example.eventdy.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttendeesActivity extends AppCompatActivity {
    
    String baseURL = "https://eventdy.herokuapp.com", attendeesURL;
    private String url = "url";

    //volley request queue
    RequestQueue requestQueue;

    //Instantiating the views
    ProgressBar progressBar;

    //Recycler view stuff
    RecyclerView RVattendersList;
    ArrayList<User> attendersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);
        
        //getting the url for the attendees list from the intent
        Intent intent = getIntent();
        attendeesURL = intent.getExtras().getString(url);

        //Initializing the request queue
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        //Referencing the views
        progressBar = findViewById(R.id.pb_attendees);

        //Recycler view stuff
        RVattendersList = findViewById(R.id.rv_events_list_attendees);
        attendersList = new ArrayList<>();

        JsonObjectRequest getEventAttenders= new JsonObjectRequest(attendeesURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                updateList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AttendeesActivity.this, "Error while getting the joining details of event!", Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        requestQueue.add(getEventAttenders);
    }

    private void updateList(JSONObject response){
        try {
            JSONArray eventAttenders = response.getJSONArray("attenders");
            int i;
            if(eventAttenders.length() == 0){
                Toast.makeText(AttendeesActivity.this, "No attendees for the event!", Toast.LENGTH_SHORT).show();
                finish();
            }
            for(i=0;i<eventAttenders.length();i++){
                JSONObject jsonObject = eventAttenders.getJSONObject(i);
                try{
                    attendersList.add(i,new User(jsonObject.getString("_id"),jsonObject.getString("username"),jsonObject.getString("bio")));
                }catch (JSONException e){
                    attendersList.add(i,new User(jsonObject.getString("_id"),jsonObject.getString("username"),""));
                }
            }

            Log.i("size", String.valueOf(attendersList.size()));
            RVattendersList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            RVattendersList.setAdapter(new UserAdapter(attendersList,getApplicationContext()));
        } catch (JSONException e) {
            Toast.makeText(AttendeesActivity.this, "No attendees for the event!", Toast.LENGTH_SHORT).show();
        }
    }
}