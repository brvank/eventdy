package com.example.eventdy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.eventdy.MainActivities.ParentActivity;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    //Some useful constants
    private final String signupUrl = "https://eventdy.herokuapp.com/signup",
            USERNAME = "username",EMAIL = "email",PASSWORD = "password",cPASSWORD = "confirmPassword";

    //Variables to store the values of edit texts
    private String username, email, password, confirmPassword;

    //Initializing the views
    private Button signInButton, signUpButton;
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    ProgressBar progressBar;

    //Volley request queue
    RequestQueue requestQueue;

    //SharedPreferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle("SignUP");

        //Instantiating the request queue
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //Initializing the shared preferences
        sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Referencing the views
        //buttons
        signInButton = findViewById(R.id.btn_sign_in_signup);
        signUpButton = findViewById(R.id.btn_signup_signup);

        //edit texts
        usernameEditText = findViewById(R.id.et_username_signup);
        emailEditText = findViewById(R.id.et_email_signup);
        passwordEditText = findViewById(R.id.et_password_signup);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password_signup);
        progressBar = findViewById(R.id.pb_signup);

        //Adding on click listeners to the buttons
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //getting the values of edit texts into variables
                username = usernameEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirmPassword = confirmPasswordEditText.getText().toString();

                //changing the warning color of the confirm password edit text
                confirmPasswordEditText.setBackgroundColor(getResources().getColor(R.color.transparent));

                //comparing variables to be not null
                if(username.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")){
                    Toast.makeText(SignupActivity.this, "All fields required!", Toast.LENGTH_SHORT).show();
                }
                //comparing passwords to be equal
                else if(!password.equals(confirmPassword)){
                    confirmPasswordEditText.setText("");
                    //Providing a warning color for the confirm password edit text
                    confirmPasswordEditText.setBackgroundColor(getResources().getColor(R.color.light_red));
                    Toast.makeText(SignupActivity.this, "Password didn't match!", Toast.LENGTH_SHORT).show();
                }
                //initializing signup process
                else{
                    signup(username, email, password, confirmPassword);
                }
            }
        });
    }

    private void signup(String username,String email, String password, String confirmPassword) {
        //Creating the json object for json object request
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(USERNAME, username);
            jsonObject.put(EMAIL, email);
            jsonObject.put(PASSWORD, password);
            jsonObject.put(cPASSWORD, confirmPassword);

            //Creating json request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signupUrl, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    String email, password, id, username, bio;
                    try {
                        email = response.getString("email");
                        password = response.getString("password");
                        id = response.getString("_id");
                        username = response.getString("username");
                        bio = response.getString("bio");
                        //for getting object from the profile use---> response.get("eventsCreated")

                        //saving email , password and id to the shared preferences
                        editor.putString("email", email);
                        editor.putString("password",password);
                        editor.putString("_id",id);
                        editor.putString("username",username);
                        editor.putString("bio",bio);
                        editor.apply();
                    }catch (JSONException exception){
                        //exception while creating data for the shared preference from response json body
                        Toast.makeText(SignupActivity.this, "Please Retry!", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(SignupActivity.this, "SignUp successful", Toast.LENGTH_SHORT).show();

                    //Going to main activity of the application i.e., 'parent activity'
                    Intent intent = new Intent(getApplicationContext(), ParentActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(SignupActivity.this, "Please Retry\n"+error.getMessage(), Toast.LENGTH_SHORT).show();
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
        } catch (JSONException e) {
            Toast.makeText(this, "Please retry!", Toast.LENGTH_SHORT).show();
        }
    }
}