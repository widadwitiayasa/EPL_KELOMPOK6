package com.diklat.pln.app;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    public ChangeIpUtils changeIpUtils;
    public boolean isActive;
    Menu menu;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        changeIpUtils = new ChangeIpUtils(this);
        broadcastReceiver = new ConnectionChangeReceiver();
//        checkInternetConnection();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu,menu);
        this.menu = menu;
        changeIpUtils.setNetwork(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ip_local:
                Log.d("URL",Konstanta.URL);
                Konstanta.URL = Konstanta.IPLOCAL;
                Log.d("URL",Konstanta.URL);
                item.setChecked(true);
                break;
            case R.id.ip_public:
                Konstanta.URL = Konstanta.IPPUBLIC;
                item.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        changeIpUtils.setNetwork(menu);
    }

//    public void checkInternetConnection(){
//    ConnectivityManager CM = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
//    NetworkInfo NI = CM.getActiveNetworkInfo();
//    if(NI!=null && NI.isConnected()){
//        ((LinearLayout)findViewById(R.id.no_connection_bar)).setVisibility(View.VISIBLE);
//    }
//    else{
//        ((LinearLayout)findViewById(R.id.no_connection_bar)).setVisibility(View.GONE);
//    }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
