package com.derric.quickbarads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
//            Class<? extends Service> service = QuickBarService.class;
//            Intent startIntent = new Intent(context, service);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ContextCompat.startForegroundService(context, startIntent);
//            } else {
//                context.startService(startIntent);
//            }
//        }

    }
}
