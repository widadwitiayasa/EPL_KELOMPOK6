package com.diklat.pln.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Fandy Aditya on 7/17/2017.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("app","Network connectivity change");
        if(intent.getExtras()!=null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                Log.i("app", "Network " + ni.getTypeName() + " connected");
                new checkInternet().execute();
            }

            } else {
                Konstanta.isConnected = false;
                Log.d("tag", "No network available!");
                Toast.makeText(context,"Tidak ada jaringan internet",Toast.LENGTH_SHORT).show();

        }
        if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            Konstanta.isConnected=false;
            Log.d("app","There's no network connectivity");
            Toast.makeText(context,"Tidak ada jaringan internet",Toast.LENGTH_SHORT).show();
        }
    }
    class checkInternet extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                if(urlc.getResponseCode()==200){
                    Log.d("tag", "Internet available");
                   Konstanta.isConnected=true;
                }
            } catch (IOException e) {
                Log.e("tag", "Error checking internet connection", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
