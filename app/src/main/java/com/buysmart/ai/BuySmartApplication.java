package com.buysmart.ai;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.buysmart.ai.utils.SessionManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.io.File;
import java.util.Locale;

public class BuySmartApplication extends Application {

    public static final String CHANNEL_PRICE_ALERTS = "price_alerts";
    public static final String CHANNEL_DEALS = "deals";
    public static final String CHANNEL_GENERAL = "general";

    private static BuySmartApplication instance;
    private SharedPreferences preferences;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        sessionManager = new SessionManager(this);
        applyTheme();
        
        FirebaseApp.initializeApp(this);
        
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        
        preferences = getSharedPreferences("BuySmartPrefs", Context.MODE_PRIVATE);
        
        createNotificationChannels();
        setDefaultLanguage();
    }

    public static BuySmartApplication getInstance() {
        return instance;
    }

    public void applyTheme() {
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            NotificationChannel priceChannel = new NotificationChannel(
                    CHANNEL_PRICE_ALERTS,
                    getString(R.string.channel_price_alerts),
                    NotificationManager.IMPORTANCE_HIGH
            );
            priceChannel.setDescription(getString(R.string.channel_price_alerts_desc));
            notificationManager.createNotificationChannel(priceChannel);

            NotificationChannel dealsChannel = new NotificationChannel(
                    CHANNEL_DEALS,
                    getString(R.string.channel_deals),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            dealsChannel.setDescription(getString(R.string.channel_deals_desc));
            notificationManager.createNotificationChannel(dealsChannel);

            NotificationChannel generalChannel = new NotificationChannel(
                    CHANNEL_GENERAL,
                    getString(R.string.channel_general),
                    NotificationManager.IMPORTANCE_LOW
            );
            generalChannel.setDescription(getString(R.string.channel_general_desc));
            notificationManager.createNotificationChannel(generalChannel);
        }
    }

    private void setDefaultLanguage() {
        String language = preferences.getString("language", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public void setLanguage(String languageCode) {
        preferences.edit().putString("language", languageCode).apply();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public String getCurrentLanguage() {
        return preferences.getString("language", "en");
    }

    public void clearCache() {
        // 1. Clear Glide image cache
        Glide.get(this).clearMemory();
        new Thread(() -> Glide.get(this).clearDiskCache()).start();

        // 2. Clear Internal Application Cache folder
        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }
}
