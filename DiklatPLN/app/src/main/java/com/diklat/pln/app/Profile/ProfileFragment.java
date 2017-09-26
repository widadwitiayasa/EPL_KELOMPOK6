package com.diklat.pln.app.Profile;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.ChangeIpUtils;
import com.diklat.pln.app.DateConvertUtil;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.Profile.PresenceList.PresenceActivity;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;
import com.diklat.pln.app.TabbedActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fandy Aditya on 4/23/2017.
 */

public class ProfileFragment extends Fragment {

    CircleImageView profileImg;
    TextView profileBigName;
    TextView profileName;
    TextView profileNip;
    TextView profileSapid;
    TextView profileBirthDate;
    TextView profileBloodType;
    TextView profileCapegDate;
    TextView profilePosition;
    TextView profileUnit;
    TextView profileOrganization;
    Button profilePresenceBtn;
    LinearLayout aturBtn;
    LinearLayout logoutBtn;
    SwipeRefreshLayout refreshLayout;
    ContentValues viewContent;
    DateConvertUtil dateConvertUtill;
    ChangeIpUtils changeIpUtils;

    private boolean isActive;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_page,container,false);
        profileImg = (CircleImageView) rootView.findViewById(R.id.profile_img);
        profileBigName = (TextView)rootView.findViewById(R.id.profile_nameBig_tv);
        profileName = (TextView)rootView.findViewById(R.id.profile_name_tv);
        profileNip = (TextView)rootView.findViewById(R.id.profile_nip_tv);
        profileSapid = (TextView)rootView.findViewById(R.id.profile_sapid_tv);
        profileBirthDate = (TextView)rootView.findViewById(R.id.profile_birthday_tv);
        profileBloodType = (TextView)rootView.findViewById(R.id.profile_bloodtype_tv);
        profileCapegDate = (TextView)rootView.findViewById(R.id.profile_capeg_tv);
        profilePosition = (TextView)rootView.findViewById(R.id.profile_position_tv);
        profileUnit = (TextView)rootView.findViewById(R.id.profile_unit_tv);
        profileOrganization = (TextView)rootView.findViewById(R.id.profile_organisasi_tv);
        profilePresenceBtn = (Button)rootView.findViewById(R.id.profile_presence_list_btn);
//        aturBtn = (LinearLayout)rootView.findViewById(R.id.profile_atur_lay);
        logoutBtn = (LinearLayout)rootView.findViewById(R.id.profile_logout_lay);

        profilePresenceBtn.setOnClickListener(op);
//        aturBtn.setOnClickListener(op);
        logoutBtn.setOnClickListener(op);
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.profile_refresh);
        refreshLayout.setOnRefreshListener(refresh);

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getData();
            }
        });
        TabbedActivity mainActivity = (TabbedActivity)getActivity();
        changeIpUtils = mainActivity.cIputils;
        isActive = mainActivity.activeActivity;
        return rootView;
    }

    SwipeRefreshLayout.OnRefreshListener refresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getData();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //isiin if mau ambil local atau server
