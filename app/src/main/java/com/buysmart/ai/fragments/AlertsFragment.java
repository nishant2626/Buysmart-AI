package com.buysmart.ai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.activities.MainActivity;
import com.buysmart.ai.activities.ProductDetailsActivity;
import com.buysmart.ai.adapters.PriceAlertAdapter;
import com.buysmart.ai.databinding.FragmentAlertsBinding;
import com.buysmart.ai.models.PriceAlert;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.NotificationHelper;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsFragment extends Fragment {

    private FragmentAlertsBinding binding;
    private PriceAlertAdapter alertAdapter;
    private FirebaseManager firebaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAlertsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseManager = FirebaseManager.getInstance();

        setupRecyclerView();
        setupClickListeners();

        loadAlerts();
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

        binding.rvAlerts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAlerts.setAdapter(alertAdapter);
    }

    private void setupClickListeners() {
        binding.btnCreateAlert.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_search);
            }
        });
    }

    private void simulatePriceDrop(PriceAlert alert) {
        double newPrice = alert.getTargetPrice();
        Map<String, Object> updates = new HashMap<>();
        updates.put("currentPrice", newPrice);
        updates.put("notified", true);
        updates.put("triggeredAt", System.currentTimeMillis());

        firebaseManager.updateAlert(alert.getAlertId(), updates, task -> {
            if (task.isSuccessful()) {
                NotificationHelper.showPriceAlertNotification(requireContext(), 
                        alert.getProductName(), 
                        alert.getTargetPrice(), 
                        newPrice);
                Toast.makeText(requireContext(), "AI: Target price reached!", Toast.LENGTH_SHORT).show();
                loadAlerts();
            }
        });
    }

    private void loadAlerts() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            showEmptyState();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        
        firebaseManager.getUserAlerts(userId, task -> {
            if (binding == null) return;
            binding.progressBar.setVisibility(View.GONE);
            
            if (task.isSuccessful() && task.getResult() != null) {
                List<PriceAlert> alerts = task.getResult().toObjects(PriceAlert.class);
                if (alerts == null || alerts.isEmpty()) {
                    showEmptyState();
                } else {
                    // Locally sort by date to bypass manual index requirement
                    Collections.sort(alerts, (a1, a2) -> Long.compare(a2.getCreatedAt(), a1.getCreatedAt()));
                    showAlerts(alerts);
                }
            } else {
                showEmptyState();
            }
        });
    }

    private void showAlerts(List<PriceAlert> alerts) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutAlerts.setVisibility(View.VISIBLE);
        binding.rvAlerts.setVisibility(View.VISIBLE);
        alertAdapter.updateAlerts(alerts);
    }

    private void showEmptyState() {
        binding.layoutAlerts.setVisibility(View.GONE);
        binding.rvAlerts.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void openProductDetails(String productId) {
        Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    private void deleteAlert(PriceAlert alert) {
        firebaseManager.deleteAlert(alert.getAlertId(), task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Alert deleted", Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();
        loadAlerts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
