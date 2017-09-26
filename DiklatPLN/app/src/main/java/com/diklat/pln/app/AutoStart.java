package com.diklat.pln.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Fandy Aditya on 8/29/2017.
 */

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        AlarmReceiverLifeLog alarm = new AlarmReceiverLifeLog();
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            {
//            alarm.setAlarm(context);
                context.startService(new Intent(context, NotificationServices.class));
            }
            else {
                context.startService(new Intent(context, NotificationServices.class));
            }
        }
    }
}
