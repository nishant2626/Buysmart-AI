package com.buysmart.ai.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.adapters.PlatformPriceAdapter;
import com.buysmart.ai.databinding.ActivityPriceComparisonBinding;
import com.buysmart.ai.models.CartItem;
import com.buysmart.ai.models.PlatformPrice;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.utils.CartManager;

import java.util.Map;

public class PriceComparisonActivity extends AppCompatActivity {

    private ActivityPriceComparisonBinding binding;
    private Product product;
    private CartManager cartManager;
    private PlatformPriceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPriceComparisonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = new CartManager(this);

        product = getIntent().getParcelableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        setupToolbar();
        displayProductInfo();
        setupPriceComparison();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayProductInfo() {
        binding.tvProductName.setText(product.getName());
        binding.tvProductBrand.setText(product.getBrand());
        binding.tvRating.setText(String.format("%.1f", product.getRating()));
        binding.tvReviewCount.setText(String.format("(%d)", product.getReviewCount()));

        // Load image
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(this)
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.ic_home)
                    .into(binding.ivProductImage);
        }
    }

    private void setupPriceComparison() {
        Map<String, PlatformPrice> platformPrices = product.getPlatformPrices();
        if (platformPrices == null || platformPrices.isEmpty()) {
            binding.rvPlatformPrices.setVisibility(View.GONE);
            binding.tvNoPrices.setVisibility(View.VISIBLE);
            return;
        }

        PlatformPrice bestPrice = product.getBestPricePlatform();
        PlatformPrice fastestDelivery = product.getFastestDeliveryPlatform();

        String bestPricePlatform = bestPrice != null ? bestPrice.getPlatform() : "";
        String fastestPlatform = fastestDelivery != null ? fastestDelivery.getPlatform() : "";

        adapter = new PlatformPriceAdapter(platformPrices, bestPricePlatform, fastestPlatform,
                price -> {
                    // Buy click - add to cart
                    cartManager.addToCart(product, price);
                    Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                    
                    // Open product URL
                    if (price.getProductUrl() != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(price.getProductUrl()));
                        startActivity(intent);
                    }
                });

        binding.rvPlatformPrices.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPlatformPrices.setAdapter(adapter);

        // Summary
        binding.tvBestPrice.setText(String.format("₹%.2f", bestPrice != null ? bestPrice.getPrice() : 0));
        binding.tvBestPricePlatform.setText(bestPricePlatform);
        binding.tvFastestDelivery.setText(fastestDelivery != null ? fastestDelivery.getFormattedDelivery() : "N/A");
        binding.tvFastestPlatform.setText(fastestPlatform);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
