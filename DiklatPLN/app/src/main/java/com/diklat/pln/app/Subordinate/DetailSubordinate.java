package com.diklat.pln.app.Subordinate;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.diklat.pln.app.BaseActivity;
import com.diklat.pln.app.DateConvertUtil;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.Profile.PresenceList.PresenceActivity;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailSubordinate extends BaseActivity {

    ImageView profileImg;
    TextView profileBigName;
    TextView profileName;
    TextView profileNip;
    TextView profileSapid;
    TextView profileBirthDate;
    TextView profileBloodType;
    TextView profileCapegDate;
    TextView profilePosition;
    TextView profilePresence;
    TextView profileUnit;
    TextView profileOrganization;
    Button profilePresenceBtn;
    LinearLayout settingLay;
    View garis;
    DateConvertUtil dateConvertUtil;

    SwipeRefreshLayout refresher;

    Bundle bundle;
    String id;
    String parameter;

    LinearLayout sapid,capeg,organisasi;
    TextView nip,unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_page);

        profileImg = (ImageView)findViewById(R.id.profile_img);
        profileBigName = (TextView)findViewById(R.id.profile_nameBig_tv);
        profileName = (TextView)findViewById(R.id.profile_name_tv);
        profileNip = (TextView)findViewById(R.id.profile_nip_tv);
        profileSapid = (TextView)findViewById(R.id.profile_sapid_tv);
        profileBirthDate = (TextView)findViewById(R.id.profile_birthday_tv);
        profileBloodType = (TextView)findViewById(R.id.profile_bloodtype_tv);
        profileCapegDate = (TextView)findViewById(R.id.profile_capeg_tv);
        profilePosition = (TextView)findViewById(R.id.profile_position_tv);
        profilePresenceBtn = (Button)findViewById(R.id.profile_presence_list_btn);
        profileUnit = (TextView)findViewById(R.id.profile_unit_tv);
        profileOrganization = (TextView)findViewById(R.id.profile_organisasi_tv);
        settingLay = (LinearLayout)findViewById(R.id.profile_setting_lay);
        garis = (View)findViewById(R.id.profile_garis);
        refresher =(SwipeRefreshLayout)findViewById(R.id.profile_refresh);
        refresher.setOnRefreshListener(refresh);

        sapid = (LinearLayout)findViewById(R.id.profile_sapidlay);
        capeg = (LinearLayout)findViewById(R.id.profile_capeglay);
        organisasi = (LinearLayout)findViewById(R.id.profile_organisasilay);
        nip = (TextView)findViewById(R.id.profile_nip_container);
        unit = (TextView)findViewById(R.id.profile_unit_container);

        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        parameter = bundle.getString("parameter");
        dateConvertUtil = new DateConvertUtil();

        profilePresenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(id);
            }
        });
        setView(parameter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profil Bawahan");

        refresher.post(new Runnable() {
            @Override
            public void run() {
                refresher.setRefreshing(true);
                if(parameter.equals("sementara"))
                    getDataMagang();
                else
                    getDataSubordinate();
            }
        });

    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    SwipeRefreshLayout.OnRefreshListener refresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(parameter.equals("sementara"))
                getDataMagang();
            else
                getDataSubordinate();
        }
    };

    private void setView(String parameter){
        settingLay.setVisibility(View.GONE);
        garis.setVisibility(View.GONE);
        if(parameter.equals("sementara")){
            sapid.setVisibility(View.GONE);
            capeg.setVisibility(View.GONE);
            organisasi.setVisibility(View.GONE);
            nip.setText("Id Registrasi");
            unit.setText("Head");
            getDataMagang();
        }
        else{
            capeg.setVisibility(View.GONE);
            sapid.setVisibility(View.VISIBLE);
//            capeg.setVisibility(View.VISIBLE);
            organisasi.setVisibility(View.VISIBLE);
            getDataSubordinate();
        }

    }
    private void getDataMagang(){
        final Session session = new Session(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/Register_magang/profil", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Detail Magang",response);
                refresher.setRefreshing(false);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Konstanta.isConnected){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        Toast.makeText(getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                    }
                }
                refresher.setRefreshing(false);
//                Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_SHORT).show();
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
                param.put("id",id);
                param.put("head","");
                param.put("unit","");
                param.put("kata","");
                param.put("type","");
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getDataSubordinate(){
        final Session session = new Session(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/User/profil", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data pegawai tetap",response);
                refresher.setRefreshing(false);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Konstanta.isConnected){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        Toast.makeText(getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                    }
                }
                refresher.setRefreshing(false);
//                Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_SHORT).show();
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
                param.put("id",id);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void openIntent(String id){
        Intent myIntent = new Intent(getBaseContext(), PresenceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }
    private void fetchData(String response){
        ParseJson pj = new ParseJson(response);
        if(parameter.equals("sementara")){
            List<String> data = pj.parseProfileMagang();
            profileName.setText(!data.get(1).equals("null") ? data.get(1) : "-");
            profileBigName.setText(!data.get(1).equals("null") ? data.get(1) : "-");
            profileBirthDate.setText(!data.get(2).equals("null") ? data.get(2) : "-");
            profileBloodType.setText(!data.get(4).equals("null") ? data.get(4) : "-");
            profileUnit.setText(!data.get(7).equals("null") ? data.get(7) : "-");
            profilePosition.setText(!data.get(7).equals("null") ? data.get(7) : "-");
            profileNip.setText(!data.get(9).equals("null") ? data.get(9) : "-");
//            Glide.with(this).load(Konstanta.URL+"/backend/photo/"+data.get(10)).into(profileImg);
            profilePosition.setText(data.get(11).toUpperCase());
            if(!data.get(10).equals("null"))
                Glide.with(getBaseContext()).load(Konstanta.URL+"/backend/photo/"+data.get(10)).into(profileImg);
            else {
                if(data.get(11).equals("Laki laki")){
                    Glide.with(getBaseContext()).load(Konstanta.URL+"/backend/photo/default_l.png").into(profileImg);
                }
                else{
                    Glide.with(getBaseContext()).load(Konstanta.URL+"/backend/photo/default_p.png").into(profileImg);
                }
            }
        }
        else{
            List<String> data = pj.parseProfile();
            if(!data.get(10).equals("null"))
                Glide.with(getBaseContext()).load(Konstanta.URL+"/backend/photo/"+data.get(10)).into(profileImg);
            else {
                if(data.get(11).equals("Laki laki")){
                    Glide.with(getBaseContext()).load(Konstanta.URL+"/backend/photo/default_l.png").into(profileImg);
                }
                else{
                    Glide.with(getBaseContext()).load(Konstanta.URL+"/backend/photo/default_p.png").into(profileImg);
                }
            }
            profileName.setText(!data.get(1).equals("null") ? data.get(1) : "-");
            profileBigName.setText(!data.get(1).equals("null") ? data.get(1) : "-");
            profileNip.setText(!data.get(2).equals("null") ? data.get(2) : "-");
            profileSapid.setText(!data.get(3).equals("null") ? data.get(3) : "-");
            profileBirthDate.setText(dateConvertUtil.convert(data.get(4)));
            profileBloodType.setText(!data.get(5).equals("null") ? data.get(5) : "-");
            profileCapegDate.setText(dateConvertUtil.convert(data.get(6)));
            profilePosition.setText(!data.get(7).equals("null") ? data.get(7) : "-");
            profileUnit.setText(!data.get(8).equals("null") ? data.get(8) : "-");
            profileOrganization.setText(!data.get(9).equals("null") ? data.get(9) : "-");
        }

    }
}
