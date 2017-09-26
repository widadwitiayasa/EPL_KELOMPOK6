package com.diklat.pln.app.Profile.PresenceList;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diklat.pln.app.R;

import java.util.List;

public class PresenceListAdapter extends RecyclerView.Adapter<PresenceListAdapter.ViewHolder> {

    private List<PresenceObject> listItem;
    private Context context;

    public PresenceListAdapter(List<PresenceObject> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.presence_object,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PresenceObject presenceObject = listItem.get(position);
        holder.date.setText(presenceObject.getDate());
        String statusArrivalString = null;
        String statausHomeString = null;
        if(!presenceObject.getStatusCuti().equals("null") && presenceObject.getStatusHome().equals("") && presenceObject.getStatusArrival().equals("") || !presenceObject.getStatusCuti().equals("null") && presenceObject.getStatusHome().equals("") && presenceObject.getStatusArrival().equals("") ){
            holder.statusLay.setVisibility(View.GONE);
            holder.cutiLay.setVisibility(View.VISIBLE);
            if(presenceObject.getStatusLibur().equals("null")&&presenceObject.getStatusCuti().equals("null")){
                holder.cutiText.setText("Tanpa Keterangan");
            }
            if(!presenceObject.getStatusLibur().equals("null")){
                holder.cutiText.setText(presenceObject.getStatusLibur());
            }
            else
                holder.cutiText.setText(presenceObject.getStatusCuti());
        }
        else{
            holder.cutiLay.setVisibility(View.GONE);
            holder.statusLay.setVisibility(View.VISIBLE);
            switch (presenceObject.getStatusArrival()){
                case "1" :
                    statusArrivalString="Datang Tepat Waktu";
                    holder.statusArrival.setTextColor(ContextCompat.getColor(context,R.color.acc_green));break;
                case "2" :
                    statusArrivalString="Datang Terlambat";
                    holder.statusArrival.setTextColor(ContextCompat.getColor(context,R.color.red_btn2));break;
                case "3":
                    if(!presenceObject.getStatusLibur().equals("null")){
                        statusArrivalString = presenceObject.getStatusLibur();
                    }
                    else
                        statusArrivalString = presenceObject.getStatusCuti();
                    holder.statusArrival.setTextColor(ContextCompat.getColor(context,R.color.black_guideline));
                    break;
                case "" : statusArrivalString ="Belum Absen";
                    holder.statusArrival.setTextColor(ContextCompat.getColor(context,R.color.black_guideline));
                    break;
            }
            switch (presenceObject.getStatusHome()){
                case "1":
                    statausHomeString="Pulang Tepat Waktu";
                    holder.statusHome.setTextColor(ContextCompat.getColor(context,R.color.acc_green));break;
                case "2":
                    statausHomeString = "Pulang Lebih Awal";
                    holder.statusHome.setTextColor(ContextCompat.getColor(context,R.color.red_btn2));break;
                case "3":
                    if(!presenceObject.getStatusLibur().equals("null")){
                        statausHomeString = presenceObject.getStatusLibur();
                    }
                    else
                        statausHomeString = presenceObject.getStatusCuti();
                    holder.statusArrival.setTextColor(ContextCompat.getColor(context,R.color.black_guideline));
                    break;
                case "":
                    statausHomeString="Belum Absen";
                    holder.statusHome.setTextColor(ContextCompat.getColor(context,R.color.black_guideline));
                    break;
            }
        }
        holder.statusArrival.setText(statusArrivalString);
        holder.statusHome.setText(statausHomeString);
        setTime(holder.homeTime,presenceObject.getHomeTime(),holder.kurungBukaHome,holder.kurungTutupHome,false,null);
        setTime(holder.homeMust,presenceObject.getHomeMust(),holder.kurungBukaHome,holder.kurungTutupHome,true,holder.homeTime);
        setTime(holder.arrivalTime,presenceObject.getArrivalTime(),holder.kurungBukaArrival,holder.kurungTutupArrival,false,null);
        setTime(holder.arrivalMust,presenceObject.getArrivalMust(),holder.kurungBukaArrival,holder.kurungTutupArrival,true,holder.arrivalTime);

//        holder.homeTime.setText(presenceObject.getHomeTime());
//        holder.arrivalTime.setText(presenceObject.getArrivalTime());
//        holder.arrivalMust.setText(presenceObject.getArrivalMust());
//        holder.homeMust.setText(presenceObject.getHomeMust());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView arrivalTime;
        TextView homeTime;
        TextView statusArrival;
        TextView statusHome;
        TextView arrivalMust;
        TextView homeMust;
        TextView kurungBukaArrival;
        TextView kurungTutupArrival;
        TextView kurungBukaHome;
        TextView kurungTutupHome;
        LinearLayout objectLay;
        LinearLayout cutiLay;
        LinearLayout statusLay;
        TextView cutiText;

        public ViewHolder(View itemView) {
            super(itemView);

            date = (TextView)itemView.findViewById(R.id.presence_object_date);
            arrivalTime = (TextView)itemView.findViewById(R.id.presence_object_arrival);
            homeTime = (TextView)itemView.findViewById(R.id.presence_object_hometime);
            statusArrival = (TextView)itemView.findViewById(R.id.presence_object_statusdtg);
            statusHome = (TextView)itemView.findViewById(R.id.presence_object_statusplg);
            arrivalMust = (TextView)itemView.findViewById(R.id.presence_object_arrivalmust);
            homeMust = (TextView)itemView.findViewById(R.id.presence_object_hometimemust);
            objectLay = (LinearLayout) itemView.findViewById(R.id.presence_object_lay);
            cutiLay = (LinearLayout) itemView.findViewById(R.id.presence_object_cutiliburlay);
            statusLay = (LinearLayout)itemView.findViewById(R.id.presence_object_statuslay);
            cutiText = (TextView)itemView.findViewById(R.id.presence_object_cutilibur);
            kurungBukaArrival = (TextView)itemView.findViewById(R.id.presence_object_kurbukaarrival);
            kurungTutupArrival = (TextView)itemView.findViewById(R.id.presence_object_kurtutuparrival);
            kurungBukaHome = (TextView)itemView.findViewById(R.id.presence_object_kurbukahometime);
            kurungTutupHome = (TextView)itemView.findViewById(R.id.presence_object_kurtutuphometime);
        }
    }
    private void setTime(TextView textView, String value,TextView kurungBuka, TextView kurungTutup, Boolean isMust,TextView child){
        if(isMust){
            if(!value.equals("null")){
                if(child.getText().equals("")){
                    textView.setText("-");
                    kurungBuka.setVisibility(View.GONE);
                    kurungTutup.setVisibility(View.GONE);
                }
                else{
                    String date[] = value.split(":");
                    textView.setText(date[0]+":"+date[1]);
                    kurungBuka.setVisibility(View.VISIBLE);
                    kurungTutup.setVisibility(View.VISIBLE);
                }
            }
            else {
                textView.setText("-");
                kurungBuka.setVisibility(View.GONE);
                kurungTutup.setVisibility(View.GONE);
            }
        }
        else{
            if(!value.equals("null")){
                String date[] = value.split(":");
                textView.setText(date[0]+":"+date[1]);
                kurungBuka.setVisibility(View.VISIBLE);
                kurungTutup.setVisibility(View.VISIBLE);
            }
            else {
                textView.setText("");
                kurungBuka.setVisibility(View.GONE);
                kurungTutup.setVisibility(View.GONE);
            }
        }
    }
}