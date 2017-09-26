package com.diklat.pln.app.Subordinate.ListSubordinate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMagang extends BaseActivity {

    RecyclerView rv;
    RadioGroup rg;
    String rgChoice;
    Spinner magangSpinner;
    Map<String,String> magangKeyVal;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_magang);

        rv = (RecyclerView)findViewById(R.id.list_magang_rv);
        magangKeyVal = new HashMap<>();
//        rg = (RadioGroup)findViewById(R.id.list_magang_radio_group);
//
//        RadioButton rgB1 = (RadioButton)findViewById(R.id.ojt);
//        RadioButton rgB2 = (RadioButton)findViewById(R.id.coop);
//
        rv.setLayoutManager(new LinearLayoutManager(this));
//        rg.clearCheck();
//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId){
//                    case R.id.ojt:
//                        getData("ojt");
//                        rgChoice = "ojt";
//                        break;
//                    case R.id.coop:
//                        getData("coop");
//                        rgChoice = "coop";
//                        break;
//                }
//            }
//        });
//        rgB1.setChecked(true);
        magangSpinner = (Spinner)findViewById(R.id.list_magang_spinner);
        magangSpinner.setOnItemSelectedListener(selector);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List Pegawai Lainnya");

        getSpinner();
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
            getData(rgChoice);
        }
    };

    AdapterView.OnItemSelectedListener selector = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            getData(magangKeyVal.get(magangSpinner.getSelectedItem().toString()));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getSpinner(){
        final Session session = new Session(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL+"/backend/API/Non_Tetap/select", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("MagangSpinner",response);
                setView(response);
            }
        }, new Response.ErrorListener() {
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
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("token",session.getPreferences("token"));
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getData(final String jenisKaryawan){
        final Session session = new Session(getBaseContext());
        Log.d("idSession",session.getPreferences("id"));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/Register_magang/profil", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("MagangCuy",response);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_LONG).show();
                if(Konstanta.isConnected&&isActive){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        Toast.makeText(getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                    }
                }
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
                param.put("id","");
                param.put("head",session.getPreferences("id"));
                param.put("unit","");
                param.put("kata","");
                param.put("type",jenisKaryawan);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void setView(String response){
        final ParseJson pj = new ParseJson(response);
        String msg = pj.parseLogin();
        if(msg.equals("ADA")){
            List<MagangObject> listData = pj.parseMagangObj();
            List<String> nonTetapList = new ArrayList<>();
            for(MagangObject magang:listData){
                nonTetapList.add(magang.getNama());
                magangKeyVal.put(magang.getNama(),magang.getId());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item,nonTetapList);
            magangSpinner.setAdapter(arrayAdapter);
        }
        else if(msg.equals("invalid")){
            final Session session = new Session(getBaseContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
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
    private void fetchData(String response){
        ParseJson pj = new ParseJson(response);
        String msg = pj.parseLogin();
        if(msg.equals("ADA")){
            List<ListSubordinateObjek> listData = pj.parseMagang();
            MagangAdapter magangAdapter = new MagangAdapter(listData,getBaseContext());
            rv.setAdapter(magangAdapter);

        }
        else if (msg.equals("invalid")){
            final Session session = new Session(getBaseContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    ParseJson pj = new ParseJson(result);
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                    Toast.makeText(getBaseContext(),"Masa Token telah habis, Silakan ulangi pilihan",Toast.LENGTH_SHORT).show();
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
        else{
            List<ListSubordinateObjek> listData = pj.parseMagang();
            MagangAdapter magangAdapter = new MagangAdapter(listData,getBaseContext());
            rv.setAdapter(magangAdapter);
        }
    }
}
