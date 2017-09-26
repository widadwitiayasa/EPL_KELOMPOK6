package com.diklat.pln.app.Inbox.ListMessage;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.ChangeIpUtils;
import com.diklat.pln.app.Inbox.OutBoxFragment;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.OnLoadMore;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;
import com.diklat.pln.app.TabbedActivity;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Fandy Aditya on 4/23/2017.
 */

public class InboxFragment extends Fragment {
    RecyclerView rv;
    LinearLayout createMessage;
    LinearLayout outBox;
    ImageView outBoxBadge;
    SwipeRefreshLayout refreshLayout;
    LeavePermissionRequest leavePermissionRequest;
    ChangeIpUtils changeIpUtils;
    private boolean isActive;
    Session session;

    CheckBox filterPesan;
    List<ListMessageObject> listData;
    List<ListMessageObject> listData2;
    TabbedActivity mainActivity;
    ListMessageAdapter listMessageAdapter;

    int nLoad;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox_page,container,false);
        listData = new ArrayList<>();
        listData2 = new ArrayList<>();
        nLoad = 30;
        rv = (RecyclerView) rootView.findViewById(R.id.inbox_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        filterPesan = (CheckBox) rootView.findViewById(R.id.filter_pesan);
        createMessage = (LinearLayout) rootView.findViewById(R.id.inbox_make_messagebtn);
        outBox = (LinearLayout) rootView.findViewById(R.id.inbox_kotak_keluarbtn);
        outBoxBadge = (ImageView)rootView.findViewById(R.id.inbox_outbox_badge);
        createMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity(ActivityPerijinan.class);
            }
        });
        outBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity(OutBoxFragment.class);
            }
        });

        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.inbox_refresh);
        refreshLayout.setOnRefreshListener(refresh);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                if(filterPesan.isChecked()) {
                    getUnreadList("30");
                }
                else
                    getListMessage("30");
            }
        });
        mainActivity = (TabbedActivity)getActivity();
        changeIpUtils = mainActivity.cIputils;
        isActive = mainActivity.activeActivity;
        session = new Session(getActivity().getBaseContext());
        leavePermissionRequest = new LeavePermissionRequest(getActivity().getBaseContext(),session);
