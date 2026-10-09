package com.buysmart.ai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.adapters.ProductAdapter;
import com.buysmart.ai.databinding.ActivitySavedProductsBinding;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.models.User;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.DummyData;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SavedProductsActivity extends AppCompatActivity {

    private ActivitySavedProductsBinding binding;
    private ProductAdapter productAdapter;
    private FirebaseManager firebaseManager;
    private SessionManager sessionManager;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        sessionManager = new SessionManager(this);
        cartManager = new CartManager(this);

        setupToolbar();
        setupRecyclerView();

        loadSavedProducts();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
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
                        if (product.getBestPricePlatform() != null) {
                            cartManager.addToCart(product, product.getBestPricePlatform());
                            Toast.makeText(SavedProductsActivity.this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCompareClick(Product product) {
                        openPriceComparison(product);
                    }
                });

        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void loadSavedProducts() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            showEmptyState();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        // Fetch fresh user data from Firebase to get latest saved products
        firebaseManager.getUser(userId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                binding.progressBar.setVisibility(View.GONE);
                showEmptyState();
                return;
            }

            User user = task.getResult().toObject(User.class);
            if (user == null || user.getSavedProducts() == null || user.getSavedProducts().isEmpty()) {
                binding.progressBar.setVisibility(View.GONE);
                showEmptyState();
                return;
            }

            // Sync with local session
            sessionManager.updateUser(user);

            List<String> savedIds = user.getSavedProducts();
            List<Product> allProducts = DummyData.getAllProducts();
            List<Product> savedProducts = new ArrayList<>();

            for (Product p : allProducts) {
                if (savedIds.contains(p.getProductId())) {
                    savedProducts.add(p);
                }
            }
            
            binding.progressBar.setVisibility(View.GONE);
            
            if (savedProducts.isEmpty()) {
                showEmptyState();
            } else {
                showProducts(savedProducts);
            }
        });
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
    protected void onResume() {
        super.onResume();
        loadSavedProducts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
