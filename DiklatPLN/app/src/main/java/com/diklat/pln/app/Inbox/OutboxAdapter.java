package com.diklat.pln.app.Inbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diklat.pln.app.DateConvertUtil;
import com.diklat.pln.app.Inbox.ListMessage.DetailMessage;
import com.diklat.pln.app.Inbox.ListMessage.ListMessageObject;
import com.diklat.pln.app.OnLoadMore;
import com.diklat.pln.app.R;

import java.util.List;

/**
 * Created by Fandy Aditya on 6/8/2017.
 */

public class OutboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ListMessageObject> listData;
    Context context;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMore onLoadMoreListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public OutboxAdapter(RecyclerView rv, List<ListMessageObject> listData, Context context) {
        this.listData = listData;
        this.context = context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rv.getLayoutManager();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onScroolMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMore mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return listData.get(position)==null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.outbox_object,parent,false);
            return new OutboxAdapter.NormalViewHolder(v);
        }
        else if(viewType==VIEW_TYPE_LOADING){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar,parent,false);
            return new OutboxAdapter.LoadingViewHolder(v);
        }
        return null;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NormalViewHolder){
            final DateConvertUtil dateConvertUtil = new DateConvertUtil();
            final ListMessageObject listMessageObject = listData.get(position);
            final String[] date = listMessageObject.getFrom().split("[ .]");
            final String[] timeMinutes = date[1].split(":");
            final String jenisHari = listMessageObject.getHari().equals("1")?"Kerja":"Kalendar";
            ((NormalViewHolder)holder).jenisCuti.setText(listMessageObject.getName());
            ((NormalViewHolder)holder).date.setText(date[0]);
            ((NormalViewHolder)holder).time.setText(timeMinutes[0]+":"+timeMinutes[1]);
            String statusString = null;
            switch (listMessageObject.getKet()){
                case "1":{
                    statusString="Disetujui";
                    ((NormalViewHolder)holder).status.setTextColor(ContextCompat.getColor(context,R.color.acc_green));break;
                }
                case "2":{
                    statusString="Ditolak";
                    ((NormalViewHolder)holder).status.setTextColor(ContextCompat.getColor(context,R.color.red_btn2));break;
                }
                case "3":{
                    statusString="Belum dikonfirmasi";
                    ((NormalViewHolder)holder).status.setTextColor(ContextCompat.getColor(context,R.color.black_guideline));break;
                }
            }
            ((NormalViewHolder)holder).status.setText(statusString);
            ((NormalViewHolder)holder).ouboxlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openIntent(listMessageObject.getIdListMessage(),listMessageObject.getName(),
                            dateConvertUtil.convert(date[0]),timeMinutes[0]+":"+timeMinutes[1],listMessageObject.getFrom(),
                            dateConvertUtil.convert(listMessageObject.getDari()),dateConvertUtil.convert(listMessageObject.getSampai()),
                            listMessageObject.getSubjek(),listMessageObject.getKet(),listMessageObject.getTo(),
                            listMessageObject.getTime(),listMessageObject.getNotes(),listMessageObject.getDate(),listMessageObject.getDurasi(),jenisHari);
                }
            });
        }
        else if (holder instanceof LoadingViewHolder){
            ((OutboxAdapter.LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        }
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ouboxlay;
        TextView date;
        TextView time;
        TextView status;
        TextView jenisCuti;
        public NormalViewHolder(View itemView) {
            super(itemView);
            ouboxlay = (LinearLayout)itemView.findViewById(R.id.outbox_objectlay);
            date = (TextView) itemView.findViewById(R.id.outbox_object_date);
            time = (TextView) itemView.findViewById(R.id.outbox_object_time);
            status = (TextView) itemView.findViewById(R.id.outbox_object_status);
            jenisCuti = (TextView)itemView.findViewById(R.id.outbox_object_jeniscuti);
        }
    }
    private void openIntent(String id, String jenisIjin, String date, String time,
                            String name,String dari, String sampai, String subjek,
                            String status,String disetujui,String flag,String pesan,String dikonfirmasiTgl,String durasi,String jenisHari){
        Intent myIntent = new Intent(context,DetailMessage.class);
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("jenis",jenisIjin);
        bundle.putString("date",date);
        bundle.putString("time",time);
        bundle.putString("name",name);
        bundle.putString("dari",dari);
        bundle.putString("sampai",sampai);
        bundle.putString("subjek",subjek);
        bundle.putString("status",status);
        bundle.putString("disetujui",disetujui);
        bundle.putString("timeFlag",flag);
        bundle.putString("parameter","outbox");
        bundle.putString("notes",pesan);
        bundle.putString("diterimaTgl",dikonfirmasiTgl);
        bundle.putString("diajukanTgl",dari);
        bundle.putString("durasi",durasi);
        bundle.putString("jenisHari",jenisHari);


        myIntent.putExtras(bundle);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}
