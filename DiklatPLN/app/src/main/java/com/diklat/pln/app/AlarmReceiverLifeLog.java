package com.diklat.pln.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diklat.pln.app.Inbox.ListMessage.LeavePermissionRequest;
import com.diklat.pln.app.Inbox.ListMessage.ListMessageObject;
import com.diklat.pln.app.Pengumuman.PengumumanObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Fandy Aditya on 7/5/2017.
 */

public class AlarmReceiverLifeLog extends BroadcastReceiver {
    private LeavePermissionRequest leavePermissionRequest;
    private Session session;
    private int badgeCount;
    private int badgePengumuman;
    private AlarmReceiverLifeLog alarm;
    Boolean isForeground;


    @Override
    public void onReceive(Context context, Intent intent) {

        alarm = new AlarmReceiverLifeLog();
        session = new Session(context);
        leavePermissionRequest = new LeavePermissionRequest(context,session);
        badgeCount = 0;
        badgePengumuman = 0;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
//        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        getNewPengumuman(context);
        getNewInboxMessage(context);
//        context.startService(new Intent(context,NotificationServices.class));
//        setAlarm(context);

        wl.release();
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiverLifeLog.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 300000 , pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmReceiverLifeLog.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private void getNewPengumuman(final Context context){
        final Session session = new Session(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Konstanta.URL + "/backend/API/Pengumuman/getPengumumanActive", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ParseJson parseJson = new ParseJson(response);
                List<PengumumanObject> list = parseJson.listPengumumanParse();
                if(list.size()>Integer.parseInt(session.getPreferences("pengumuman"))){
                    badgePengumuman = list.size()-Integer.parseInt(session.getPreferences("pengumuman"));
                    pengumumanNotification(context);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("token",session.getPreferences("token"));
                return header;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    private void getNewInboxMessage(final Context context){
//        leavePermissionRequest.getInbox("", new Callback() {
//            @Override
//            public void onSuccessResponse(String result) {
//                ParseJson pj = new ParseJson(result);
//                List<ListMessageObject> listData = pj.parseInbox();
//                for(ListMessageObject message:listData){
//                    if(message.getStatusLihat().equals("2")){
//                        notificationShow();
//                    }
//                }
//            }
//        });
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Konstanta.URL + "/backend/API/Cuti/inbox_cuti", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ParseJson pj = new ParseJson(response);
                List<ListMessageObject> listData = pj.parseInbox();
                if(listData.size()>0){
                    notificationShow(context);
                }
                badgeCount = listData.size()+badgePengumuman;
                session.editPreferencesBadgeicon(badgeCount);
                ShortcutBadger.applyCount(context,badgeCount);
//                for(ListMessageObject message:listData){
//                    if(message.getStatusLihat().equals("2")){
//                        notificationShow();
//                    }
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(Konstanta.URL.equals(Konstanta.IPLOCAL)){
                    Konstanta.URL=Konstanta.IPPUBLIC;
//                    Toast.makeText(getBaseContext(),"Mengganti koneksi ke jaringan Luar PLN . . .",Toast.LENGTH_SHORT).show();
                }
                else if(Konstanta.URL.equals(Konstanta.IPPUBLIC)){
                    Konstanta.URL=Konstanta.IPLOCAL;
                }
//                Toast.makeText(getBaseContext(),"Mengganti koneksi ke jaringan PLN",Toast.LENGTH_SHORT).show();
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
                param.put("status","");
                param.put("lihat","2");
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void notificationShow(Context context){
//        session.loginProcess(session.getPreferences("username"), session.getPreferences("password"), new Callback() {
//            @Override
//            public void onSuccessResponse(String result) {
//                Intent intent = new Intent(getBaseContext(),TabbedActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);
//                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(getBaseContext())
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Notifikasi")
//                        .setContentText("Ada Pesan Baru Masuk")
//                        .setAutoCancel(true)
//                        .setSound(sound)
//                        .setContentIntent(pendingIntent);
//                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(0, notifBuilder.build());
//            }
//        });
        Intent intent = new Intent(context,TabbedActivity.class);
        intent.putExtra("notifpesan","2");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("Notifikasi")
                .setContentText("Ada Pesan Baru Masuk")
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build());
    }
    private void pengumumanNotification(Context context){
        Intent intent = new Intent(context,TabbedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("Notifikasi")
                .setContentText("Pengumuman Baru")
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build());
    }

}
