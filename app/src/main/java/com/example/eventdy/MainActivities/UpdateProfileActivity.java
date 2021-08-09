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

public class UpdateProfileActivity extends AppCompatActivity {

    //Create event url and base url -- PATCH
    private final String baseURL = "https://eventdy.herokuapp.com", updateURL = baseURL + "/profile";

    //Creating required variables
    private final String USERNAME ="username", EMAIL ="email", PASS="password", CPASS="confirmPassword",BIO="bio";

    //Volley request queue
    RequestQueue requestQueue;

    //Shared preferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Initializing the views
    EditText etEmail, etUsername, etPass, etCpass, etBio;
    CheckBox cdEmail, cdUsername, cdPass, cdCpass, cdBio;
    Button btnUpdate;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //Initializing the shared preference
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Instantiating the request queue
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        getSupportActionBar().setTitle("Update Profile");

        //Referencing the views
        //edit texts
        etEmail = findViewById(R.id.et_email_update_profile);
        etUsername = findViewById(R.id.et_username_update_profile);
        etPass = findViewById(R.id.et_password_update_profile);
        etCpass = findViewById(R.id.et_Cpassword_update_profile);
        etBio = findViewById(R.id.et_bio_update_profile);
        //check boxes
        cdEmail = findViewById(R.id.cb_email_update_profile);
        cdUsername = findViewById(R.id.cb_username_update_profile);
        cdPass = findViewById(R.id.cb_password_update_profile);
        cdCpass = findViewById(R.id.cb_Cpassword_update_profile);
        cdBio = findViewById(R.id.cb_bio_update_profile);
        //button
        btnUpdate = findViewById(R.id.btn_update_profile);
        //progress bar
        progressBar = findViewById(R.id.pb_update_profile);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if((etEmail.getText().toString().equals("") && etUsername.getText().toString().equals("") &&
                            etPass.getText().toString().equals("") && etCpass.getText().toString().equals("") &&
                            etBio.getText().toString().equals(""))||(!cdUsername.isChecked() && !cdEmail.isChecked() && !cdPass.isChecked() && !cdCpass.isChecked() && !cdBio.isChecked()) ){
                        Toast.makeText(UpdateProfileActivity.this, "Nothing to update!",Toast.LENGTH_SHORT).show();
                    }else{
                        updateProfile();
                    }
                } catch (JSONException e) {
                    Toast.makeText(UpdateProfileActivity.this, "Unable to update profile!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfile() throws JSONException {
        JSONObject updateObject = new JSONObject();
        if(cdEmail.isChecked() && !etEmail.getText().toString().equals("")){
            updateObject.put(EMAIL,etEmail.getText().toString());
        }
        if(cdUsername.isChecked() && !etUsername.getText().toString().equals("")){
            updateObject.put(USERNAME,etUsername.getText().toString());
        }
        if(cdPass.isChecked() && !etPass.getText().toString().equals("")){
            updateObject.put(PASS,etPass.getText().toString());
        }
        if(cdCpass.isChecked() && !etCpass.getText().toString().equals("")){
            updateObject.put(CPASS,etCpass.getText().toString());
        }
        if(cdBio.isChecked() && !etBio.getText().toString().equals("")){
            updateObject.put(BIO,etBio.getText().toString());
        }

        JsonObjectRequest updaterequest = new JsonObjectRequest(Request.Method.PATCH, updateURL, updateObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                if (cdEmail.isChecked()) {
                    editor.putString(EMAIL,etEmail.getText().toString());
                }
                if (cdUsername.isChecked()) {
                    editor.putString(USERNAME, etUsername.getText().toString());
                }
                if (cdPass.isChecked()) {
                    editor.putString(PASS, etPass.getText().toString());
                }
                if (cdBio.isChecked()) {
                    editor.putString(BIO, etBio.getText().toString());
                }
                Toast.makeText(UpdateProfileActivity.this, "Profile updated,\nrefresh to get changes!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpdateProfileActivity.this, "Unable to update profile!", Toast.LENGTH_SHORT).show();
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