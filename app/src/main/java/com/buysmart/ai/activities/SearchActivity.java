package com.buysmart.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.adapters.ProductAdapter;
import com.buysmart.ai.databinding.ActivitySearchBinding;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.utils.PriceComparisonAPI;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ProductAdapter productAdapter;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        category = getIntent().getStringExtra("category");

        setupToolbar();
        setupRecyclerView();
        setupFilters();

        loadProducts();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        if (category != null) {
            binding.toolbar.setTitle(category);
        }
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>(), 
                new ProductAdapter.ProductClickListener() {
                    @Override
                    public void onProductClick(Product product) {
                        openProductDetails(product);
                    }

                    @Override
                    public void onAddToCartClick(Product product) {
                        // Add to cart
                    }

                    @Override
                    public void onCompareClick(Product product) {
                        openPriceComparison(product);
                    }
                });

        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void setupFilters() {
        binding.btnFilter.setOnClickListener(v -> showFilterDialog());
        binding.btnSort.setOnClickListener(v -> showSortDialog());
    }

    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        // Mock products for demo
        List<Product> products = PriceComparisonAPI.getMockProducts();
        
        binding.progressBar.setVisibility(View.GONE);
        
        if (products.isEmpty()) {
            showEmptyState();
        } else {
            showProducts(products);
        }
    }

    private void showProducts(List<Product> products) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.rvProducts.setVisibility(View.VISIBLE);
        productAdapter.updateProducts(products);
    }

    private void showEmptyState() {
        binding.rvProducts.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void showFilterDialog() {
        // Show filter bottom sheet
    }

    private void showSortDialog() {
        // Show sort bottom sheet
    }

    private void openProductDetails(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void openPriceComparison(Product product) {
        Intent intent = new Intent(this, PriceComparisonActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
