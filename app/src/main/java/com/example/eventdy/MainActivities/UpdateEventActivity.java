package com.example.eventdy.MainActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.Map;

public class UpdateEventActivity extends AppCompatActivity {

    //Create event url and base url--PATCH
    private final String baseURL = "https://eventdy.herokuapp.com", updateURL = baseURL + "/event/";
    private String id;

    //Creating required variables
    private final String TITLE ="title", ABOUT ="about", DATE="date", eDATE="endingDate",EXCERPT="excerpt", CATEGORY="category", LOCATION="location", ID="_id";

    //Volley request queue
    RequestQueue requestQueue;

    //Shared preferences
    SharedPreferences sharedPreferences;

    //Initializing the views
    EditText etTitle, etCategory, etAbout, etDetails, etStartDate, etEndDate, etLocation;
    CheckBox cdTitle, cdCategory, cdAbout, cdDetails, cdStartDate, cdEndDate, cdLocation;
    Button btnUpdate;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        //getting the event id from the intent
        Intent intent = getIntent();
        id = intent.getExtras().getString(ID);

        getSupportActionBar().setTitle("Edit Event");

        //Initializing the shared preference
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);

        //Instantiating the request queue
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //Referencing the views
        //edit texts
        etTitle = findViewById(R.id.et_title_update_event);
        etAbout = findViewById(R.id.et_about_update_event);
        etCategory = findViewById(R.id.et_category_update_event);
        etDetails = findViewById(R.id.et_details_update_event);
        etStartDate = findViewById(R.id.et_start_update_event);
        etEndDate = findViewById(R.id.et_end_update_event);
        etLocation = findViewById(R.id.et_location_update_event);
        //check boxes
        cdTitle = findViewById(R.id.cb_title_update_event);
        cdAbout = findViewById(R.id.cb_about_update_event);
        cdCategory = findViewById(R.id.cb_category_update_event);
        cdDetails = findViewById(R.id.cb_details_update_event);
        cdStartDate = findViewById(R.id.cb_startDate_update_event);
        cdEndDate = findViewById(R.id.cb_endDate_update_event);
        cdLocation = findViewById(R.id.cb_location_update_event);
        //button
        btnUpdate = findViewById(R.id.btn_update_event);
        //progress bar
        progressBar = findViewById(R.id.pb_update_event);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if((etTitle.getText().toString().equals("") && etAbout.getText().toString().equals("") && etCategory.getText().toString().equals("") &&
                            etDetails.getText().toString().equals("") && etStartDate.getText().toString().equals("") && etEndDate.getText().toString().equals("") &&
                            etLocation.getText().toString().equals("")) || (!cdTitle.isChecked() && !cdAbout.isChecked()&& !cdCategory.isChecked()
                            && !cdDetails.isChecked()&& !cdStartDate.isChecked()&& !cdEndDate.isChecked()&& !cdLocation.isChecked())){
                        Toast.makeText(UpdateEventActivity.this, "Nothing to update!", Toast.LENGTH_SHORT).show();
                    }else{
                        updateEvent();
                    }
                } catch (JSONException e) {
                    Toast.makeText(UpdateEventActivity.this, "Unable to send update request!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateEvent() throws JSONException {
        JSONObject updateObject = new JSONObject();
        if(cdTitle.isChecked()){
            updateObject.put(TITLE,etTitle.getText().toString());
        }
        if(cdAbout.isChecked()){
            updateObject.put(ABOUT,etAbout.getText().toString());
        }
        if(cdCategory.isChecked()){
            updateObject.put(CATEGORY,etCategory.getText().toString());
        }
        if(cdDetails.isChecked()){
            updateObject.put(EXCERPT,etDetails.getText().toString());
        }
        if(cdStartDate.isChecked()){
            updateObject.put(DATE,etStartDate.getText().toString());
        }
        if(cdEndDate.isChecked()){
            updateObject.put(eDATE,etEndDate.getText().toString());
        }
        if(cdLocation.isChecked()){
            updateObject.put(LOCATION,etLocation.getText().toString());
        }
        JsonObjectRequest updaterequest = new JsonObjectRequest(Request.Method.PATCH, updateURL + id, updateObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpdateEventActivity.this, "Event updated,\nrefresh to get changes!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpdateEventActivity.this, "Unable to update event!", Toast.LENGTH_SHORT).show();
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
        requestQueue.add(updaterequest);
    }
}