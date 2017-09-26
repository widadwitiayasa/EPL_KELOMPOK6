package com.diklat.pln.app.Inbox.ListMessage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailMessage extends BaseActivity {

    ImageView profPic;
    TextView name;
    TextView date;
    TextView clock;
    TextView dateFrom;
    TextView dateTo;
    TextView terhitung;
    TextView status;
    TextView notes;
    TextView subjek;
    TextView tglHidetv;
    TextView tghHide;
    TextView waktuHide;
    TextView waktuHidetv;
    TextView disetujui;
    TextView disetujuitv;
    TextView jamTelatTv;
    LinearLayout jamTelatlay;
    TextView jamTelat;
    Button accept;
    Button reject;
    LinearLayout accrjtLay;
    LinearLayout waktuLay;
    LinearLayout waktuHideLay;
    TextView diterimaTgl;
    TextView diajukanTgl;
    Bundle bundle;

    String msgid;
    String id;
    String nama;
    String tanggal;
    String waktu;
    String dari;
    String sampai;
    String jenis;
    String statusIjin;
    String subjekString;
    String fromPhoto;
    String toPhoto;
    String disetujuiString;
    String flagTime;
    String diajukanTglString;
    String diterimaTglString;
    TextView waktuPengajuan;

    String durasi;


    String notesString;

    LeavePermissionRequest leavePermissionRequest;
    ProgressDialog progressDialog;

    LinearLayout detailContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);

        accrjtLay = (LinearLayout)findViewById(R.id.detail_msg_accrjtlay);

        profPic = (ImageView)findViewById(R.id.detail_message_img);
        name = (TextView)findViewById(R.id.detail_message_name);
        date = (TextView)findViewById(R.id.detail_message_date);
        clock = (TextView)findViewById(R.id.detail_message_clock);
        dateFrom = (TextView)findViewById(R.id.detail_message_datefrom);
        dateTo = (TextView)findViewById(R.id.detail_message_dateto);
        terhitung = (TextView)findViewById(R.id.detail_message_terhitung);
        notes = (TextView)findViewById(R.id.detail_message_notes);
        accept = (Button)findViewById(R.id.detail_message_acceptbtn);
        reject = (Button)findViewById(R.id.detail_message_rejectbtn);
        status = (TextView)findViewById(R.id.detail_message_status);
        subjek = (TextView)findViewById(R.id.detail_message_subjek);
        tglHidetv = (TextView)findViewById(R.id.detail_message_tglhidetv);
        tghHide = (TextView)findViewById(R.id.detail_message_tglhide);
        waktuHidetv = (TextView)findViewById(R.id.detail_message_waktuhidetv);
        waktuHide = (TextView)findViewById(R.id.detail_message_waktuhide);
        disetujui = (TextView)findViewById(R.id.detail_message_siapaijin);
        disetujuitv = (TextView)findViewById(R.id.detail_message_siapaijintv);
        jamTelatTv = (TextView)findViewById(R.id.detail_message_jamtelattv);
        jamTelatlay = (LinearLayout)findViewById(R.id.detail_message_jamtelatlay);
        jamTelat = (TextView)findViewById(R.id.detail_message_jamtelat);
        diajukanTgl = (TextView)findViewById(R.id.detail_message_diajukantgl);
        diterimaTgl = (TextView)findViewById(R.id.detail_message_dikonfirmasitgl);
        waktuHideLay = (LinearLayout)findViewById(R.id.detail_message_waktuhidelay);
        waktuLay = (LinearLayout)findViewById(R.id.detail_message_clocklay);
        waktuPengajuan = (TextView)findViewById(R.id.detail_message_waktupengajuan);
        detailContent = (LinearLayout)findViewById(R.id.detail_content_message);

        accept.setOnClickListener(op);
        reject.setOnClickListener(op);
        bundle = getIntent().getExtras();
        durasi = bundle.getString("durasi");
        String parameter = bundle.getString("parameter");
        msgid = bundle.getString("msgid");
        id = bundle.getString("id");
        Log.d("id",bundle.getString("id"));
        nama = bundle.getString("name");
        tanggal = bundle.getString("date");
        waktu = bundle.getString("time");
        dari = bundle.getString("dari");
        sampai = bundle.getString("sampai");
        jenis = bundle.getString("jenis");
        subjekString = bundle.getString("subjek");
        fromPhoto = bundle.getString("fromphoto");
        toPhoto = bundle.getString("tophoto");
        flagTime = bundle.getString("timeFlag");
        Log.d("subjek",jenis);
        statusIjin = bundle.getString("status");
        disetujuiString = bundle.getString("disetujui");
        notesString = bundle.getString("notes");
        diterimaTglString = bundle.getString("diterimaTgl");
        diajukanTglString = bundle.getString("diajukanTgl");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Ijin");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sedang memuat, harap menunggu . . .");
        progressDialog.show();

        setView(parameter);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setView(String parameter){
        if(!flagTime.equals("null")){
            jamTelat.setVisibility(View.VISIBLE);
            jamTelatlay.setVisibility(View.VISIBLE);
            jamTelatTv.setVisibility(View.VISIBLE);
            final String[] timeString = flagTime.split(":");
            jamTelat.setText(timeString[0]+ ":" +timeString[1]);
        }
        if(parameter.equals("inbox")){
            if(subjekString.equals("0")){
                accrjtLay.setVisibility(View.GONE);
                disetujui.setVisibility(View.VISIBLE);
                disetujuitv.setVisibility(View.VISIBLE);
                disetujui.setText(disetujuiString);
                name.setText(bundle.getString("from"));
                setStatusLihat();
                if(!fromPhoto.equals("null")){
                    Glide.with(this).load(Konstanta.URL+"/backend/photo/"+fromPhoto).into(profPic);
                }
                else Glide.with(this).load(Konstanta.URL+"/backend/photo/default_l.png").into(profPic);

            }
            else if (subjekString.equals("3")) {
                detailContent.setVisibility(View.GONE);
                accrjtLay.setVisibility(View.GONE);
                name.setText(bundle.getString("from"));
                setStatusLihat();
                if(!fromPhoto.equals("null")){
                    Glide.with(this).load(Konstanta.URL+"/backend/photo/"+fromPhoto).into(profPic);
                }
                else Glide.with(this).load(Konstanta.URL+"/backend/photo/default_l.png").into(profPic);
            }
            else {
                accrjtLay.setVisibility(View.VISIBLE);
                name.setText(nama);
                progressDialog.dismiss();
                if(!fromPhoto.equals("null"))
                    Glide.with(this).load(Konstanta.URL+"/backend/photo/"+fromPhoto).into(profPic);
                else Glide.with(this).load(Konstanta.URL+"/backend/photo/default_l.png").into(profPic);
            }
        }
        else if(parameter.equals("outbox")){
            progressDialog.dismiss();
            name.setVisibility(View.GONE);
            accrjtLay.setVisibility(View.GONE);
            profPic.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
            waktuLay.setVisibility(View.GONE);

            disetujui.setVisibility(View.VISIBLE);
            disetujuitv.setVisibility(View.VISIBLE);
//            waktuHidetv.setVisibility(View.VISIBLE);
            waktuHideLay.setVisibility(View.VISIBLE);
            tglHidetv.setVisibility(View.VISIBLE);
            tghHide.setVisibility(View.VISIBLE);
            waktuPengajuan.setVisibility(View.GONE);

            tghHide.setText(tanggal);
            waktuHide.setText(waktu);
            disetujui.setText(disetujuiString);
        }

        String statusIjinString;
        if(statusIjin.equals("1")){
            statusIjinString = "Disetujui";
        }
        else if(statusIjin.equals("2")) {
            statusIjinString = "Ditolak";
        }
        else statusIjinString = "Belum dikonfirmasi";
        if(notesString.equals("null")){
            notes.setText("-");
        }else notes.setText(notesString);


        date.setText(tanggal);
        clock.setText(waktu);
        dateFrom.setText(dari);
        terhitung.setText(durasi+" Hari "+bundle.getString("jenisHari") );
        dateTo.setText(sampai);
        subjek.setText(jenis);
        status.setText(statusIjinString);
        diterimaTgl.setText(diterimaTglString);
        diajukanTgl.setText(diajukanTglString);

//        Glide.with(this).load(fotonya);
    }
    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.detail_message_acceptbtn : showDialog("accept");break;
                case R.id.detail_message_rejectbtn : showDialog("reject");break;
            }
        }
    };

    private void setStatusLihat(){
        final Session session = new Session(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/Cuti/inbox_lihat", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               Log.d("response",response);
               progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_SHORT).show();
                if(Konstanta.isConnected&&isActive){
                    if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                        Toast.makeText(getBaseContext(),R.string.error_local,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getBaseContext(),R.string.error_public,Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.dismiss();
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
                param.put("inbox",msgid);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void responseCuti(final String action){
        progressDialog.show();
        Session session = new Session(getBaseContext());
        leavePermissionRequest = new LeavePermissionRequest(getBaseContext(),session);
        leavePermissionRequest.approvalCuti(id, action, new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.d("Approval cuti",result);
                fetchData(result,action);
                progressDialog.dismiss();
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
                progressDialog.dismiss();
            }
        });
    }
    private void showDialog(final String action){
        final View viewDialog = LayoutInflater.from(this).inflate(R.layout.detail_message_dialog,null);

        TextView dialogString = (TextView)viewDialog.findViewById(R.id.dialog_detail_message);
        if(action.equals("accept")){
            String string = "Apakah yakin ingin menyetujui permohonan ijin dari " + nama +" ?";
            dialogString.setText(string);
        }
        else{
            String string = "Apakah yakin ingin menolak permohonan ijin dari " + nama +" ?";
            dialogString.setText(string);
        }
        final AlertDialog.Builder myDialog  = new AlertDialog.Builder(this);
        myDialog.setView(viewDialog);
        myDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (action){
                    case "accept" : responseCuti("1");break;
                    case "reject" : responseCuti("2");break;
                }
            }
        })
                .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        myDialog.show();
    }

    private long differenceDate(String start, String end){
        long days = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            Date startDate = simpleDateFormat.parse(start);
            Date endDate = simpleDateFormat.parse(end);

            long different = endDate.getTime() - startDate.getTime();
            days = different/(1000*60*60*24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days+1;
    }
    private void fetchData(String result,String action){
        ParseJson pj = new ParseJson(result);
        String msg = pj.parseLogin();
        if(msg.equals("SUCCESS")){
            if (action.equals("1"))
            {
                Toast.makeText(getBaseContext(),"Persetujuan ijin berhasil",Toast.LENGTH_SHORT).show();
                setStatusLihat();
                finish();
            }
            else {
                Toast.makeText(getBaseContext(),"Penolakan ijin berhasil",Toast.LENGTH_SHORT).show();
                setStatusLihat();
                finish();
            }
        }
        else{
            final Session session = new Session(getBaseContext());
            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                @Override
                public void onSuccessResponse(String result) {
                    ParseJson pj = new ParseJson(result);
                    String token = pj.parseToken();
                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                    Toast.makeText(getBaseContext(),"Masa token telah habis, silakan ulangi pilihan",Toast.LENGTH_SHORT).show();
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
