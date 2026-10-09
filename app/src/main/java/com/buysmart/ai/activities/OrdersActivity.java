package com.buysmart.ai.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.adapters.OrderAdapter;
import com.buysmart.ai.databinding.ActivityOrdersBinding;
import com.buysmart.ai.models.Order;
import com.buysmart.ai.utils.FirebaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding binding;
    private OrderAdapter adapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();

        setupToolbar();
        setupRecyclerView();
        loadOrders();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter(new ArrayList<>());
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        firebaseManager.getUserOrders(userId, task -> {
            if (binding == null) return;
            binding.progressBar.setVisibility(View.GONE);

            if (task.isSuccessful() && task.getResult() != null) {
                List<Order> orders = task.getResult().toObjects(Order.class);
                if (orders == null || orders.isEmpty()) {
                    showEmptyState();
                } else {
                    // Locally sort by date (Newest First) to bypass Firestore Index requirement
                    Collections.sort(orders, (o1, o2) -> Long.compare(o2.getOrderDate(), o1.getOrderDate()));
                    showOrders(orders);
                }
            } else {
                showEmptyState();
                Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrders(List<Order> orders) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.rvOrders.setVisibility(View.VISIBLE);
        adapter.updateOrders(orders);
    }

    private void showEmptyState() {
        binding.rvOrders.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
