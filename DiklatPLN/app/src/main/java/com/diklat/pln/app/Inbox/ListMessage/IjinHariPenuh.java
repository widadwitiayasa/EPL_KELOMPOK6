package com.diklat.pln.app.Inbox.ListMessage;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.ChangeIpUtils;
import com.diklat.pln.app.CutiObject;
import com.diklat.pln.app.DateConvertUtil;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fandy Aditya on 6/9/2017.
 */

public class IjinHariPenuh extends Fragment {

    Spinner subjek;
    Button kirim;
    EditText from;
    EditText to;
    EditText notes;
    Calendar myCalendar;
    TextView ijinLeft;
    LeavePermissionRequest leavePermissionRequest;
    Session session;
    Map<String,CutiObject> jatahCuti;

    ProgressDialog progressDialog;
    DateConvertUtil dateConvertUtil;
    ChangeIpUtils changeIpUtils;
    private boolean isActive;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_leave_permission_form,container,false);

        subjek = (Spinner)rootView.findViewById(R.id.leave_permission_subjekspnr);
        kirim = (Button)rootView.findViewById(R.id.leave_permission_kirimbtn);
        from = (EditText)rootView.findViewById(R.id.leave_permission_from);
        to = (EditText)rootView.findViewById(R.id.leave_permission_to);
        notes = (EditText)rootView.findViewById(R.id.leave_permission_note);
        ijinLeft = (TextView)rootView.findViewById(R.id.leave_permission_sisaijin);
        myCalendar = Calendar.getInstance();
        ActivityPerijinan mainActivity = (ActivityPerijinan) getActivity();
        changeIpUtils = mainActivity.cIputils;
        isActive = mainActivity.isActive;
        session = new Session(getContext());
        leavePermissionRequest = new LeavePermissionRequest(getContext(),session);

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from.getText().toString().length()==0||to.getText().toString().length()==0){
                    Toast.makeText(getActivity().getBaseContext(),"Form Dari/Sampai kosong",Toast.LENGTH_SHORT).show();
                }
                else if(subjek.getSelectedItem().toString().equals("- - -Pilih jenis cuti- - -")){
                    Toast.makeText(getActivity().getBaseContext(),"Pilih Subjek Ijin Terlebih Dahulu",Toast.LENGTH_SHORT).show();
                }
                else {
                    showDialog();
                }
            }
        });
        to.setOnClickListener(op);
        from.setOnClickListener(op);
        subjek.setOnItemSelectedListener(itemSelector);
        setView();
        dateConvertUtil = new DateConvertUtil();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Sedang memuat, harap menunggu . . .");
        progressDialog.show();

        return rootView;
    }

    private void setView(){
        leavePermissionRequest.getJatahCuti(new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                progressDialog.dismiss();
                Log.d("result",result);
                ParseJson pj = new ParseJson(result);
                String msg = pj.parseLogin() != null ? pj.parseLogin() : "";
                List<String> subjekCuti = new ArrayList<>();
                jatahCuti = new HashMap<>();
                subjekCuti.add("- - -Pilih jenis cuti- - -");
                jatahCuti.put("- - -Pilih jenis cuti- - -",new CutiObject("0","0","0","0","0","0","0"));
                List<CutiObject> listData;
                if(msg.equals("ADA")){
                    listData = pj.parseCuti();
                    for(CutiObject cuti:listData){
                        if(cuti.getFlag().equals("0")){
                            subjekCuti.add(cuti.getNamaCuti());
                            jatahCuti.put(cuti.getNamaCuti(),new CutiObject(cuti.getId(),
                                    cuti.getNamaCuti(),cuti.getMinLapor(),cuti.getMaxLaport(),
                                    cuti.getSisa(),cuti.getKeterangan(),cuti.getFlag()));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,subjekCuti);
                        subjek.setAdapter(arrayAdapter);
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
                            setView();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
                else {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,subjekCuti);
                    subjek.setAdapter(arrayAdapter);
                    Toast.makeText(getContext(),"Anda tidak memiliki jatah cuti",Toast.LENGTH_SHORT).show();
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
    AdapterView.OnItemSelectedListener itemSelector = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = parent.getSelectedItem().toString();
            Log.d("SelectedItem",selectedItem);
            ijinLeft.setText(Integer.parseInt(jatahCuti.get(selectedItem).getSisa())>=365 ? DecimalFormatSymbols.getInstance().getInfinity() :
                    jatahCuti.get(selectedItem).getSisa());
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel(from);

        }
    };
    DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel(to);
        }
    };

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.leave_permission_from:
                    new DatePickerDialog(getContext(),dateFrom,myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;

                case R.id.leave_permission_to:
                    new DatePickerDialog(getContext(),dateTo,myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;
            }
        }
    };

    private void updateLabel(EditText source){
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        source.setText(sdf.format(myCalendar.getTime()));
    }
    private void showDialog(){
        final View v = LayoutInflater.from(getContext()).inflate(R.layout.leave_permission_form_dialog,null);
        final TextView dialogText = (TextView)v.findViewById(R.id.dialog_leavepermission_text);

        progressDialog.show();
        leavePermissionRequest.ajukanCuti(jatahCuti.get(subjek.getSelectedItem().toString()).getId(),dateConvertUtil.reverseConvert(from.getText().toString()),dateConvertUtil.reverseConvert(to.getText().toString()),
                new Callback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        progressDialog.dismiss();
                        Log.d("ijinDialog",result);
                        ParseJson pj = new ParseJson(result);
                        Log.d("ajukan_cuti",result);
                        final List<String> data = pj.parseAjukanCuti();
                        if(ijinLeft.getText().toString().equals("0")) {
                            AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
                            myDialog.setView(v);
                            String dialogString = "Sisa cuti/ijin tidak cukup atau terdapat pengajuan cuti/ijin yang belum dikonfirmasi. Pengajuan cuti/ijin tidak dapat dilanjutkan.";
                            dialogText.setText(dialogString);
                            myDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            myDialog.show();
                        }
                        else if(data.get(1).equals("0")) {
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
                        else if(data.get(0).equals("invalid")){
                            final Session session = new Session(getContext());
                            session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
                                @Override
                                public void onSuccessResponse(String result) {
                                    ParseJson pj = new ParseJson(result);
                                    String token = pj.parseToken();
                                    session.editPreferences(token,session.getPreferences("username"),session.getPreferences("password"));
                                    showDialog();
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                        }
                        else {
                            AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
                            myDialog.setView(v);
                            String dialogString = data.get(0) + ". Apakah ingin melanjutkan pengajuan ijin ?";
                            dialogText.setText(dialogString);
                            myDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendCuti(data);
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
    private void sendCuti(List<String> data){
//        progressDialog.show();
        leavePermissionRequest.submitCuti(jatahCuti.get(subjek.getSelectedItem().toString()).getId(), dateConvertUtil.reverseConvert(from.getText().toString()), dateConvertUtil.reverseConvert(to.getText().toString()),
                data.get(1), data.get(2), data.get(3),notes.getText().toString(),new Callback() {
                    @Override
                    public void onSuccessResponse(String result) {
//                        progressDialog.dismiss();
                        ParseJson pj = new ParseJson(result);
                        String msg = pj.parseLogin();
                        if(msg.equals("SUCCESS")){
                            if(getContext()!=null)
                                Toast.makeText(getContext(),"Pengajuan ijin berhasil ",Toast.LENGTH_SHORT).show();
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