//        if(localAvailable()){
//            callingLocalDB("notfirst",null);
//        }
//        else
//            getData();
//        fetchData("test");
    }
    private boolean localAvailable(){
        File localDb = getContext().getDatabasePath("local.db");
        return localDb.exists();
    }
    private void getData(){
        final Session session = new Session(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL+"/backend/API/User/profil", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("profil",response);
                refreshLayout.setRefreshing(false);
                fetchData(response);
            }
        }, new Response.ErrorListener() {
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
                refreshLayout.setRefreshing(false);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("token",session.getPreferences("token"));
                return headers;
            }
        };
        RequestQueue newReq = Volley.newRequestQueue(getContext());
        newReq.add(stringRequest);
    }
    private void fetchData(String response){
        final Session session = new Session(getContext());
        final ParseJson pj = new ParseJson(response);
        List<String> data = new ArrayList<>();
        data.addAll(pj.parseProfile());
        String msg = pj.parseLogin();
        if(msg.equals("ok") && data.size()>0){
            dateConvertUtill = new DateConvertUtil();
            session.editPreferencesIdOnly(data.get(0),data.get(11));
            profileName.setText(!data.get(1).equals("") ? data.get(1) : "-");
            profileBigName.setText(!data.get(1).equals("") ? data.get(1) : "-");
            profileNip.setText(!data.get(2).equals("") ? data.get(2) : "-");
            profileSapid.setText(!data.get(3).equals("") ? data.get(3) : "-");
            profileBirthDate.setText(!data.get(4).equals("")?dateConvertUtill.convert(data.get(4)) : "-");
            profileBloodType.setText(!data.get(5).equals("") ? data.get(5) : "-" );
            profileCapegDate.setText(!data.get(6).equals("")?dateConvertUtill.convert(data.get(6)) : "-");
            profilePosition.setText(!data.get(7).equals("") ? data.get(7) : "-");
            profileUnit.setText(!data.get(8).equals("") ? data.get(8) : "-");
            profileOrganization.setText(!data.get(9).equals("") ? data.get(9) : "-");
            if(!data.get(10).equals("null"))
                Glide.with(this).load(Konstanta.URL+"/backend/photo/"+data.get(10)).into(profileImg);
            else {
                if(data.get(11).equals("Laki laki")){
                    Glide.with(getContext()).load(Konstanta.URL+"/backend/photo/default_l.png").into(profileImg);
                }
                else{
                    Glide.with(getContext()).load(Konstanta.URL+"/backend/photo/default_p.png").into(profileImg);
                }
            }
//            Glide.with(getContext()).load(Konstanta.URL+"/backend/photo/"+data.get(10)).into(profileImg);
        }
        else{
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                    getData();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if(Konstanta.isConnected) {
                        if(Konstanta.URL.equals(Konstanta.IPLOCAL)) {
                            Toast.makeText(getContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

//        ContentValues contentValues = new ContentValues();
//        contentValues.put("id",data.get(0));
//        contentValues.put("nama",data.get(1));
//        contentValues.put("nip",data.get(2));
//        contentValues.put("sapid",data.get(3));
//        contentValues.put("born_date",data.get(4));
//        contentValues.put("blood_type",data.get(5));
//        contentValues.put("capeg_date",data.get(6));
//        contentValues.put("position",data.get(7));
//        contentValues.put("unit",data.get(8));
//        contentValues.put("organization",data.get(9));
//        contentValues.put("profPic","http://www.clipartbest.com/cliparts/7ia/L9j/7iaL9jAaT.jpg");
//
//        callingLocalDB("first",contentValues);

    }
    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.profile_logout_lay :
                {
                    Session session = new Session(getActivity().getBaseContext());
                    session.logout();

                    break;
                }
                case R.id.profile_presence_list_btn : openPage(PresenceActivity.class);break;
            }
        }
    };
    private void openPage(Class page){
        Intent myIntent = new Intent(getContext(),page);
        Bundle bundle = new Bundle();
        bundle.putString("id","");
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }
//    private void callingLocalDB(String param, ContentValues contentValues){
//        LocalDB localDB = new LocalDB(getContext(),"local.db",null,6);
//        ContentValues data;
//        if (param.equals("first")){
//            localDB.insertProfile(contentValues);
//            data =  localDB.showProfile(contentValues.getAsString("id"));
//        }
//        else {
//          data =  localDB.showProfile(contentValues.getAsString("id"));
//        }
//        viewContent = data;
//        setView();
//    }
    private void setView(){
//        profileName.setText(viewContent.getAsString("nama"));
//        profileBigName.setText(viewContent.getAsString("nama"));
//        profileNip.setText(viewContent.getAsString("nip"));
//        profileSapid.setText(viewContent.getAsString("sapid"));
//        profileBirthDate.setText(viewContent.getAsString("born_date"));
//        profileBloodType.setText(viewContent.getAsString("blood_type"));
//        profileCapegDate.setText(viewContent.getAsString("capeg_date"));
//        profilePosition.setText(viewContent.getAsString("position"));
//        profileUnit.setText(viewContent.getAsString("unit"));
//        profileOrganization.setText(viewContent.getAsString("organization"));
//        Glide.with(getContext()).load(viewContent.getAsString("profpic")).into(profileImg);
    }
}
