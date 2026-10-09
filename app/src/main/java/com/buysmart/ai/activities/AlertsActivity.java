package com.buysmart.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.adapters.PriceAlertAdapter;
import com.buysmart.ai.databinding.ActivityAlertsBinding;
import com.buysmart.ai.models.PriceAlert;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsActivity extends AppCompatActivity {

    private ActivityAlertsBinding binding;
    private PriceAlertAdapter alertAdapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlertsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();

        setupToolbar();
        setupRecyclerView();
        setupClickListeners();

        loadAlerts();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        alertAdapter = new PriceAlertAdapter(new ArrayList<>(), 
                new PriceAlertAdapter.AlertClickListener() {
                    @Override
                    public void onAlertClick(PriceAlert alert) {
                        openProductDetails(alert.getProductId());
                    }

                    @Override
                    public void onDeleteClick(PriceAlert alert) {
                        deleteAlert(alert);
                    }

                    @Override
                    public void onToggleClick(PriceAlert alert, boolean isActive) {
                        toggleAlert(alert, isActive);
                    }

                    @Override
                    public void onTestDropClick(PriceAlert alert) {
                        simulatePriceDrop(alert);
                    }
                });

        binding.rvAlerts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAlerts.setAdapter(alertAdapter);
    }

    private void setupClickListeners() {
        binding.btnCreateAlert.setOnClickListener(v -> {
            finish();
        });
    }

    private void openProductDetails(String productId) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    private void simulatePriceDrop(PriceAlert alert) {
        // FIXED: Set current price exactly to the Target Price for accurate testing
        double newPrice = alert.getTargetPrice();
        Map<String, Object> updates = new HashMap<>();
        updates.put("currentPrice", newPrice);
        updates.put("notified", true);
        updates.put("triggeredAt", System.currentTimeMillis());

        firebaseManager.updateAlert(alert.getAlertId(), updates, task -> {
            if (task.isSuccessful()) {
                NotificationHelper.showPriceAlertNotification(this, 
                        alert.getProductName(), 
                        alert.getTargetPrice(), 
                        newPrice);
                Toast.makeText(this, "AI: Target price reached!", Toast.LENGTH_SHORT).show();
                loadAlerts();
            }
        });
    }

    private void loadAlerts() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        firebaseManager.getUserAlerts(userId, task -> {
            if (binding == null) return;
            binding.progressBar.setVisibility(View.GONE);
            
            if (task.isSuccessful() && task.getResult() != null) {
                List<PriceAlert> alerts = task.getResult().toObjects(PriceAlert.class);
                
                if (alerts.isEmpty()) {
                    showEmptyState();
                } else {
                    showAlerts(alerts);
                }
            } else {
                showEmptyState();
            }
        });
    }

    private void showAlerts(List<PriceAlert> alerts) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.rvAlerts.setVisibility(View.VISIBLE);
        alertAdapter.updateAlerts(alerts);
    }

    private void showEmptyState() {
        binding.rvAlerts.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void deleteAlert(PriceAlert alert) {
        firebaseManager.deleteAlert(alert.getAlertId(), task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Alert deleted", Toast.LENGTH_SHORT).show();
                loadAlerts();
            }
        });
    }

    private void toggleAlert(PriceAlert alert, boolean isActive) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("active", isActive);
        firebaseManager.updateAlert(alert.getAlertId(), updates, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
