package com.buysmart.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.adapters.CartItemAdapter;
import com.buysmart.ai.databinding.ActivityCartBinding;
import com.buysmart.ai.models.CartItem;
import com.buysmart.ai.models.Coupon;
import com.buysmart.ai.utils.CartManager;

import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartManager cartManager;
    private CartItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = new CartManager(this);

        setupToolbar();
        setupRecyclerView();
        setupClickListeners();

        loadCartItems();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new CartItemAdapter(cartManager.getCartItems(), 
                new CartItemAdapter.CartItemListener() {
                    @Override
                    public void onQuantityChanged(CartItem item, int quantity) {
                        cartManager.updateQuantity(item.getProductId(), item.getPlatform(), quantity);
                        updateCartUI();
                    }

                    @Override
                    public void onRemoveClick(CartItem item) {
                        cartManager.removeFromCart(item.getProductId(), item.getPlatform());
                        loadCartItems();
                    }

                    @Override
                    public void onItemClick(CartItem item) {
                        // Open product details if needed
                    }
                });

        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnCheckout.setOnClickListener(v -> {
            if (cartManager.getCartCount() > 0) {
                // Launch Checkout Activity instead of just a Toast
                Intent intent = new Intent(this, CheckoutActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnContinueShopping.setOnClickListener(v -> finish());

        binding.btnClearCart.setOnClickListener(v -> {
            cartManager.clearCart();
            loadCartItems();
        });
    }

    private void loadCartItems() {
        List<CartItem> items = cartManager.getCartItems();
        if (items.isEmpty()) {
            showEmptyState();
        } else {
            showCartItems(items);
        }
    }

    private void showCartItems(List<CartItem> items) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutCartContent.setVisibility(View.VISIBLE);
        adapter.updateItems(items);
        updateCartUI();
    }

    private void showEmptyState() {
        binding.layoutCartContent.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void updateCartUI() {
        if (binding == null) return;

        Map<String, Double> platformTotals = cartManager.getPlatformTotals();
        String cheapestPlatform = cartManager.getCheapestPlatform();

        double total = cartManager.getCartTotal();
        double savings = cartManager.getTotalSavings();
        
        if (cheapestPlatform != null) {
            double cheapestTotal = platformTotals.get(cheapestPlatform);
            Coupon bestCoupon = cartManager.findBestCoupon(cheapestPlatform);
            
            binding.cardSmartOptimizer.setVisibility(View.VISIBLE);
            binding.tvCheapestPlatform.setText(cheapestPlatform);

            if (bestCoupon != null) {
                double discount = bestCoupon.calculateDiscount(cheapestTotal);
                double finalPrice = cheapestTotal - discount;
                
                binding.tvCartTotal.setText(String.format("₹%.2f", finalPrice));
                binding.tvTotalSavings.setText(String.format("Total Savings: ₹%.2f", savings + discount));
                binding.tvTotalSavings.setTextColor(ContextCompat.getColor(this, R.color.secondary));
                
                binding.tvPotentialSavings.setText(String.format("✨ AI Applied: %s (-₹%.0f)", bestCoupon.getCode(), discount));
                binding.tvPotentialSavings.setVisibility(View.VISIBLE);
                
                binding.tvCheapestTotal.setText(String.format("₹%.2f", finalPrice));
            } else {
                binding.tvCartTotal.setText(String.format("₹%.2f", cheapestTotal));
                binding.tvTotalSavings.setText(String.format("You save ₹%.2f", savings));
                binding.tvPotentialSavings.setVisibility(View.GONE);
                binding.tvCheapestTotal.setText(String.format("₹%.2f", cheapestTotal));
            }
        } else {
            binding.cardSmartOptimizer.setVisibility(View.GONE);
            binding.tvCartTotal.setText(String.format("₹%.2f", total));
        }

        binding.tvItemCount.setText(String.format("%d items", cartManager.getCartCount()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
