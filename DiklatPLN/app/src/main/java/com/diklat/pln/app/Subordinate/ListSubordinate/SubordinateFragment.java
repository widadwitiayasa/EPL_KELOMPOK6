package com.diklat.pln.app.Subordinate.ListSubordinate;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.ChangeIpUtils;
import com.diklat.pln.app.CutiObject;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;
import com.diklat.pln.app.TabbedActivity;
import com.diklat.pln.app.UnitObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Fandy Aditya on 4/23/2017.
 */

public class SubordinateFragment extends Fragment {
    RecyclerView rv;
    SwipeRefreshLayout refresh;
    EditText search;
    Spinner spinnerBawahan;
    List<ListSubordinateObjek> listSubordinate;
    ListSubordinateAdapter listSubordinateAdapter;
    ChangeIpUtils changeIpUtils;
    Map<String,String> unitId;
    private boolean isActive;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.fragment_subordinate_page,container,false);
        rv = (RecyclerView) rootView.findViewById(R.id.list_subordinate_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        unitId = new HashMap<>();
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        spinnerBawahan = (Spinner)rootView.findViewById(R.id.spinner_bawahan);
        TabbedActivity mainActivity = (TabbedActivity)getActivity();
        changeIpUtils = mainActivity.cIputils;
        isActive = mainActivity.activeActivity;
        listSubordinate = new ArrayList<>();
        refresh = (SwipeRefreshLayout)rootView.findViewById(R.id.list_subordinate_refresh);
        search = (EditText)rootView.findViewById(R.id.search_subordinate);
        refresh.setOnRefreshListener(refresher);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
//                getSubordinateList();
                if(spinnerBawahan.getChildCount() > 1) {
                    getBawahanNew();
                }
                else getSubordinateList();
            }
        });
        Button magangBtn = (Button)rootView.findViewById(R.id.list_subordinate_magangbtn);
        magangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage();
            }
        });
        spinnerBawahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refresh.setRefreshing(true);
                getBawahanNew();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                refresh.setRefreshing(true);
                getSubordinateList();
            }
        });
        getListUnit();
//        getSubordinateList();
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    List<ListSubordinateObjek> filteredList = new ArrayList<>();
                    if(listSubordinate.size() > 0) {
                        for(ListSubordinateObjek filter : listSubordinate){
                            String name = filter.getName().toLowerCase();
                            if(name.contains(s.toString().toLowerCase())){
                                filteredList.add(filter);
                            }
                        }
                        listSubordinateAdapter.setFilter(filteredList);
                    }
                }
            });
        return rootView;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    SwipeRefreshLayout.OnRefreshListener refresher = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
//            getSubordinateList();
            getBawahanNew();
            if(spinnerBawahan.getChildCount() > 1) {
                getBawahanNew();
            }
            else getSubordinateList();
        }
    };

    private void getBawahanNew() {
        final Session session = new Session(getActivity().getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/User/searchName", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                refresh.setRefreshing(false);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(getActivity() != null)
                {
                    if(Konstanta.isConnected&&isActive){
                        if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                            Toast.makeText(getActivity().getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity().getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                        }
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
                param.put("nip","");
                param.put("unit",unitId.get(spinnerBawahan.getSelectedItem().toString()));
                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getBaseContext());
        requestQueue.add(stringRequest);
    }

    private void getListUnit() {
        final Session session = new Session(getActivity().getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Konstanta.URL + "/backend/API/Unit/getUnitAll", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ParseJson pj = new ParseJson(response);
                String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                List<String> unitName = new ArrayList<>();
                List<UnitObject> unitObjects = new ArrayList<>();
                if(msg.equals("ADA")) {
                    unitObjects.addAll(pj.parseUnit());
                    for(UnitObject unit : unitObjects){
                        unitName.add(unit.getName());
                        unitId.put(unit.getName(),unit.getId());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item, unitName);
                    spinnerBawahan.setAdapter(arrayAdapter);
                    if(unitObjects.size()>1) {
                        spinnerBawahan.setVisibility(View.VISIBLE);
                    }
                    else {
                        spinnerBawahan.setVisibility(View.GONE);
                    }
                }
                else if(msg.equals("invalid")) {
                    final Session session = new Session(getContext());
                    session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                        @Override
                        public void onSuccessResponse(String result) {
                            ParseJson pj = new ParseJson(result);
                            String token = pj.parseToken();
                            session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                            getListUnit();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
                else {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item, unitName);
                    spinnerBawahan.setAdapter(arrayAdapter);
                    Toast.makeText(getContext(),"Anda tidak memiliki unit bawahan",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getBaseContext() , "Gagal Memuat List Unit" , Toast.LENGTH_LONG).show();
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


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getBaseContext());
        requestQueue.add(stringRequest);
    }

    private void getSubordinateList(){
        final Session session = new Session(getActivity().getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Konstanta.URL + "/backend/API/User/select", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                refresh.setRefreshing(false);
                Log.d("list_subordinate",response);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refresh.setRefreshing(false);
                if(getActivity() != null) {
                    if (Konstanta.isConnected && isActive) {
                        if (Konstanta.URL.equals(Konstanta.IPLOCAL)) {
                            Toast.makeText(getActivity().getBaseContext(), R.string.error_local, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity().getBaseContext(), R.string.error_public, Toast.LENGTH_SHORT).show();
                        }
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getBaseContext());
        requestQueue.add(stringRequest);
    }
    private void fetchData(String response){
        if (getActivity() != null) {
            Session session = new Session(getActivity().getBaseContext());
            ParseJson pj = new ParseJson(response);
            listSubordinate = pj.listSubParse();
            Iterator<ListSubordinateObjek> it = listSubordinate.iterator();
            while (it.hasNext()) {
                ListSubordinateObjek bawahan = it.next();
                if (bawahan.getNip().equals(session.getPreferences("username"))) {
                    it.remove();
                }
            }
            listSubordinateAdapter = new ListSubordinateAdapter(listSubordinate,getActivity().getBaseContext());
            rv.setAdapter(listSubordinateAdapter);
        }

    }
    private void openPage(){
        Intent myIntent = new Intent(getActivity().getBaseContext(),ListMagang.class);
        startActivity(myIntent);
    }
}
