package com.buysmart.ai.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.buysmart.ai.services.PriceAlertService;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                // Schedule price alert checks on boot
                Intent serviceIntent = new Intent(context, PriceAlertService.class);
                serviceIntent.putExtra("action", "check_alerts");
                context.startService(serviceIntent);
            }
        }
    }
}
