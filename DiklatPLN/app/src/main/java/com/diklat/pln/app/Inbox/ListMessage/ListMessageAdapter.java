package com.diklat.pln.app.Inbox.ListMessage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diklat.pln.app.DateConvertUtil;
import com.diklat.pln.app.OnLoadMore;
import com.diklat.pln.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fandy Aditya on 4/29/2017.
 */

public class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListMessageObject> listItem;
    private Context context;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private OnLoadMore onLoadMoreListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public ListMessageAdapter(RecyclerView recyclerView, List<ListMessageObject> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        return listItem.get(position)==null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_object,parent,false);
            return new NormalViewHolder(v);
        }
        else if(viewType==VIEW_TYPE_LOADING){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar,parent,false);
            return new LoadingViewHolder(v);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NormalViewHolder){
            Log.d("masukboyki","yeahhh");
            final ListMessageObject listMessageObject = listItem.get(position);
            final DateConvertUtil dateConvertUtil = new DateConvertUtil();
            String stringName = "";
            String subjectString = "";
            final String jenisHari = listMessageObject.getHari().equals("1")?"Kerja":"Kalendar";
            if(listMessageObject.getSubjek().equals("0")){
                subjectString = "Informasi Persetujuan Ijin";
                stringName = listMessageObject.getTo();
                if (listMessageObject.getKet().equals("1")){
                    ((NormalViewHolder) holder).statusLine.setVisibility(View.VISIBLE);
                    ((NormalViewHolder) holder).statusLine.setBackgroundColor(Color.parseColor("#66BB6A"));
                }
                else if (listMessageObject.getKet().equals("2")){
                    ((NormalViewHolder) holder).statusLine.setVisibility(View.VISIBLE);
                    ((NormalViewHolder) holder).statusLine.setBackgroundColor(Color.parseColor("#ef5350"));
                }
                ((NormalViewHolder) holder).range.setVisibility(View.GONE);
            }
            else if(listMessageObject.getSubjek().equals("1")){
                subjectString = "Permintaan Persetujuan Ijin";
                stringName = listMessageObject.getFrom();
                ((NormalViewHolder) holder).statusLine.setVisibility(View.GONE);
                ((NormalViewHolder) holder).range.setVisibility(View.VISIBLE);
            }
            else if(listMessageObject.getSubjek().equals("3")){
                subjectString = "Pesan Dari Sistem";
                stringName = listMessageObject.getFrom();
                ((NormalViewHolder) holder).statusLine.setVisibility(View.GONE);
                ((NormalViewHolder) holder).statusLine.setVisibility(View.VISIBLE);
                ((NormalViewHolder) holder).statusLine.setBackgroundColor(Color.parseColor("#fbc02d"));
                ((NormalViewHolder) holder).range.setVisibility(View.GONE);
            }

            if(listMessageObject.getStatusLihat().equals("1")){
                ((NormalViewHolder) holder).subject.setTypeface(null, Typeface.NORMAL);
                ((NormalViewHolder) holder).name.setTypeface(null, Typeface.NORMAL);
                ((NormalViewHolder) holder).range.setTypeface(null,Typeface.NORMAL);
            }
            else {
                ((NormalViewHolder) holder).subject.setTypeface(null, Typeface.BOLD);
                ((NormalViewHolder) holder).name.setTypeface(null, Typeface.BOLD);
                ((NormalViewHolder) holder).range.setTypeface(null,Typeface.BOLD);
            }
            if(listMessageObject.getHari().equals("1")){

            }

            ((NormalViewHolder) holder).name.setText(stringName);
            ((NormalViewHolder) holder).subject.setText(subjectString);
            final String[] date = listMessageObject.getDate().split("[ .]");
            final String diajukanTglString[] = listMessageObject.getDiajukanTgl().split("[ .]");
            final String diterimaTglString[] = listMessageObject.getDiterimaTgl().split("[ .]");
            final String[] timeMinutes = date[1].split(":");
            String range = dateConvertUtil.convert(listMessageObject.getDari()) + " s/d " + dateConvertUtil.convert(listMessageObject.getSampai());
            ((NormalViewHolder) holder).time.setText(timeMinutes[0]+":"+timeMinutes[1]);
            ((NormalViewHolder) holder).date.setText(dateConvertUtil.convert(date[0]));
            ((NormalViewHolder) holder).range.setText(range);
//        holder.time.setText(date[1]);
            final String finalStringName = stringName;
            ((NormalViewHolder) holder).messageLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openIntent(listMessageObject.getIdMsgId(),listMessageObject.getIdListMessage(),listMessageObject.getName(),dateConvertUtil.convert(date[0]),timeMinutes[0]+":"+timeMinutes[1], finalStringName,dateConvertUtil.convert(listMessageObject.getDari()),dateConvertUtil.convert(listMessageObject.getSampai()),
                            listMessageObject.getSubjek(),listMessageObject.getKet(),listMessageObject.getFromPhoto(),listMessageObject.getToPhoto(),listMessageObject.getTo(),listMessageObject.getFrom(),listMessageObject.getTime(),listMessageObject.getNotes(),dateConvertUtil.convert(diajukanTglString[0]),dateConvertUtil.convert(diterimaTglString[0]),listMessageObject.getDurasi(),jenisHari);
                }
            });
        }
        else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return listItem == null ? 0 : listItem.size();
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
        CardView cardView;
        LinearLayout messageLay;
        TextView name;
        TextView subject;
        TextView date;
        TextView time;
        View statusLine;
        TextView range;

        public NormalViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_message_objectname);
            subject = (TextView) itemView.findViewById(R.id.list_message_objectsubjek);
            date = (TextView) itemView.findViewById(R.id.list_message_objectgl);
            time = (TextView) itemView.findViewById(R.id.list_message_objectwaktu);
            messageLay = (LinearLayout)itemView.findViewById(R.id.list_message_objectlay);
            cardView = (CardView)itemView.findViewById(R.id.list_message_cardview);
            statusLine = itemView.findViewById(R.id.list_message_statusline);
            range= (TextView) itemView.findViewById(R.id.list_message_range);
        }
    }

    private void openIntent(String lihatId, String id, String jenisIjin, String date, String time, String name,String dari,
                            String sampai, String subjek, String status,String fromPhoto,String toPhoto,String disetujui,
                            String from,String flag,String notes,String diajukanTgl,String diterimaTgl,String durasi,String jenisHari){
        Intent myIntent = new Intent(context,DetailMessage.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("msgid",lihatId);
        bundle.putString("id",id);
        bundle.putString("jenis",jenisIjin);
        bundle.putString("date",date);
        bundle.putString("time",time);
        bundle.putString("name",name);
        bundle.putString("dari",dari);
        bundle.putString("sampai",sampai);
        bundle.putString("subjek",subjek);
        bundle.putString("status",status);
        bundle.putString("fromphoto",fromPhoto);
        bundle.putString("tophoto",toPhoto);
        bundle.putString("disetujui",disetujui);
        bundle.putString("from",from);
        bundle.putString("parameter","inbox");
        bundle.putString("timeFlag",flag);
        bundle.putString("notes",notes);
        bundle.putString("diajukanTgl",diajukanTgl);
        bundle.putString("diterimaTgl",diterimaTgl);
        bundle.putString("durasi",durasi);
        bundle.putString("jenisHari",jenisHari);
        myIntent.putExtras(bundle);
        context.startActivity(myIntent);
    }
    public void setFilter(List<ListMessageObject> messageFilter){
        listItem = new ArrayList<>();
        listItem.addAll(messageFilter);
        notifyDataSetChanged();
    }
}
