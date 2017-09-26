package com.diklat.pln.app;

import com.android.volley.VolleyError;

/**
 * Created by Fandy Aditya on 6/7/2017.
 */

public interface Callback {
    void onSuccessResponse(String result);
    void onErrorResponse(VolleyError error);
}
