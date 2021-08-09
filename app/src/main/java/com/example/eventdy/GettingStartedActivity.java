package com.example.eventdy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.eventdy.MainActivities.ParentActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class GettingStartedActivity extends AppCompatActivity {
    //required URLs
    private final String profileURL = "https://eventdy.herokuapp.com/profile/";

    //variable to let the user wait for the network connection to get again
    boolean wait = false;

    //Volley request queue
    private RequestQueue requestQueue;

    //Initializing the button object
    private Button getStartedButton;

    private SharedPreferences sharedPreferences;

    //progress bar for loading
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);

        Objects.requireNonNull(getSupportActionBar()).hide();

        //getting request queue from volley singleton class
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //getting the progress bar
        progressBar = findViewById(R.id.pb_start);

        //Getting the details of previous user if exists
        ArrayList<String> user = new ArrayList<>();
        //user = customApplication.getDetails();

        //Initializing the shared preferences
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);


        if (!connected()) {
            Toast.makeText(GettingStartedActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            if (!sharedPreferences.contains("cookie")) {
                enableButton();
            } else {
                String cookie = sharedPreferences.getString("cookie", "");
                if(!cookie.equals("")) {
                    checkAuthenticated(cookie);
                }
            }
        }

    }

    private void enableButton() {
        //creating the UI for the activity
        Toast.makeText(getApplicationContext(), "Welcome User", Toast.LENGTH_LONG).show();

        //Instantiating the button object and setting the on click listener to it
        getStartedButton = findViewById(R.id.btn_get_started);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        getStartedButton.setVisibility(View.VISIBLE);
    }

    private void checkAuthenticated(String cookie) {

        JsonObjectRequest getProfile = new JsonObjectRequest(Request.Method.GET,profileURL + sharedPreferences.getString("_id",""), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(GettingStartedActivity.this, ParentActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                enableButton();
            }
        }){
            //Creating the header for authorized network access by passing the cookies to the server
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> cookies = new HashMap<>();
                cookies.put("cookie",cookie);
                return cookies;
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        requestQueue.add(getProfile);
    }

    private boolean connected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            //not connected to the internet
            connected = false;
        }
        return connected;
    }
}