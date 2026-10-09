package com.buysmart.ai.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.adapters.CouponAdapter;
import com.buysmart.ai.databinding.ActivityCouponsBinding;
import com.buysmart.ai.models.Coupon;
import com.buysmart.ai.utils.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class CouponsActivity extends AppCompatActivity {

    private ActivityCouponsBinding binding;
    private CouponAdapter couponAdapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCouponsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();

        setupToolbar();
        setupRecyclerView();
        setupFilters();

        loadCoupons();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        couponAdapter = new CouponAdapter(new ArrayList<>(), new CouponAdapter.CouponClickListener() {
            @Override
            public void onCopyClick(Coupon coupon) {
                copyCouponCode(coupon.getCode());
            }

            @Override
            public void onApplyClick(Coupon coupon) {
                applyCoupon(coupon);
            }
        });

        binding.rvCoupons.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCoupons.setAdapter(couponAdapter);
    }

    private void setupFilters() {
        // Setup platform filter chips
    }

    private void loadCoupons() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        // Mock coupons for demo
        List<Coupon> coupons = getMockCoupons();
        binding.progressBar.setVisibility(View.GONE);
        
        if (coupons.isEmpty()) {
            showEmptyState();
        } else {
            showCoupons(coupons);
        }
    }

    private void showCoupons(List<Coupon> coupons) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.rvCoupons.setVisibility(View.VISIBLE);
        couponAdapter.updateCoupons(coupons);
    }

    private void showEmptyState() {
        binding.rvCoupons.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void copyCouponCode(String code) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Coupon Code", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
    }

    private void applyCoupon(Coupon coupon) {
        Toast.makeText(this, "Coupon " + coupon.getCode() + " applied!", Toast.LENGTH_SHORT).show();
    }

    private List<Coupon> getMockCoupons() {
        List<Coupon> coupons = new ArrayList<>();

        Coupon c1 = new Coupon();
        c1.setCouponId("c1");
        c1.setCode("SAVE10");
        c1.setDescription("Get 10% off on all electronics");
        c1.setPlatform("Amazon");
        c1.setDiscountPercentage(10);
        c1.setMinimumOrder(1000);
        c1.setMaximumDiscount(500);
        c1.setSuccessRate(95);
        c1.setVerified(true);
        c1.setValidFrom(System.currentTimeMillis() - 86400000);
        c1.setValidUntil(System.currentTimeMillis() + 86400000 * 30);
        coupons.add(c1);

        Coupon c2 = new Coupon();
        c2.setCouponId("c2");
        c2.setCode("FLAT500");
        c2.setDescription("Flat ₹500 off on orders above ₹5000");
        c2.setPlatform("Flipkart");
        c2.setDiscountAmount(500);
        c2.setMinimumOrder(5000);
        c2.setSuccessRate(88);
        c2.setVerified(true);
        c2.setValidFrom(System.currentTimeMillis() - 86400000);
        c2.setValidUntil(System.currentTimeMillis() + 86400000 * 15);
        coupons.add(c2);

        Coupon c3 = new Coupon();
        c3.setCouponId("c3");
        c3.setCode("FIRSTBUY");
        c3.setDescription("20% off on your first purchase");
        c3.setPlatform("Meesho");
        c3.setDiscountPercentage(20);
        c3.setMaximumDiscount(1000);
        c3.setSuccessRate(92);
        c3.setVerified(true);
        c3.setValidFrom(System.currentTimeMillis() - 86400000);
        c3.setValidUntil(System.currentTimeMillis() + 86400000 * 60);
        coupons.add(c3);

        return coupons;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
