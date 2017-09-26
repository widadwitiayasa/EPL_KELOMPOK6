package com.diklat.pln.app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.diklat.pln.app.Inbox.ListMessage.ActivityPerijinan;
import com.diklat.pln.app.Inbox.ListMessage.LeavePermissionRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IjinTengahHari extends Fragment {

    Spinner spinner;
    EditText tanggalDari;
    EditText tanggalSampai;
    EditText jam;
    Calendar myCalendar;
    Map<String,CutiObject> jatahCuti;

    Session session;
    LeavePermissionRequest leavePermissionRequest;
    String idIjin;
    EditText notes;
    DateConvertUtil dateConvertUtil;
    ChangeIpUtils changeIpUtils;
    private boolean isActive;

    ProgressDialog progressDialog;
    TextView jamKedataganTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_ijin_tengah_hari,container,false);
        tanggalDari = (EditText)rootView.findViewById(R.id.ijin_tengahhari_dari);
        tanggalSampai = (EditText)rootView.findViewById(R.id.ijin_tengahhari_sampai);
        jam = (EditText)rootView.findViewById(R.id.ijin_tengahhari_jam);
        notes = (EditText)rootView.findViewById(R.id.leave_permission_tengah_hari_note);
        spinner = (Spinner)rootView.findViewById(R.id.ijin_tengahhari_subjekspnr);
        jamKedataganTv = (TextView)rootView.findViewById(R.id.jam_kedatangan_tv);
        myCalendar = Calendar.getInstance();
        Button submit = (Button) rootView.findViewById(R.id.ijin_tengahhari_submitbtn);

        submit.setOnClickListener(op);
        tanggalDari.setOnClickListener(op);
        tanggalSampai.setOnClickListener(op);
        jam.setOnClickListener(op);

        session = new Session(getContext());
        leavePermissionRequest = new LeavePermissionRequest(getContext(),session);
        dateConvertUtil = new DateConvertUtil();

        getIdIjin();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Sedang memuat, harap menunggu . . .");
        ActivityPerijinan mainActivity = (ActivityPerijinan) getActivity();
        changeIpUtils = mainActivity.cIputils;
        isActive = mainActivity.isActive;
        spinner.setOnItemSelectedListener(listener);
        return rootView;
    }
    DatePickerDialog.OnDateSetListener tanggalDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            updateLabel(tanggalDari);
        }
    };
    DatePickerDialog.OnDateSetListener tanggalTo = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR,year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            updateLabel(tanggalSampai);
        }
    };
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            myCalendar.set(Calendar.MINUTE,minute);
            updateTimeLabel();
        }
    };
    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ijin_tengahhari_dari:
                    new DatePickerDialog(getContext(),tanggalDate,myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();break;
                case R.id.ijin_tengahhari_sampai:
                    new DatePickerDialog(getContext(),tanggalTo,myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();break;
                case R.id.ijin_tengahhari_jam:
                {
                    new TimePickerDialog(getContext(),timeSetListener,myCalendar.get(Calendar.HOUR_OF_DAY),
                            myCalendar.get(Calendar.MINUTE),true).show();break;
                }
                case R.id.ijin_tengahhari_submitbtn:{
                    showDialog();
                }
            }
        }
    };
    private void getIdIjin(){
        leavePermissionRequest.getJatahCuti(new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                ParseJson pj = new ParseJson(result);
                String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                List<String> subjekCuti = new ArrayList<>();
                jatahCuti = new HashMap<>();
                subjekCuti.add("- - -Pilih jenis ijin- - -");
                jatahCuti.put("- - -Pilih jenis ijin- - -",new CutiObject("0","0","0","0","0","0","0"));
                List<CutiObject> listData;
                if(msg.equals("ADA")){
                    for(CutiObject cuti:pj.parseCuti()){
                        if(cuti.getFlag().equals("1")){
                            subjekCuti.add(cuti.getNamaCuti());
                            idIjin = cuti.getId();
                            jatahCuti.put(cuti.getNamaCuti(),new CutiObject(cuti.getId(),
                                    cuti.getNamaCuti(),cuti.getMinLapor(),cuti.getMaxLaport(),
                                    cuti.getSisa(),cuti.getKeterangan(),cuti.getFlag()));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,subjekCuti);
                        spinner.setAdapter(arrayAdapter);
                    }
                }
                else if(msg.equals("invalid")){
                    final Session session = new Session(getContext());
                    session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                        @Override
                        public void onSuccessResponse(String result) {
                            ParseJson pj = new ParseJson(result);
                            String token = pj.parseToken();
                            session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                            getIdIjin();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error){
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
    private void updateLabel(EditText editText){
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        editText.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateTimeLabel(){
        String timeFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat,Locale.ENGLISH);
        jam.setText(sdf.format(myCalendar.getTime()));
    }

    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position == 1){
                jamKedataganTv.setText("Jam Kedatangan Sebenarnya");
            }
            else if(position == 2) {
                jamKedataganTv.setText("Jam Kepulangan Sebenarnya");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void showDialog(){
        final View v = LayoutInflater.from(getContext()).inflate(R.layout.leave_permission_form_dialog,null);
        final TextView dialogText = (TextView)v.findViewById(R.id.dialog_leavepermission_text);
        Log.d("tanggal",dateConvertUtil.reverseConvert(tanggalDari.getText().toString()));
        Log.d("tanggaldari",dateConvertUtil.reverseConvert(tanggalSampai.getText().toString()));
        progressDialog.show();
        leavePermissionRequest.ajukanCuti(idIjin,dateConvertUtil.reverseConvert(tanggalDari.getText().toString()),dateConvertUtil.reverseConvert(tanggalDari.getText().toString()),
                new Callback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        progressDialog.dismiss();
                        Log.d("ijinDialog",result);
                        ParseJson pj = new ParseJson(result);
                        Log.d("ajukan_cuti",result);
                        final List<String> data = pj.parseAjukanCuti();
                        if(data.get(0).equals("invalid")){
                            final Session session = new Session(getContext());
                            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                                @Override
                                public void onSuccessResponse(String result) {
                                    ParseJson pj = new ParseJson(result);
                                    String token = pj.parseToken();
                                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                                    //showDialog();
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
                                    progressDialog.dismiss();
                                }
                            });
                            AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
                            myDialog.setView(v);
                            String dialogString = data.get(0) + ". Pengajuan ijin tidak dapat dilanjutkan";
                            dialogText.setText(dialogString);
                            myDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            myDialog.show();
                        }
                        else if(data.get(1).equals("0")){
                            AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
                            myDialog.setView(v);
                            String dialogString = data.get(0) + ". Pengajuan ijin tidak dapat dilanjutkan";
                            dialogText.setText(dialogString);
                            myDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            myDialog.show();
                        }
                        else {
                            AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
                            myDialog.setView(v);
                            String dialogString = data.get(0) + ". Apakah ingin melanjutkan pengajuan ijin ?";
                            dialogText.setText(dialogString);
                            myDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    submitIjin(data);
                                    getActivity().finish();
                                }
                            }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            myDialog.show();
                        }
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
                        progressDialog.dismiss();
                    }
                });
    }

    private void submitIjin(List<String> data){
        leavePermissionRequest.submitIjinTengahHari(jatahCuti.get(spinner.getSelectedItem().toString()).getId(), dateConvertUtil.reverseConvert(tanggalDari.getText().toString()),dateConvertUtil.reverseConvert(tanggalDari.getText().toString()), jam.getText().toString(), data.get(1), data.get(2), data.get(3),notes.getText().toString(), new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                ParseJson pj = new ParseJson(result);
                String msg = pj.parseLogin();
                if(msg.equals("SUCCESS")){
                    Toast.makeText(getContext(),"Pengajuan ijin berhasil",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                else if(msg.equals("invalid")){
                    progressDialog.dismiss();
                    final Session session = new Session(getContext());
                    session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                        @Override
                        public void onSuccessResponse(String result) {
                            progressDialog.dismiss();
                            ParseJson pj = new ParseJson(result);
                            String token = pj.parseToken();
                            session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                            Toast.makeText(getContext(),"Masa token telah habis, silakan ulangi pilihan",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
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
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Pengajuan ijin gagal, silahkan ulangi pengiriman",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
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
}