//        setBadge();
        listMessageAdapter = new ListMessageAdapter(rv,listData2,getActivity().getBaseContext());
        rv.setAdapter(listMessageAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());

        filterPesan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refreshLayout.setRefreshing(true);
                    getUnreadList("30");
                }
                else {
                    refreshLayout.setRefreshing(true);
                    getListMessage("30");
                }
            }
        });

        listMessageAdapter.setOnLoadMoreListener(new OnLoadMore() {
            @Override
            public void onScroolMore() {
                if(listData2.size()<=1000&&listData2.size()>=30){
                    listData2.add(null);
                    listMessageAdapter.notifyItemInserted(listData2.size() - 1);
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            listData2.remove(listData2.size() - 1);
//                            listMessageAdapter.notifyItemRemoved(listData2.size());

                            //Generating more data
//                            for(int i=nLoad;i<nLoad+10;i++){
//                                listData2.add(listData.get(i));
//                            }
                            nLoad+=10;
                            if(filterPesan.isChecked()) {
                                getUnreadList(String.valueOf(nLoad));
                            }
                            else {
                                getListMessage(String.valueOf(nLoad));
                            }
//                            listMessageAdapter.notifyDataSetChanged();
                            listMessageAdapter.setLoaded();
                        }
                    }, 1500);
                }
            }
        });

        getListMessage("30");
        getIconBadge();

        return rootView;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (filterPesan.isChecked()) {
            refreshLayout.setRefreshing(true);
            getUnreadList("30");
        }
        else {
            refreshLayout.setRefreshing(true);
            getListMessage("30");
        }
    }
    private void switchActivity(Class page){
        Intent intent = new Intent(getActivity().getBaseContext(),page);
        startActivity(intent);
    }

    SwipeRefreshLayout.OnRefreshListener refresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(filterPesan.isChecked()) {
                getUnreadList("30");
            }
            else
                getListMessage("30");
        }
    };

    private void getUnreadList(String jumlah) {
        leavePermissionRequest.getInbox(jumlah, "", "2", new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                refreshLayout.setRefreshing(false);
                fetchData(result);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(Konstanta.isConnected){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        Toast.makeText(getActivity().getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity().getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                    }
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void getListMessage(String jumlah){
        leavePermissionRequest.getInbox(jumlah,"","", new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                refreshLayout.setRefreshing(false);
                fetchData(result);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(Konstanta.isConnected){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        if(getActivity() != null) {
                            Toast.makeText(getActivity().getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        if(getActivity() != null) {
                            Toast.makeText(getActivity().getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void getIconBadge(){
        leavePermissionRequest.getInbox("", "", "2", new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                ParseJson pj = new ParseJson(result);
                String msg = pj.parseLogin() !=null ? pj.parseLogin() : "";
                if(msg.equals("ADA")) {
                    List iconPurpose = pj.parseInbox();
                    int badge = iconPurpose.size();
                    int iconBadge = Integer.parseInt(session.getPreferences("iconbadge")) + badge;
                    session.editPreferencesBadgeicon(iconBadge);
                    ShortcutBadger.applyCount(getActivity().getBaseContext(), Integer.parseInt(session.getPreferences("iconbadge")));
                    Log.d("badge", String.valueOf(badge));
                    mainActivity.updateBadge(String.valueOf(badge), "inbox");
                }
                else if(msg.equals("invalid")){
                    final Session session = new Session(getActivity().getBaseContext());
                    session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                        @Override
                        public void onSuccessResponse(String result) {
                            ParseJson pj = new ParseJson(result);
                            String token = pj.parseToken();
                            session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                            getIconBadge();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(Konstanta.isConnected){
                                if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                                    Toast.makeText(getActivity().getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getActivity().getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                else if(msg==null){
                    Toast.makeText(getActivity().getBaseContext(),"Gagal terkoneksi ke jaringan, silakan ganti jaringan atau cek koneksi anda",Toast.LENGTH_LONG).show();
                }
                else {
                    mainActivity.updateBadge("0","inbox");
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void fetchData(String response){
        Log.d("inbox",response);
        ParseJson pj = new ParseJson(response);
        String msg = pj.parseLogin() !=null ? pj.parseLogin() : "";
        if(msg.equals("ADA")){
            listData2.clear();
            listData2.addAll(pj.parseInbox());
//            for(int i=0;i<30;i++){
//                listData2.add(listData.get(i));
//            }
            rv.setAdapter(listMessageAdapter);
            //listMessageAdapter.notifyDataSetChanged();
//            listMessageAdapter = new ListMessageAdapter(rv,listData,getActivity().getBaseContext());
//            rv.setAdapter(listMessageAdapter);
//            int badge=0;
//            for(ListMessageObject listMessage: listData){
//                if(listMessage.getStatusLihat().equals("2")){
//                    badge++;
//                }
//            }
//            int iconBadge = Integer.parseInt(session.getPreferences("iconbadge")) + badge;
//            session.editPreferencesBadgeicon(iconBadge);
//            ShortcutBadger.applyCount(getActivity().getBaseContext(),Integer.parseInt(session.getPreferences("iconbadge")));
//            Log.d("badge",String.valueOf(badge));
//            mainActivity.updateBadge(String.valueOf(badge),"inbox");
        }
        else if(msg.equals("invalid")){
            final Session session = new Session(getActivity().getBaseContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    ParseJson pj = new ParseJson(result);
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                    getListMessage("30");
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if(Konstanta.isConnected){
                        if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                            Toast.makeText(getActivity().getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity().getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        else if(msg==null){
            Toast.makeText(getActivity().getBaseContext(),"Gagal terkoneksi ke jaringan, silakan ganti jaringan atau cek koneksi anda",Toast.LENGTH_LONG).show();
        }
        else {
            List<ListMessageObject> listData = pj.parseInbox();
            ListMessageAdapter listMessageAdapter = new ListMessageAdapter(rv,listData,getActivity().getBaseContext());
            rv.setAdapter(listMessageAdapter);
            mainActivity.updateBadge("0","inbox");
        }
    }
}
