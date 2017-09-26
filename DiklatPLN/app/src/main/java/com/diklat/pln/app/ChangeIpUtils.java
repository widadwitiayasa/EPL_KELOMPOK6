package com.diklat.pln.app;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Fandy Aditya on 7/12/2017.
 */

public class ChangeIpUtils extends AlertDialog{
    private Context context;
    private MenuItem ipLocal;
    private MenuItem ipPublic;
    private String textMessage;
    private Menu menu;

    public ChangeIpUtils(Context context) {
        super(context);
        this.context = context;
    }

    public void now(){
        checkNetwork();
    }
    public void loginChange(){
        if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
            Konstanta.URL=Konstanta.IPPUBLIC;
        }
        else if(Konstanta.URL.equals(Konstanta.IPPUBLIC)){
            Konstanta.URL=Konstanta.IPLOCAL;
        }
    }
    private void checkNetwork(){
        if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
            showAlert("local");
        }
        else{
            showAlert("public");
        }
    }
    public void setNetwork(Menu menu){
        this.menu = menu;
        ipLocal = menu.findItem(R.id.ip_local);
        ipPublic = menu.findItem(R.id.ip_public);
        if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
            ipLocal.setChecked(true);
        }
        else{
            ipPublic.setChecked(true);
        }
    }
    private void showAlert(final String network){
        View v = LayoutInflater.from(getContext()).inflate(R.layout.leave_permission_form_dialog,null);
        TextView textView = (TextView) v.findViewById(R.id.dialog_leavepermission_text);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(v);
        switch (network){
            case "local":
                textMessage = "Tidak dapat terhubung dengan server. Apakah anda berada di luar jaringan PLN?";break;
            case "public":
                textMessage = "Tidak dapat terhubung dengan server. Apakah anda berada di jaringan PLN?";break;
        }
        textView.setText(textMessage);
        alert.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (network.equals("local")) {
                    Konstanta.URL = Konstanta.IPPUBLIC;
                    ipPublic.setChecked(true);
                } else {
                    Konstanta.URL = Konstanta.IPLOCAL;
                    ipLocal.setChecked(true);
                }
            }
        }).setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(context,"Tidak ada koneksi internet",Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

}
