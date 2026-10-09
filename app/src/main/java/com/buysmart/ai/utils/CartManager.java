package com.buysmart.ai.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.buysmart.ai.models.CartItem;
import com.buysmart.ai.models.Coupon;
import com.buysmart.ai.models.PlatformPrice;
import com.buysmart.ai.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    private static final String PREF_NAME = "BuySmartCart";
    private static final String KEY_CART_ITEMS = "cartItems";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<CartItem> cartItems;

    public CartManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
        loadCart();
    }

    private void loadCart() {
        String json = pref.getString(KEY_CART_ITEMS, null);
        if (json != null) {
            Type type = new TypeToken<List<CartItem>>() {}.getType();
            cartItems = gson.fromJson(json, type);
        }
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCart() {
        String json = gson.toJson(cartItems);
        editor.putString(KEY_CART_ITEMS, json);
        editor.apply();
    }

    public void addToCart(Product product, PlatformPrice platformPrice) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(product.getProductId()) 
                    && item.getPlatform().equals(platformPrice.getPlatform())) {
                item.setQuantity(item.getQuantity() + 1);
                saveCart();
                return;
            }
        }

        CartItem newItem = new CartItem(product, platformPrice);
        cartItems.add(newItem);
        saveCart();
    }

    public void removeFromCart(String productId, String platform) {
        cartItems.removeIf(item -> item.getProductId().equals(productId) 
                && item.getPlatform().equals(platform));
        saveCart();
    }

    public void updateQuantity(String productId, String platform, int quantity) {
        if (quantity <= 0) {
            removeFromCart(productId, platform);
            return;
        }

        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId) 
                    && item.getPlatform().equals(platform)) {
                item.setQuantity(quantity);
                saveCart();
                return;
            }
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getCartCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public double getTotalSavings() {
        double savings = 0;
        for (CartItem item : cartItems) {
            savings += item.getTotalSavings();
        }
        return savings;
    }

    // ==================== AUTO COUPON LOGIC ====================

    public Coupon findBestCoupon(String platform) {
        List<Coupon> allCoupons = DummyData.getAllCoupons();
        double orderTotal = getPlatformTotal(platform);
        Coupon bestCoupon = null;
        double maxDiscount = 0;

        for (Coupon coupon : allCoupons) {
            if (coupon.getPlatform().equalsIgnoreCase(platform) && coupon.isVerified()) {
                double discount = coupon.calculateDiscount(orderTotal);
                if (discount > maxDiscount) {
                    maxDiscount = discount;
                    bestCoupon = coupon;
                }
            }
        }
        return bestCoupon;
    }

    public double getPlatformTotal(String platform) {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.getPlatform().equalsIgnoreCase(platform)) {
                total += item.getTotalPrice();
            }
        }
        return total;
    }

    public Map<String, Double> getPlatformTotals() {
        Map<String, Double> totals = new HashMap<>();
        for (CartItem item : cartItems) {
            String p = item.getPlatform();
            totals.put(p, totals.getOrDefault(p, 0.0) + item.getTotalPrice());
        }
        return totals;
    }

    public String getCheapestPlatform() {
        Map<String, Double> totals = getPlatformTotals();
        String cheapest = null;
        double min = Double.MAX_VALUE;
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            if (entry.getValue() < min) {
                min = entry.getValue();
                cheapest = entry.getKey();
            }
        }
        return cheapest;
    }

    public void clearCart() {
        cartItems.clear();
        saveCart();
    }
}
