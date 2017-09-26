package com.diklat.pln.app;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Fandy Aditya on 7/19/2017.
 */

public class MyVolleyPolicy extends StringRequest {
    public MyVolleyPolicy(int method, String url, Response.Listener<String> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
