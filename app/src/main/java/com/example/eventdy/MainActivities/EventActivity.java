package com.example.eventdy.MainActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.eventdy.R;
import com.example.eventdy.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private final String baseURL = "https://eventdy.herokuapp.com";
    private String getEventURL = baseURL + "/event/", attendURL = baseURL+"/attend/", attendeesURL = baseURL+"/event-attenders/";
    private final String ID="_id";
    private String id, organizerId, userId;

    //Master boolean
    boolean master;

    //Volley request queue
    RequestQueue requestQueue;

    //Initializing the UI controllers
    TextView eventTitleTextView, eventAboutTextView, eventCategoryTextView, eventExcerptTextView, eventDateTextView, eventEdateTextView, eventLocationTextView;
    ProgressBar progressBar;

    //Text views for join , attendees and organiser
    TextView joinAndAttendees, Organizer;

    //Shared preferences
    SharedPreferences sharedPreferences;

    //TODO : to create the feature to store the joined and created events into the database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Instantiating the shared preferences
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);

        getSupportActionBar().setTitle("Event");

        //Getting the event id from the intent
        Intent intent = getIntent();
        id = intent.getExtras().getString(ID);

        //Getting the user id
        userId = sharedPreferences.getString("_id", "00");
        if(userId.equals("00")){
            Toast.makeText(EventActivity.this, "Closed", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Setting the master to be not true for the event
        master = false;

        //Referencing the views
        eventTitleTextView      = findViewById(R.id.tv_title_event);
        eventAboutTextView      = findViewById(R.id.tv_about_event);
        eventCategoryTextView   = findViewById(R.id.tv_category_event);
        eventAboutTextView      = findViewById(R.id.tv_about_event);
        eventExcerptTextView    = findViewById(R.id.tv_excerpt_event);
        eventDateTextView       = findViewById(R.id.tv_date_event);
        eventEdateTextView      = findViewById(R.id.tv_eDate_event);
        eventLocationTextView   = findViewById(R.id.tv_location_event);
        //For organizer
        Organizer               = findViewById(R.id.tv_organizer_event);
        joinAndAttendees        = findViewById(R.id.tv_join_attendee_event);
        //Progress bar
        progressBar = findViewById(R.id.pb_event);

        //Initializing the request queue
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        getEventDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Delete:
                if(master){
                    deleteEvent();
                }else{
                    Toast.makeText(EventActivity.this, "You are not allowed to delete", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Refresh:
                getEventDetails();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getEventDetails(){
        //volley request for getting the list of attendees

        JsonObjectRequest getEventAttenders= new JsonObjectRequest(attendeesURL + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    checkIfJoined(response);
                } catch (JSONException e) {
                    Toast.makeText(EventActivity.this, "Be the first one to join it!",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EventActivity.this, "Error while getting the joining details of event!", Toast.LENGTH_SHORT).show();
            }
        });

        //volley request for getting the event details
        JsonObjectRequest getEventDetails = new JsonObjectRequest(getEventURL + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                //Function to create the view and setting the on click listeners
                try {
                    createViews(response);
                } catch (JSONException e) {
                    Toast.makeText(EventActivity.this, "Some fields missing!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.VISIBLE);
                requestQueue.add(getEventAttenders);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EventActivity.this, "Error while getting the event!", Toast.LENGTH_SHORT).show();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        requestQueue.add(getEventDetails);
    }
    //Function to create the views at the start of the activity
    private void createViews(JSONObject response) throws JSONException {

        //getting the event organiser id
        JSONObject organizerDetails = (JSONObject) response.get("organizer");
        organizerId = organizerDetails.getString("_id");

        Organizer.setVisibility(View.VISIBLE);

        if(organizerId.equals(userId)){
            master = true;
            Organizer.setText("Edit");
            Organizer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("_id",id);
                    Intent intent = new Intent(EventActivity.this, UpdateEventActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }else{
            Organizer.setText("Organizer");
            Organizer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("_id",organizerId);
                    Intent intent = new Intent(EventActivity.this, UserProfileActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }


        eventTitleTextView    .setText(response.getString("title"));
        eventAboutTextView    .setText(response.getString("about"));
        eventCategoryTextView .setText(response.getString("category"));
        eventAboutTextView    .setText(response.getString("about"));
        eventDateTextView     .setText(response.getString("date"));
        eventEdateTextView    .setText(response.getString("endingDate"));
        eventLocationTextView .setText(response.getString("location"));
        eventExcerptTextView  .setText(response.getString("excerpt"));

        getSupportActionBar().setTitle(response.getString("title"));

        //navigation text views

    }

    //Function to check if the event is joined already by the user
    private void checkIfJoined(JSONObject response) throws JSONException {
        //boolean value to check if joining the event is allowed or not
        boolean joinAllowed = true;
        JSONArray attenders = (JSONArray) response.get("attenders");
        int i;
        for(i=0;i<attenders.length();i++){
            if(attenders.getJSONObject(i).getString("_id").equals(userId) || master){
                joinAllowed = false;
                setAttendees();
                break;
            }
        }
        //If joining allowed but master
        if(master){
            joinAllowed = false;
            setAttendees();
        }
        if(attenders.length() == 0 && joinAllowed) {
            Toast.makeText(EventActivity.this, "Be the first one to join it!", Toast.LENGTH_SHORT).show();
            joinOptions();
        }else if(joinAllowed){
            joinOptions();
        }
    }

    //Function to open the join options event
    private void joinOptions(){
        joinAndAttendees.setVisibility(View.VISIBLE);
        joinAndAttendees.setText("Join");
        joinAndAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest joinRequest = new StringRequest(Request.Method.POST,attendURL + id, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        setAttendees();
                        Toast.makeText(EventActivity.this, "Event joined!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EventActivity.this, "Error while joining event!", Toast.LENGTH_SHORT).show();
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
                requestQueue.add(joinRequest);
            }
        });
    }

    private void setAttendees(){
        joinAndAttendees.setVisibility(View.VISIBLE);
        joinAndAttendees.setText("Attendees");
        joinAndAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("url",attendeesURL+id);
                Intent intent = new Intent(EventActivity.this,AttendeesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void deleteEvent(){
        String deleteURL = getEventURL + id;
        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EventActivity.this, "Event Deleted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EventActivity.this, "Event deletion failed!", Toast.LENGTH_SHORT).show();
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
        requestQueue.add(deleteRequest);
    }
}