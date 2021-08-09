package com.example.eventdy.MainActivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.eventdy.R;
import com.example.eventdy.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add_EventScreenActivity extends AppCompatActivity {

    //Create event url and base url
    private final String baseURL = "https://eventdy.herokuapp.com", eventURL = "https://eventdy.herokuapp.com/event";
    //Creating required variables
    private String eventId, eventTitle, eventAbout, eventCategory, eventDate, eventEndingDate, eventLocation, eventExcerpt;
    private final String TITLE ="title", ABOUT ="about", DATE="date", eDATE="endingDate",EXCERPT="excerpt", CATEGORY="category", LOCATION="location", ID="_id";
    
    //Volley request queue
    RequestQueue requestQueue;

    //Shared preferences
    SharedPreferences sharedPreferences;

    //Initializing the views
    Button createButton ;
    EditText eventTitleEditText, eventAboutEditText, eventCategoryEditText,
            eventDateEditText, eventEndingDateEditText, eventLocationEditText, eventExcerptEditText;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_screen);

        //Initializing the shared preference
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);

        getSupportActionBar().setTitle("New Event");

        //Referencing the views
        createButton = findViewById(R.id.btn_create_event_screen);
        eventTitleEditText = findViewById(R.id.et_title_add_event_screen);
        eventAboutEditText = findViewById(R.id.et_about_add_event_screen);
        eventCategoryEditText = findViewById(R.id.et_category_add_event_screen);
        eventExcerptEditText = findViewById(R.id.et_excerpt_add_event_screen);
        eventDateEditText = findViewById(R.id.et_date_add_event_screen);
        eventEndingDateEditText = findViewById(R.id.et_endingDate_add_event_screen);
        eventLocationEditText = findViewById(R.id.et_location_add_event_screen);
        progressBar = findViewById(R.id.pb_create);
        
        //Instantiating the request queue
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //Setting onClickListener to the create button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    postingNewEventToServer();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Function to post a new event to the server
    private void postingNewEventToServer() throws JSONException {

        //Checking if the fields are filled or not
        if(!eventTitleEditText.getText().toString().equals("") && !eventAboutEditText.getText().toString().equals("") && !eventCategoryEditText.getText().toString().equals("") &&
                !eventDateEditText.getText().toString().equals("") && !eventLocationEditText.getText().toString().equals("") &&
                !eventEndingDateEditText.getText().toString().equals("") && !eventExcerptEditText.getText().toString().equals("")){

            //getting the edit text string values
            eventTitle = eventTitleEditText.getText().toString();
            eventAbout = eventAboutEditText.getText().toString();
            eventCategory = eventCategoryEditText.getText().toString();
            eventDate = eventDateEditText.getText().toString();
            eventLocation = eventLocationEditText.getText().toString();
            eventEndingDate = eventEndingDateEditText.getText().toString();
            eventExcerpt = eventExcerptEditText.getText().toString();

            //Creating the json object to be passed to the volley request
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TITLE, eventTitle);
            jsonObject.put(CATEGORY, eventCategory);
            jsonObject.put(ABOUT, eventAbout);
            jsonObject.put(EXCERPT, eventExcerpt);
            jsonObject.put(DATE, eventDate);
            jsonObject.put(eDATE, eventEndingDate);
            jsonObject.put(LOCATION, eventLocation);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, eventURL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Add_EventScreenActivity.this, "Event created!", Toast.LENGTH_SHORT).show();
                    finish();
                    Toast.makeText(Add_EventScreenActivity.this, "Refresh the list!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Add_EventScreenActivity.this, "Event creation failed!", Toast.LENGTH_SHORT).show();
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
            requestQueue.add(jsonObjectRequest);

        }
        //Some fields are not filled or empty
        else{
            Toast.makeText(Add_EventScreenActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
        }
    }
}