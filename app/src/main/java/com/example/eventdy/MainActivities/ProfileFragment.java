package com.example.eventdy.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.eventdy.LoginActivity;
import com.example.eventdy.R;
import com.example.eventdy.Resources.Events;
import com.example.eventdy.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private final String baseURL = "https://eventdy.herokuapp.com" , logoutURL = "https://eventdy.herokuapp.com/logout";

    //Main view
    View view;

    //array list for the recycler view
    ArrayList<Events> events = new ArrayList<>();

    //progress bar
    ProgressBar progressBar;

    //Volley request queue
    RequestQueue requestQueue;

    //Shared Preferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Initializing the views
    TextView userNameTextView, bioTextView;
    Button myProfileButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to set the menu options in action bar
        setHasOptionsMenu(true);

        //Initializing request queue
        requestQueue = VolleySingleton.getInstance(requireContext()).getRequestQueue();

        //Initializing shared preferences
        sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Referencing the views
        userNameTextView = view.findViewById(R.id.tv_user_name_profile);
        bioTextView = view.findViewById(R.id.tv_bio_profile);
        myProfileButton = view.findViewById(R.id.btn_my_profile);
        progressBar = view.findViewById(R.id.pb_fragment_profile);

        //setting the username and bio profile
        userNameTextView.setText(sharedPreferences.getString("username", "Unknown"));
        bioTextView.setText(sharedPreferences.getString("bio","Anonymous"));

        //setting the on click listener for my profile button
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Going to the user profile activity to get the user profile
                String _id = sharedPreferences.getString("_id","00");
                if(!_id.equals("00")){
                    Bundle bundle = new Bundle();
                    bundle.putString("_id",_id);
                    Intent intent = new Intent(requireContext(),UserProfileActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(requireContext(), "Do logout and login!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Edit:
                Intent intent = new Intent(requireContext(),UpdateProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.Logout:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,logoutURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //Clearing the shared preferences and applying the change
                editor.clear();
                editor.apply();
                //going to login screen after logging out
                Toast.makeText(requireContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> headers = new HashMap<>();
                headers.put("cookie",sharedPreferences.getString("cookie",""));
                return headers;
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        requestQueue.add(stringRequest);
    }
}