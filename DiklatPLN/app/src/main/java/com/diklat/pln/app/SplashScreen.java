package com.diklat.pln.app;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashScreen extends AppCompatActivity {

    ProgressBar progressBar;
    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Fabric.with(this, new Crashlytics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        refresh = (Button)findViewById(R.id.refreshbtn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new logingIn().execute();
                progressBar.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            }
        });
        new logingIn().execute();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getAction(){
        final Session session = new Session(getBaseContext());
        if(session.isAvailable()){
           session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
               @Override
               public void onSuccessResponse(String result) {
                   ParseJson pj = new ParseJson(result);
                   String msg = pj.parseLogin() !=null ? pj.parseLogin() : "";
                   if(msg.equals("invalid")){
                       Toast.makeText(getBaseContext(),"username atau password salah",Toast.LENGTH_SHORT).show();
                       openPage(LoginActivity.class);
                   }
                   else {
//                       String token = pj.parseToken();
                       openPage(TabbedActivity.class);
//                       session.removePreferences();
//                       session.editPreferences(token,session.,password.getText().toString());
                   }
               }

               @Override
               public void onErrorResponse(VolleyError error) {
                   checkConnection();
                   if(Konstanta.isConnected){
                       ChangeIpUtils changeIpUtils = new ChangeIpUtils(getBaseContext());
                       changeIpUtils.loginChange();
                       getAction();
                   }
                   else{
                       Toast.makeText(getBaseContext(),"Tidak ada jaringan internet",Toast.LENGTH_SHORT).show();
                       refresh.setVisibility(View.VISIBLE);
                   }
               }
           });
        }
        else{
            openPage(LoginActivity.class);
        }
    }
    private void checkConnection(){
        ConnectivityManager CM = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo NI = CM.getActiveNetworkInfo();
        if(NI!=null && NI.isConnected()){
            new checkInternet().execute();
        }
        else{
            Konstanta.isConnected=false;
            progressBar.setVisibility(View.GONE);
        }
    }
    private void openPage(Class page){
        Intent myIntent = new Intent(getBaseContext(),page);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }


    class checkInternet extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isInternet=false;
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if(urlc.getResponseCode()==200){
                    Log.d("tag", "Internet available");
                    Konstanta.isConnected=true;
                    isInternet=true;
                }
                else{
                    isInternet=false;
                }
            } catch (IOException e) {
                Log.e("tag", "Error checking internet connection", e);
            }
            return isInternet;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                progressBar.setVisibility(View.VISIBLE);
            }
            else
                progressBar.setVisibility(View.GONE);
        }
    }
    class logingIn extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            getAction();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
