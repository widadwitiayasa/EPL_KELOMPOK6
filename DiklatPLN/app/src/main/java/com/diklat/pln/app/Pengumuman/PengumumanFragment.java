package com.diklat.pln.app.Pengumuman;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.ChangeIpUtils;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.LocalDB;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;
import com.diklat.pln.app.TabbedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fandy Aditya on 5/13/2017.
 */

public class PengumumanFragment extends Fragment {

    RecyclerView rv;
    SwipeRefreshLayout refresher;
    Menu menu;
    ChangeIpUtils changeIpUtils;
    private boolean isActive;
    LocalDB localDB;
    TabbedActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pengumuman_fragment,container,false);
        rv = (RecyclerView)rootView.findViewById(R.id.pengumuman_rv);
        refresher = (SwipeRefreshLayout)rootView.findViewById(R.id.list_pengumuman_refresh);
        setHasOptionsMenu(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        getData();
        mainActivity = (TabbedActivity)getActivity();
        changeIpUtils = mainActivity.cIputils;
        isActive = mainActivity.activeActivity;
        refresher.setOnRefreshListener(refresh);
        refresher.post(new Runnable() {
            @Override
            public void run() {
                refresher.setRefreshing(true);
                getData();
            }
        });
        localDB = new LocalDB(getContext(),"pengumumanlocal.db",null,1);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
    }

    SwipeRefreshLayout.OnRefreshListener refresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getData();
        }
    };

    private void getData(){
        final Session session = new Session(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Konstanta.URL + "/backend/API/Pengumuman/getPengumumanActive", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("pengumuman",response);
                refresher.setRefreshing(false);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refresher.setRefreshing(false);
                if(Konstanta.isConnected){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        Toast.makeText(getContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                    }
                }
                //Toast.makeText(getContext(),error.toString(),Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private void localDBAction(List<PengumumanObject> listData,String action){
        if(action.equals("add")){
            int i=0;
            for(PengumumanObject pengumuman : listData){
                Log.d("idpengumuman",String.valueOf(listData.get(i).getIdPengumuman()));
                Log.d("pengumuman",String.valueOf(localDB.checkId(pengumuman.getIdPengumuman())));
                if(localDB.checkId(pengumuman.getIdPengumuman())){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id",pengumuman.getIdPengumuman());
                    contentValues.put("status_lihat","0");
                    localDB.insert(contentValues);
                }
                i++;
            }
            List<PengumumanObject> listPengumumanLocal = new ArrayList<>();
            listPengumumanLocal.addAll(localDB.getPengumuman());
            for (PengumumanObject pengumumanObject: listPengumumanLocal) {
               Log.d("pengumumanObject", pengumumanObject.getIdPengumuman());
            }
        }
        else if(action.equals("delete")){
            List<PengumumanObject> listPengumumanLocal = new ArrayList<>();
            listPengumumanLocal.addAll(localDB.getPengumuman());
            for (PengumumanObject pengumumanObject: listPengumumanLocal) {
                if (listData.contains(pengumumanObject)) {
                    localDB.delete(pengumumanObject.getIdPengumuman());
                }
            }
        }
    }
    private void fetchData(String response){
        ParseJson parseJson = new ParseJson(response);
        String msg = parseJson.parseLogin() !=null ? parseJson.parseLogin() : "";
        if(msg.equals("ADA")){
            final List<PengumumanObject> listData = parseJson.listPengumumanParse();
            PengumumanAdapter pengumumanAdapter = new PengumumanAdapter(getContext(),listData,localDB);
            rv.setAdapter(pengumumanAdapter);
            Session session = new Session(getContext());
            session.editPreferencesPengumuman(listData.size());
            localDBAction(listData,"add");
            localDBAction(listData,"delete");
            mainActivity.updateBadge(String.valueOf(localDB.showPengumumanStatus()),"pengumuman");
            session.editPreferencesBadgeicon(localDB.showPengumumanStatus());
        }
        else if(msg.equals("invalid")){
            final Session session = new Session(getContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    ParseJson pj = new ParseJson(result);
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                    getData();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if(Konstanta.isConnected){
                        if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                            Toast.makeText(getContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        else{
            final List<PengumumanObject> listData = parseJson.listPengumumanParse();
            PengumumanAdapter pengumumanAdapter = new PengumumanAdapter(getContext(),listData,localDB);
            rv.setAdapter(pengumumanAdapter);
            mainActivity.updateBadge("0","pengumuman");
        }
    }
}
