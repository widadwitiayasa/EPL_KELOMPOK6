package com.diklat.pln.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class Session{

    private Context context;
    private SharedPreferences sharedPreferences;

    public Session(Context context) {
        if (context!=null){
            this.context = context;
            sharedPreferences = this.context.getSharedPreferences("myToken",Context.MODE_PRIVATE);
        }
        else{
            logout();
        }

    }

    public boolean isAvailable(){
        boolean returnVal;
        String value = sharedPreferences.getString("token","empty");
        if(value.equals("empty"))
            returnVal = false;
        else returnVal = true;
        return returnVal;
    }
    public void editPreferences(String token,String username,String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token);
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }
    public void editPreferencesIdOnly(String id,String user){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id",id);
        editor.putString("user",user);
        editor.apply();
    }
    public void editPreferencesPengumuman(int i){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pengumuman",String.valueOf(i));
        editor.apply();
    }
    public void editPreferencesBadgeicon(int i){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("badgeicon",String.valueOf(i));
        editor.apply();
    }
    public void removePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    public String getPreferences(String param){
        String returnVal = null;
        switch (param){
            case "token" : returnVal = sharedPreferences.getString("token",null);break;
            case "username" : returnVal = sharedPreferences.getString("username",null);break;
            case "password" : returnVal = sharedPreferences.getString("password",null);break;
            case "id" : returnVal = sharedPreferences.getString("id",null);break;
            case "user" : returnVal = sharedPreferences.getString("user",null);break;
            case "pengumuman" :returnVal = sharedPreferences.getString("pengumuman",null);break;
            case "iconbadge" : returnVal = sharedPreferences.getString("badgeicon","0");break;
        }
        return returnVal;
    }

    public void loginProcess(final String username, final String password,final Callback callback){
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        StringRequest newReq = new StringRequest(Request.Method.POST, Konstanta.URL+"/backend/API/user/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               callback.onErrorResponse(error);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("nip",username);
                param.put("password",password);
                param.put("time", String.valueOf(System.currentTimeMillis()/1000));
                return param;
            }
        };

        RequestQueue requestQueue =Volley.newRequestQueue(context);
        requestQueue.add(newReq);
    }
    public void logout(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/user/logout", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ParseJson pj = new ParseJson(response);
                String msg = pj.parseLogin();
//                if(msg.equals("ok")){
                    openIntent(SplashScreen.class);
                    removePreferences();
                    ShortcutBadger.applyCount(context,0);
//                }
                if(context != null){
                    context.stopService(new Intent(context,NotificationServices.class));

//                    PackageManager pm  = context.getPackageManager();
//                    ComponentName componentName = new ComponentName(context, AlarmReceiverLifeLog.class);
//                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                            PackageManager.DONT_KILL_APP);
//                    AlarmReceiverLifeLog alarm = new AlarmReceiverLifeLog();
//                    alarm.cancelAlarm(context);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(context != null){
                    Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(context,"Log out gagal, coba ganti ip terlebih dahulu",Toast.LENGTH_SHORT).show();
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("token",getPreferences("token"));
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context != null ? context : null);
        requestQueue.add(stringRequest);
    }
    private boolean getAnswer(String response){
        boolean returnVal;
        ParseJson pj = new ParseJson(response);
        String timeToken = pj.parseExpiredTokenTime();
        if(timeToken.equals("0")){
            returnVal = true;
        }
        else returnVal = false;
        return returnVal;
    }
    private void openIntent(Class page){
        Intent openPage = new Intent(context,page);
        openPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(openPage);
    }
}
