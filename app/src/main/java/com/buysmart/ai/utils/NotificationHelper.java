package com.buysmart.ai.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.buysmart.ai.R;
import com.buysmart.ai.activities.MainActivity;
import com.buysmart.ai.activities.ProductDetailsActivity;
import com.buysmart.ai.models.Product;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationHelper {

    public static final int NOTIFICATION_ID_PRICE_DROP = 1001;
    public static final int NOTIFICATION_ID_DEAL = 1002;
    public static final int NOTIFICATION_ID_ALERT = 1003;
    public static final int NOTIFICATION_ID_GENERAL = 1004;

    public static void showPriceDropNotification(Context context, Product product, double oldPrice, double newPrice) {
        String title = "Price Drop Alert!";
        String message = String.format("%s price dropped from ₹%.2f to ₹%.2f", 
                product.getName(), oldPrice, newPrice);

        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra("product", product);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 
                NOTIFICATION_ID_PRICE_DROP, intent, 
                getPendingIntentFlags());

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, 
                com.buysmart.ai.BuySmartApplication.CHANNEL_PRICE_ALERTS)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_PRICE_DROP, builder.build());
    }

    public static void showPriceAlertNotification(Context context, String productName, 
                                                   double targetPrice, double currentPrice) {
        String title = "Target Price Reached! 🔥";
        String message = String.format("%s is now ₹%,.0f (Target: ₹%,.0f)", 
                productName, currentPrice, targetPrice);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 
                NOTIFICATION_ID_ALERT, intent, 
                getPendingIntentFlags());

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, 
                com.buysmart.ai.BuySmartApplication.CHANNEL_PRICE_ALERTS)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.secondary))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_ALERT, builder.build());
    }

    private static int getPendingIntentFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.FLAG_UPDATE_CURRENT;
    }

    private static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
