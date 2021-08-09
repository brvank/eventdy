package com.example.eventdy.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.eventdy.R;

public class ParentActivity extends AppCompatActivity {

    //initializing image views variables for navigation bar
    ImageView home,profile,add_event,search,notification;

    //initializing navigation view
    LinearLayout navigationBar;

    //initializing fragment container
    FrameLayout fragmentContainer;

    //initializing the fragment manager
    FragmentManager fragmentManager = getSupportFragmentManager();

    //initializing the fragments(home,profile,add_event and search fragments)
    HomeFragment homeFragment;
    SearchFragment searchFragment;
    Add_EventFragment add_eventFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        //referencing the image views of bottom navigation bar
        home = findViewById(R.id.iv_home_parent);
        profile = findViewById(R.id.iv_profile_parent);
        add_event = findViewById(R.id.iv_add_parent);
        search = findViewById(R.id.iv_search_parent);

        //referencing the navigation bar
        navigationBar = findViewById(R.id.ll_navigation_bar_parent);

        //referencing the fragment container
        fragmentContainer = findViewById(R.id.fl_fragment_container_parent);

        //instantiating all fragments
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        add_eventFragment = new Add_EventFragment();
        profileFragment = new ProfileFragment();

        //setting the home fragment as the default one
        if(savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(fragmentContainer.getId(),homeFragment,null)
                    .add(fragmentContainer.getId(),searchFragment,null)
                    .add(fragmentContainer.getId(),add_eventFragment,null)
                    .add(fragmentContainer.getId(),profileFragment,null)
                    .commit();
            changeFragmentView(R.id.iv_home_parent);
        }

        //attaching event listeners to different image views inside navigation bar
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentView(R.id.iv_home_parent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentView(R.id.iv_search_parent);
            }
        });

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentView(R.id.iv_add_parent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentView(R.id.iv_profile_parent);
            }
        });
    }

    // function to change the views by changing the fragments inside frame layout
    private void changeFragmentView(int id){
        switch(id){
            //TODO: to make active each images in navigation bar and then changing the active state of other images to inactive state.
            case R.id.iv_home_parent:

                getSupportActionBar().setTitle("Home");

                home.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_home_24));
                search.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_search_24));
                add_event.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_add_24));
                profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_account_circle_24));

                fragmentManager.beginTransaction()
                        .show(homeFragment)
                        .hide(searchFragment)
                        .hide(add_eventFragment)
                        .hide(profileFragment)
                        .commit();
                break;

            case R.id.iv_search_parent:

                getSupportActionBar().setTitle("Search");

                home.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_home_24));
                search.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_search_24));
                add_event.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_add_24));
                profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_account_circle_24));

                fragmentManager.beginTransaction()
                        .hide(homeFragment)
                        .show(searchFragment)
                        .hide(add_eventFragment)
                        .hide(profileFragment)
                        .commit();
                break;

            case R.id.iv_add_parent:

                getSupportActionBar().setTitle("Create");

                home.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_home_24));
                search.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_search_24));
                add_event.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24));
                profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_account_circle_24));

                fragmentManager.beginTransaction()
                        .hide(homeFragment)
                        .hide(searchFragment)
                        .show(add_eventFragment)
                        .hide(profileFragment)
                        .commit();
                break;

            case R.id.iv_profile_parent:

                getSupportActionBar().setTitle("Profile");

                home.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_home_24));
                search.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_search_24));
                add_event.setImageDrawable(getResources().getDrawable(R.drawable.ic_sharp_add_24));
                profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_account_circle_24));

                fragmentManager.beginTransaction()
                        .hide(homeFragment)
                        .hide(searchFragment)
                        .hide(add_eventFragment)
                        .show(profileFragment)
                        .commit();
                break;
        }
    }

}