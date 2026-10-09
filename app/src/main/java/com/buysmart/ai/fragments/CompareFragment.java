package com.buysmart.ai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.activities.CartActivity;
import com.buysmart.ai.adapters.CartItemAdapter;
import com.buysmart.ai.databinding.FragmentCompareBinding;
import com.buysmart.ai.models.CartItem;
import com.buysmart.ai.models.Coupon;
import com.buysmart.ai.utils.CartManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompareFragment extends Fragment {

    private FragmentCompareBinding binding;
    private CartManager cartManager;
    private CartItemAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCompareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartManager = new CartManager(requireContext());

        setupRecyclerView();
        setupClickListeners();

        loadCartItems();
    }

    private void setupRecyclerView() {
        cartAdapter = new CartItemAdapter(new ArrayList<>(), 
                new CartItemAdapter.CartItemListener() {
                    @Override
                    public void onQuantityChanged(CartItem item, int quantity) {
                        cartManager.updateQuantity(item.getProductId(), item.getPlatform(), quantity);
                        updateUI();
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

        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCartItems.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        binding.btnCheckout.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CartActivity.class));
        });

        binding.btnContinueShopping.setOnClickListener(v -> {
            if (getActivity() instanceof com.buysmart.ai.activities.MainActivity) {
                ((com.buysmart.ai.activities.MainActivity) getActivity()).navigateToTab(R.id.nav_home);
            }
        });
    }

    private void loadCartItems() {
        if (binding == null) return;
        
        List<CartItem> items = cartManager.getCartItems();
        
        if (items == null || items.isEmpty()) {
            showEmptyState();
        } else {
            showCartItems(items);
        }
    }

    private void showCartItems(List<CartItem> items) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutCartContent.setVisibility(View.VISIBLE);
        cartAdapter.updateItems(items);
        updateUI();
    }

    private void showEmptyState() {
        binding.layoutCartContent.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        if (binding == null) return;

        Map<String, Double> platformTotals = cartManager.getPlatformTotals();
        String cheapestPlatform = cartManager.getCheapestPlatform();
        
        double subTotal = cartManager.getCartTotal();
        double savings = cartManager.getTotalSavings();
        int itemCount = cartManager.getCartCount();

        // --- AI OPTIMIZED CHECKOUT PRICE LOGIC ---
        if (cheapestPlatform != null && !platformTotals.isEmpty()) {
            double cheapestTotal = platformTotals.get(cheapestPlatform);
            
            binding.cardSmartOptimizer.setVisibility(View.VISIBLE);
            binding.tvCheapestPlatform.setText(cheapestPlatform);
            
            // Apply Best Coupon for the best platform
            Coupon bestCoupon = cartManager.findBestCoupon(cheapestPlatform);
            double finalPrice = cheapestTotal;
            double couponDiscount = 0;

            if (bestCoupon != null) {
                couponDiscount = bestCoupon.calculateDiscount(cheapestTotal);
                finalPrice = cheapestTotal - couponDiscount;
                
                binding.tvPotentialSavings.setVisibility(View.VISIBLE);
                binding.tvPotentialSavings.setText(
                        String.format("✨ AI Applied: %s (-₹%.0f)\nCheapest price found on %s", 
                                bestCoupon.getCode(), couponDiscount, cheapestPlatform));
                binding.tvPotentialSavings.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary));
            } else {
                binding.tvPotentialSavings.setText(String.format("Cheapest price found on %s", cheapestPlatform));
                binding.tvPotentialSavings.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
                binding.tvPotentialSavings.setVisibility(View.VISIBLE);
            }

            binding.tvCheapestTotal.setText(String.format("₹%.2f", finalPrice));
            
            // CRITICAL FIX: Update the main checkout total to reflect the AI-optimized price
            binding.tvCartTotal.setText(String.format("₹%.2f", finalPrice));
            binding.tvTotalSavings.setText(String.format("You save ₹%.2f (incl. Coupon)", savings + couponDiscount));
            binding.tvTotalSavings.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary));

        } else {
            binding.cardSmartOptimizer.setVisibility(View.GONE);
            binding.tvCartTotal.setText(String.format("₹%.2f", subTotal));
            binding.tvTotalSavings.setText(String.format("You save ₹%.2f", savings));
            binding.tvTotalSavings.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
        }

        binding.tvItemCount.setText(String.format("%d items", itemCount));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
