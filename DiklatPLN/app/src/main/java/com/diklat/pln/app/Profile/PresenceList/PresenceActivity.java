package com.diklat.pln.app.Profile.PresenceList;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diklat.pln.app.BaseActivity;
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresenceActivity extends BaseActivity {

    RecyclerView rv;
    Spinner spinnerYear;
    Spinner spinnerMonth;

    String staff;
    Bundle bundle;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence);

        rv = (RecyclerView)findViewById(R.id.presence_rv);
        spinnerYear = (Spinner)findViewById(R.id.pressence_spinnerYear);
        spinnerMonth = (Spinner)findViewById(R.id.pressence_spinnerMonth);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.presence_refresher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Absensi");

        rv.setLayoutManager(new LinearLayoutManager(this));
        bundle = getIntent().getExtras();
        staff = bundle.getString("id");

        spinnerYear.setOnItemSelectedListener(selectedListenerYear);
        spinnerMonth.setOnItemSelectedListener(selectedListenerMonth);
        swipeRefreshLayout.setOnRefreshListener(refresher);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getListData();
            }
        });

        setView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            }
        return super.onOptionsItemSelected(item);
    }

        SwipeRefreshLayout.OnRefreshListener refresher = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListData();
            }
        };

    private void setView(){
        ArrayList<String> listYear = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] month = dfs.getMonths();
        for(int i=thisYear;i<=2035;i++){
            listYear.add(String.valueOf(i));
        }
        ArrayAdapter<String>adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,listYear);
        spinnerYear.setAdapter(adapter);
        spinnerYear.getLayoutParams().height = 150;
        spinnerMonth.getLayoutParams().height = 150;
        int indexThisYear = adapter.getPosition(String.valueOf(thisYear));
        spinnerYear.setSelection(indexThisYear);
        spinnerMonth.setSelection(thisMonth);
    }

    AdapterView.OnItemSelectedListener selectedListenerYear = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            getListData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener selectedListenerMonth = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            getListData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getListData(){
        final Session session = new Session(getBaseContext());
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/Absen/lihat_absen", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("dataabsen",response);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_SHORT).show();
                if(Konstanta.isConnected&&isActive){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        Toast.makeText(getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("token",session.getPreferences("token"));
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("staff",staff);
                param.put("bulan",String.valueOf(spinnerMonth.getSelectedItemPosition()+1));
                param.put("tahun",spinnerYear.getSelectedItem().toString());
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void fetchData(String response){
        final ParseJson pj = new ParseJson(response);
        String msg = pj.parseLogin() !=null ? pj.parseLogin() : "";
        if(msg.equals("ADA")){
            List<PresenceObject> presenceObjectList = pj.parsePresence();
            PresenceListAdapter presenceListAdapter = new PresenceListAdapter(presenceObjectList,getBaseContext());
            rv.setAdapter(presenceListAdapter);
        }
        else if(msg.equals("KOSONG")){
            List<PresenceObject> presenceObjectList = pj.parsePresence();
            PresenceListAdapter presenceListAdapter = new PresenceListAdapter(presenceObjectList,getBaseContext());
            rv.setAdapter(presenceListAdapter);
        }
        else {
            final Session session = new Session(getBaseContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                    getListData();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if(Konstanta.isConnected&&isActive){
                        if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                            Toast.makeText(getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

}

