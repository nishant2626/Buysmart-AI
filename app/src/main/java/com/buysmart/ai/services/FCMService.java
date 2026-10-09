package com.buysmart.ai.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.buysmart.ai.R;
import com.buysmart.ai.activities.MainActivity;
import com.buysmart.ai.activities.ProductDetailsActivity;
import com.buysmart.ai.utils.FirebaseManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Send token to server
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Map<String, String> data = message.getData();
        String type = data.get("type");

        if (type != null) {
            switch (type) {
                case "price_drop":
                    showPriceDropNotification(data);
                    break;
                case "price_alert":
                    showPriceAlertNotification(data);
                    break;
                case "deal":
                    showDealNotification(data);
                    break;
                default:
                    showGeneralNotification(message.getNotification());
                    break;
            }
        } else if (message.getNotification() != null) {
            showGeneralNotification(message.getNotification());
        }
    }

    private void sendTokenToServer(String token) {
        String userId = FirebaseManager.getInstance().getCurrentUserId();
        if (userId != null) {
            // Update token in Firestore
            FirebaseManager.getInstance().updateUser(userId, 
                    new java.util.HashMap<String, Object>() {{
                        put("fcmToken", token);
                    }}, task -> {
                        // Handle completion
                    });
        }
    }

    private void showPriceDropNotification(Map<String, String> data) {
        String productId = data.get("productId");
        String productName = data.get("productName");
        String oldPrice = data.get("oldPrice");
        String newPrice = data.get("newPrice");

        String title = "Price Drop Alert!";
        String message = String.format("%s dropped from ₹%s to ₹%s", 
                productName, oldPrice, newPrice);

        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, 
                com.buysmart.ai.BuySmartApplication.CHANNEL_PRICE_ALERTS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showPriceAlertNotification(Map<String, String> data) {
        String productId = data.get("productId");
        String productName = data.get("productName");
        String targetPrice = data.get("targetPrice");
        String currentPrice = data.get("currentPrice");

        String title = "Target Price Reached!";
        String message = String.format("%s is now ₹%s (your target: ₹%s)", 
                productName, currentPrice, targetPrice);

        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, 
                com.buysmart.ai.BuySmartApplication.CHANNEL_PRICE_ALERTS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.secondary))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showDealNotification(Map<String, String> data) {
        String title = data.get("title");
        String message = data.get("message");
        String productId = data.get("productId");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tab", "home");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, 
                com.buysmart.ai.BuySmartApplication.CHANNEL_DEALS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.accent))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showGeneralNotification(RemoteMessage.Notification notification) {
        if (notification == null) return;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, 
                com.buysmart.ai.BuySmartApplication.CHANNEL_GENERAL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getBody()));

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
