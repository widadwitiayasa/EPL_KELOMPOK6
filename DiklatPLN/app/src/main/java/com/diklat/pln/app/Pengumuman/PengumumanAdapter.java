package com.diklat.pln.app.Pengumuman;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diklat.pln.app.DateConvertUtil;
import com.diklat.pln.app.LocalDB;
import com.diklat.pln.app.R;

import java.util.List;

/**
 * Created by Fandy Aditya on 5/13/2017.
 */

public class PengumumanAdapter extends RecyclerView.Adapter<PengumumanAdapter.ViewHolder> {

    Context context;
    List<PengumumanObject> listData;
    LocalDB localDB;

    public PengumumanAdapter(Context context, List<PengumumanObject> listData,LocalDB localDB) {
        this.context = context;
        this.listData = listData;
        this.localDB = localDB;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pengumuman_object,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PengumumanObject pengumumanObject = listData.get(position);
        DateConvertUtil dateConvertUtil = new DateConvertUtil();
        String id = pengumumanObject.getIdPengumuman();
        holder.judul.setText(pengumumanObject.getJudulPengumuman());
        String deskripsiSingkat="";
        if(pengumumanObject.getDeskripsiPengumuman().length()>30){
            deskripsiSingkat = pengumumanObject.getDeskripsiPengumuman().substring(0,30);
        }
        else deskripsiSingkat = pengumumanObject.getDeskripsiPengumuman();
        holder.deskripsiSingkat.setText(deskripsiSingkat);
        holder.tanggal.setText(dateConvertUtil.convert(pengumumanObject.getTanggalPengumuman()));
        holder.pengumumanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(pengumumanObject.getJudulPengumuman(),pengumumanObject.getDeskripsiPengumuman(),pengumumanObject.getIdPengumuman());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView pengumumanCard;
        TextView judul;
        TextView deskripsiSingkat;
        TextView tanggal;

        public ViewHolder(View itemView) {
            super(itemView);
            judul = (TextView)itemView.findViewById(R.id.pengumuman_judul_objek);
            deskripsiSingkat = (TextView)itemView.findViewById(R.id.pengumuman_deskripsi_objek);
            tanggal = (TextView)itemView.findViewById(R.id.pengumuman_tanggal_objek);
            pengumumanCard = (CardView)itemView.findViewById(R.id.pengumuman_objeklay);
        }
    }
    private void showDialog(String judul, String deskripsi, final String id){
        final ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("status_lihat","1");
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_pengumuman,null);
        TextView deskripsiPanjang = (TextView) v.findViewById(R.id.dialog_pengumuman_deskripsipanjang);
        TextView judulPengumuman = (TextView)v.findViewById(R.id.dialog_pengumuman_judul);
        deskripsiPanjang.setText(deskripsi);
        judulPengumuman.setText(judul);
        AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
        myDialog.setView(v);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateStatusLihat(contentValues);
            }
        });
        myDialog.show();
    }
    private void updateStatusLihat(ContentValues contentValues){
        localDB.update(contentValues);
    }
}
