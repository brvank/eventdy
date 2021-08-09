package com.example.eventdy;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


//Class to return volley access to every class with only one instance
public class VolleySingleton {

    //Variables for volley request access
    private static VolleySingleton instance;
    private RequestQueue requestQueue;

    //Variable for getting the context of the activity calling for volley access
    private static Context context;

    //private constructor for getting the request queue
    private VolleySingleton(Context context) {
        VolleySingleton.context = context;
        requestQueue = getRequestQueue();
    }

    //constructor to get the volley singleton
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    //to add requests to the request queue
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    //to add the request to the volley request queue
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
