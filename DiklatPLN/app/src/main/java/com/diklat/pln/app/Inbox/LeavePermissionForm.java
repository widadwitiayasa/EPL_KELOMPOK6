package com.diklat.pln.app.Inbox;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.diklat.pln.app.BaseActivity;
import com.diklat.pln.app.Callback;
import com.diklat.pln.app.CutiObject;
import com.diklat.pln.app.Inbox.ListMessage.LeavePermissionRequest;
import com.diklat.pln.app.ParseJson;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LeavePermissionForm extends BaseActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_permission_form);
        subjek = (Spinner)findViewById(R.id.leave_permission_subjekspnr);
        kirim = (Button)findViewById(R.id.leave_permission_kirimbtn);
        from = (EditText)findViewById(R.id.leave_permission_from);
        to = (EditText)findViewById(R.id.leave_permission_to);
        notes = (EditText)findViewById(R.id.leave_permission_note);
        ijinLeft = (TextView)findViewById(R.id.leave_permission_sisaijin);
        myCalendar = Calendar.getInstance();

        session = new Session(getBaseContext());
        leavePermissionRequest = new LeavePermissionRequest(getBaseContext(),session);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Form Ajukan Ijin");

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from.getText().toString().length()==0||to.getText().toString().length()==0){
                    Toast.makeText(getBaseContext(),"Form Dari/Sampai kosong",Toast.LENGTH_SHORT).show();
                }
                else if(subjek.getSelectedItem().toString().equals("- - -Pilih jenis cuti- - -")){
                    Toast.makeText(getBaseContext(),"Pilih Subjek Ijin Terlebih Dahulu",Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private void setView(){
        leavePermissionRequest.getJatahCuti(new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.d("result",result);
                ParseJson pj = new ParseJson(result);
                String msg = pj.parseLogin();
                List<String> subjekCuti = new ArrayList<>();
                jatahCuti = new HashMap<>();
                subjekCuti.add("- - -Pilih jenis cuti- - -");
                jatahCuti.put("- - -Pilih jenis cuti- - -",new CutiObject("0","0","0","0","0","0","0"));
                List<CutiObject> listData;
                if(msg.equals("ADA")){
                    listData = pj.parseCuti();
                    Toast.makeText(getBaseContext(),listData.get(4).toString(),Toast.LENGTH_SHORT).show();
                    for(CutiObject cuti:listData){
                        if(Integer.parseInt(cuti.getSisa())<0){
                            subjekCuti.add(cuti.getNamaCuti());
                            jatahCuti.put(cuti.getNamaCuti(),new CutiObject(cuti.getId(),
                                    cuti.getNamaCuti(),cuti.getMinLapor(),cuti.getMaxLaport(),
                                    cuti.getSisa(),cuti.getKeterangan(),cuti.getFlag()));
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item,subjekCuti);
                        subjek.setAdapter(arrayAdapter);
                    }
                }
                else {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item,subjekCuti);
                    subjek.setAdapter(arrayAdapter);
                    Toast.makeText(getBaseContext(),"Anda tidak memiliki jatah cuti",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    AdapterView.OnItemSelectedListener itemSelector = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = parent.getSelectedItem().toString();
            Log.d("SelectedItem",selectedItem);
            ijinLeft.setText(jatahCuti.get(selectedItem).getSisa());
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
                    new DatePickerDialog(LeavePermissionForm.this,dateFrom,myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;

                case R.id.leave_permission_to:
                    new DatePickerDialog(LeavePermissionForm.this,dateTo,myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    break;
            }
        }
    };

    private void updateLabel(EditText source){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        source.setText(sdf.format(myCalendar.getTime()));
    }
    private void showDialog(){
        final View v = LayoutInflater.from(this).inflate(R.layout.leave_permission_form_dialog,null);
        final TextView dialogText = (TextView)v.findViewById(R.id.dialog_leavepermission_text);
        leavePermissionRequest.ajukanCuti(jatahCuti.get(subjek.getSelectedItem().toString()).getId(),from.getText().toString(),to.getText().toString(),
        new Callback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.d("Ajukan cuti",result);
                ParseJson pj = new ParseJson(result);
                final List<String> data = pj.parseAjukanCuti();
                if(data.get(1).equals("0")){
                    AlertDialog.Builder myDialog = new AlertDialog.Builder(LeavePermissionForm.this);
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
                    AlertDialog.Builder myDialog = new AlertDialog.Builder(LeavePermissionForm.this);
                    myDialog.setView(v);
                    String dialogString = data.get(0) + ". Apakah ingin melanjutkan pengajuan ijin ?";
                    dialogText.setText(dialogString);
                    myDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendCuti(data);
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

            }
        });
    }
    private void sendCuti(List<String> data){
        leavePermissionRequest.submitCuti(jatahCuti.get(subjek.getSelectedItem().toString()).getId(), from.getText().toString(), to.getText().toString(),
                data.get(1), data.get(2), data.get(3),notes.getText().toString(),new Callback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        ParseJson pj = new ParseJson(result);
                        String msg = pj.parseLogin();
                        if(msg.equals("SUCCESS")){
                            Toast.makeText(getBaseContext(),"Pengajuan ijin berhasil ",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(getBaseContext(),"Pengajuan ijin gagal, silahkan ulangi pengiriman",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }
}
