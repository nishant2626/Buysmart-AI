package com.buysmart.ai.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.buysmart.ai.models.PriceAlert;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.NotificationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PriceAlertService extends IntentService {

    private static final String TAG = "PriceAlertService";

    public PriceAlertService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;

        String action = intent.getStringExtra("action");
        if ("check_alerts".equals(action)) {
            checkPriceAlerts();
        }
    }

    private void checkPriceAlerts() {
        FirebaseManager firebase = FirebaseManager.getInstance();
        
        // Get all active alerts
        firebase.getUserAlerts(firebase.getCurrentUserId(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    
                    for (DocumentSnapshot doc : documents) {
                        PriceAlert alert = doc.toObject(PriceAlert.class);
                        if (alert != null && alert.isActive() && !alert.isNotified()) {
                            checkAlertPrice(alert);
                        }
                    }
                }
            }
        });
    }

    private void checkAlertPrice(PriceAlert alert) {
        FirebaseManager firebase = FirebaseManager.getInstance();
        
        firebase.getProduct(alert.getProductId(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Product product = task.getResult().toObject(Product.class);
                    if (product != null) {
                        double currentPrice = product.getLowestPrice();
                        
                        // Update current price in alert
                        alert.setCurrentPrice(currentPrice);
                        firebase.updateAlert(alert.getAlertId(), 
                                new java.util.HashMap<String, Object>() {{
                                    put("currentPrice", currentPrice);
                                }}, null);
                        
                        // Check if price dropped below target
                        if (currentPrice <= alert.getTargetPrice()) {
                            triggerAlert(alert, product);
                        }
                    }
                }
            }
        });
    }

    private void triggerAlert(PriceAlert alert, Product product) {
        FirebaseManager firebase = FirebaseManager.getInstance();
        
        // Mark alert as notified
        alert.setNotified(true);
        alert.setTriggeredAt(System.currentTimeMillis());
        
        firebase.updateAlert(alert.getAlertId(), new java.util.HashMap<String, Object>() {{
            put("isNotified", true);
            put("triggeredAt", alert.getTriggeredAt());
        }}, null);
        
        // Show notification
        NotificationHelper.showPriceAlertNotification(this, 
                product.getName(), 
                alert.getTargetPrice(), 
                alert.getCurrentPrice());
    }
}
