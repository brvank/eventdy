package com.example.eventdy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventdy.MainActivities.ParentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    //Variables and constants for url and parameters
    private final String baseURL = "https://eventdy.herokuapp.com", loginURL = "https://eventdy.herokuapp.com/login";
    private String email,password;

    //Declaring views
    Button loginButton, signupButton;
    EditText emailEditText,passwordEditText;
    ProgressBar progressBar;

    //request queue
    RequestQueue requestQueue;

    //Shared Preferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //JSON object
    JSONObject jsonObject;
    String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");

        //Getting the views by id
        loginButton = findViewById(R.id.btn_login_login);
        signupButton = findViewById(R.id.btn_signup_login);
        emailEditText = findViewById(R.id.et_email_login);
        passwordEditText = findViewById(R.id.et_password_login);
        progressBar = findViewById(R.id.pb_login);

        //Initializing the requestQueue
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //Initializing the shared preferences
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Setting onClickListeners to different views
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!emailEditText.getText().toString().equals("") && !passwordEditText.getText().toString().equals("")){
                    login(emailEditText.getText().toString(),passwordEditText.getText().toString());
                }else{
                    Toast.makeText(LoginActivity.this, "Email or password not filled!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSignUp();
            }
        });
    }

    //moving to signup
    private void moveToSignUp(){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    //login request
    private void login(String email,String password){

        JSONObject jsonObject = new JSONObject();
        try {
            //creating json object for the request
            jsonObject.put("email",email);
            jsonObject.put("password",password);

            //json object request for login request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginURL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    String email, password, id, username;
                    try {
                        email = response.getString("email");
                        password = response.getString("password");
                        id = response.getString("_id");
                        username = response.getString("username");
                        //TODO: to add the image section into the shared preferences file
                        //for getting object from the profile use---> response.get("eventsCreated")

                        //saving email , password and id to the shared preferences
                        editor.putString("email", email);
                        editor.putString("password",password);
                        editor.putString("_id",id);
                        editor.putString("username",username);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Logging in successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
                        startActivity(intent);
                        finish();
                    }catch (JSONException exception){
                        //exception while creating data for the shared preference from response json body
                        Toast.makeText(LoginActivity.this, "Please Retry!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(LoginActivity.this, "Please Retry!", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    String cookie;
                    //saving session cookie into the shared preferences
                    cookie = response.headers.get("Set-Cookie");
                    editor.putString("cookie",cookie);
                    editor.apply();
                    return super.parseNetworkResponse(response);
                }
            };
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            requestQueue.add(jsonObjectRequest);
        }catch (JSONException exception){

            //exception while creating json object for login request
            Toast.makeText(this, "Please Retry", Toast.LENGTH_SHORT).show();
        }
    }

}