package com.diklat.pln.app.Inbox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.diklat.pln.app.BaseActivity;
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.Inbox.ListMessage.LeavePermissionRequest;
import com.diklat.pln.app.Inbox.ListMessage.ListMessageObject;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.OnLoadMore;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fandy Aditya on 6/3/2017.
 */

public class OutBoxFragment extends BaseActivity {


    RecyclerView rv;
    SwipeRefreshLayout refresh;
    OutboxAdapter outboxAdapter;
    List<ListMessageObject> listMessage;

    int nLoad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outbox_fragment);

        rv = (RecyclerView)findViewById(R.id.outbox_rv);
        refresh = (SwipeRefreshLayout)findViewById(R.id.outbox_refresh);
        listMessage = new ArrayList<>();

        nLoad=30;

        rv.setLayoutManager(new LinearLayoutManager(this));
        refresh.setOnRefreshListener(refresher);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
                getMessage("30");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Kotak Keluar");

        outboxAdapter = new OutboxAdapter(rv,listMessage,getBaseContext());
        rv.setAdapter(outboxAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());

        outboxAdapter.setOnLoadMoreListener(new OnLoadMore() {
            @Override
            public void onScroolMore() {
                if(listMessage.size()<=1000&&listMessage.size()>=30){
                    listMessage.add(null);
                    outboxAdapter.notifyItemInserted(listMessage.size() - 1);
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nLoad+=10;
                            getMessage(String.valueOf(nLoad));
                            outboxAdapter.notifyDataSetChanged();
                            outboxAdapter.setLoaded();
                        }
                    }, 1500);
                }
            }
        });

        getMessage("30");
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
            getMessage("30");
        }
    };

    private void getMessage(String jumlah){
        Session session = new Session(getBaseContext());
        LeavePermissionRequest leavePermissionRequest = new LeavePermissionRequest(getBaseContext(),session);
        leavePermissionRequest.getOutBox(jumlah, new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.d("outbox",result);
                refresh.setRefreshing(false);
                fetchData(result);
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
                refresh.setRefreshing(false);
            }
        });
    }
    private void fetchData(String result){
        ParseJson pj = new ParseJson(result);
        String msg = pj.parseLogin();
        if (msg.equals("ADA")){
            listMessage.clear();
            listMessage.addAll(pj.parseOutbox());
            outboxAdapter.notifyDataSetChanged();
        }
        else if(msg.equals("invalid")){
            final Session session = new Session(getBaseContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    ParseJson pj = new ParseJson(result);
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                    getMessage("30");
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
