package com.diklat.pln.app.Subordinate.ListSubordinate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diklat.pln.app.Konstanta;
import com.diklat.pln.app.R;
import com.diklat.pln.app.Subordinate.DetailSubordinate;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fandy Aditya on 6/16/2017.
 */

public class MagangAdapter extends RecyclerView.Adapter<MagangAdapter.ViewHolder> {

    private List<ListSubordinateObjek> listItem;
    private Context context;

    public MagangAdapter(List<ListSubordinateObjek> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subordinate_object, parent, false);
        return new MagangAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListSubordinateObjek listSubordinateObjek = listItem.get(position);

        final String id = listSubordinateObjek.getId();
        if(!listSubordinateObjek.getImg().equals("null"))
            Glide.with(context).load(Konstanta.URL+"/backend/photo/"+listSubordinateObjek.getImg()).into(holder.img);
        else {
            if(listSubordinateObjek.getGender().equals("Laki laki")){
                Glide.with(context).load(Konstanta.URL+"/backend/photo/default_l.png").into(holder.img);
            }
            else{
                Glide.with(context).load(Konstanta.URL+"/backend/photo/default_p.png").into(holder.img);
            }
        }
        holder.name.setText(listSubordinateObjek.getName());
        holder.position.setText(listSubordinateObjek.getPosition().toUpperCase());
        holder.subordinateLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView name;
        TextView position;
        LinearLayout subordinateLay;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (CircleImageView) itemView.findViewById(R.id.list_subordinate_img);
            name = (TextView) itemView.findViewById(R.id.list_subordinate_name);
            position = (TextView) itemView.findViewById(R.id.list_subordinate_position);
            subordinateLay = (LinearLayout) itemView.findViewById(R.id.list_subordinatelay);
        }
    }

    private void openIntent(String id){
        Bundle bundle = new Bundle();
        Intent myIntent = new Intent(context, DetailSubordinate.class);
        bundle.putString("id",id);
        bundle.putString("parameter","sementara");
        myIntent.putExtras(bundle);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}
