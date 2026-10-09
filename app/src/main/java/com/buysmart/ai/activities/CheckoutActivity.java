package com.buysmart.ai.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.buysmart.ai.adapters.OrderBreakdownAdapter;
import com.buysmart.ai.databinding.ActivityCheckoutBinding;
import com.buysmart.ai.models.CartItem;
import com.buysmart.ai.models.Coupon;
import com.buysmart.ai.models.Order;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.FirebaseManager;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private CartManager cartManager;
    private FirebaseManager firebaseManager;
    private double totalCo2Saved = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = new CartManager(this);
        firebaseManager = FirebaseManager.getInstance();

        setupToolbar();
        setupOrderBreakdown();
        calculateOrderSummary();
        setupClickListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupOrderBreakdown() {
        List<CartItem> items = cartManager.getCartItems();
        OrderBreakdownAdapter adapter = new OrderBreakdownAdapter(items);
        binding.rvOrderBreakdown.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderBreakdown.setAdapter(adapter);
        
        // Calculate total CO2 saved from cart items (Impact Feature)
        // In a real app, this would come from the specific platform price object
        totalCo2Saved = items.size() * 45.0; // Mock calculation
        binding.tvTotalCo2.setText(String.format(Locale.getDefault(), "Saving %.0fg of CO2 with this eco-friendly order", totalCo2Saved));
    }

    private void calculateOrderSummary() {
        double itemsTotal = cartManager.getCartTotal();
        String cheapestPlatform = cartManager.getCheapestPlatform();
        double discount = 0;

        if (cheapestPlatform != null) {
            Coupon bestCoupon = cartManager.findBestCoupon(cheapestPlatform);
            if (bestCoupon != null) {
                discount = bestCoupon.calculateDiscount(itemsTotal);
            }
        }

        binding.tvGrandTotal.setText(String.format(Locale.getDefault(), "₹%,.2f", itemsTotal - discount));
    }

    private void setupClickListeners() {
        binding.btnPlaceOrder.setOnClickListener(v -> {
            if (validateForm()) {
                placeUnifiedOrder();
            }
        });
    }

    private void placeUnifiedOrder() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(cartManager.getCartItems());
        order.setTotalAmount(cartManager.getCartTotal()); // Simplified for demo
        order.setPlatform("BuySmart Unified");
        order.setAddress(binding.etAddress.getText().toString());
        order.setPaymentMethod("UPI");
        order.setCo2Saved(totalCo2Saved);
        order.setOrderDate(System.currentTimeMillis());

        firebaseManager.placeOrder(order, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Unified Order Placed! AI is handling your purchases.", Toast.LENGTH_LONG).show();
                cartManager.clearCart();
                finish();
            } else {
                Toast.makeText(this, "Proxy Checkout failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        if (binding.etName.getText().toString().trim().isEmpty()) {
            binding.etName.setError("Name required");
            return false;
        }
        if (binding.etAddress.getText().toString().trim().isEmpty()) {
            binding.etAddress.setError("Address required");
            return false;
        }
        return true;
    }
}
