package com.diklat.pln.app;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import java.util.Timer;
import java.util.TimerTask;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Fandy Aditya on 6/12/2017.
 */

public class NotificationServices extends Service {
    private Timer timer;
    private LeavePermissionRequest leavePermissionRequest;
    private Session session;
    private int badgeCount;
    private int badgePengumuman;
    private AlarmReceiverLifeLog alarm;
    Boolean isForeground;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        alarm = new AlarmReceiverLifeLog();
        timer.schedule(timerTask,60000,60000 * 5);
        session = new Session(getBaseContext());
        leavePermissionRequest = new LeavePermissionRequest(getBaseContext(),session);
        badgeCount = 0;
        badgePengumuman = 0;
//        getNewPengumuman();
//        getNewInboxMessage();
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
//            try {
//                getForeground();
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
            getNewPengumuman();
            getNewInboxMessage();
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        alarm.setAlarm(getBaseContext());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer.cancel();
//        startService(new Intent(this, NotificationServices.class));
        sendBroadcast(new Intent(this, AutoStart.class));
//        timerTask.cancel();
    }
    private void getForeground() throws PackageManager.NameNotFoundException {
        ActivityManager am = (ActivityManager) NotificationServices.this.getSystemService(ACTIVITY_SERVICE);
// The first in the list of RunningTasks is always the foreground task.
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
        PackageManager pm = NotificationServices.this.getPackageManager();
        PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();

       if(foregroundAppPackageInfo!=null){
           isForeground=true;
       }
       else{
           isForeground=false;
       }
    }
    private void getNewPengumuman(){
        final Session session = new Session(getBaseContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Konstanta.URL + "/backend/API/Pengumuman/getPengumumanActive", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ParseJson parseJson = new ParseJson(response);
                List<PengumumanObject> list = parseJson.listPengumumanParse();
                if(list.size()>Integer.parseInt(session.getPreferences("pengumuman"))){
                    badgePengumuman = list.size()-Integer.parseInt(session.getPreferences("pengumuman"));
                    pengumumanNotification();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);
    }
    private void getNewInboxMessage(){
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
                    notificationShow();
                }
                badgeCount = listData.size()+badgePengumuman;
                session.editPreferencesBadgeicon(badgeCount);
                ShortcutBadger.applyCount(getBaseContext(),badgeCount);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);
    }
    private void notificationShow(){
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
        Intent intent = new Intent(getBaseContext(),TabbedActivity.class);
        intent.putExtra("notifpesan","2");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(getBaseContext())
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("Notifikasi")
                .setContentText("Ada Pesan Baru Masuk")
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build());
    }
    private void pengumumanNotification(){
        Intent intent = new Intent(getBaseContext(),TabbedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(getBaseContext())
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("Notifikasi")
                .setContentText("Pengumuman Baru")
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifBuilder.build());
    }
}
