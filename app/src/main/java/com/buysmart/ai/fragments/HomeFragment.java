package com.buysmart.ai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.activities.MainActivity;
import com.buysmart.ai.activities.ProductDetailsActivity;
import com.buysmart.ai.activities.QuickDeliveryActivity;
import com.buysmart.ai.activities.SmartScanActivity;
import com.buysmart.ai.adapters.CategoryAdapter;
import com.buysmart.ai.adapters.ProductAdapter;
import com.buysmart.ai.databinding.FragmentHomeBinding;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.DummyData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductAdapter recommendedAdapter;
    private ProductAdapter trendingAdapter;
    private CategoryAdapter categoryAdapter;
    private CartManager cartManager;

    private final String[] categories = {
            "All", "Electronics", "Fashion", "Home & Living", "Beauty", 
            "Sports", "Books", "Toys", "Groceries"
    };

    private final int[] categoryIcons = {
            R.drawable.ic_search, R.drawable.ic_electronics, R.drawable.ic_fashion, R.drawable.ic_home_living,
            R.drawable.ic_beauty, R.drawable.ic_sports, R.drawable.ic_books,
            R.drawable.ic_toys, R.drawable.ic_groceries
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(binding.searchBarWrapper, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), insets.top, v.getPaddingRight(), v.getPaddingBottom());
            return WindowInsetsCompat.CONSUMED;
        });

        cartManager = new CartManager(requireContext());

        setupAIGreeting();
        setupSearchBar();
        setupBentoClickListeners();
        setupCategories();
        setupEditorialSections();
        setupScrollBehavior();

        loadData();
    }

    private void setupAIGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (timeOfDay < 12) greeting = "Good Morning.";
        else if (timeOfDay < 16) greeting = "Good Afternoon.";
        else if (timeOfDay < 21) greeting = "Good Evening.";
        else greeting = "Good Night.";

        binding.tvGreeting.setText(greeting);
        binding.tvAiInsight.setText("Analyzing 2,500+ daily offers for you.");
        
        binding.tvGreeting.setAlpha(0f);
        binding.tvGreeting.animate().alpha(1f).setDuration(800).start();
    }

    private void setupSearchBar() {
        binding.searchBarContainer.setOnClickListener(v -> navigateToSearch(null));
        binding.btnVoiceSearch.setOnClickListener(v -> startVoiceSearch());
        
        binding.btnCart.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_compare);
            }
        });
        
        binding.btnNotifications.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_alerts);
            }
        });
    }

    private void setupBentoClickListeners() {
        // Zero-Input Shopping: Smart Scan Activity
        binding.bentoSmartScan.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), SmartScanActivity.class)));
            
        // SwiftDash: 10-Min Delivery Enabled
        binding.bentoQuickDelivery.setOnClickListener(v -> 
            startActivity(new Intent(requireContext(), QuickDeliveryActivity.class)));
    }

    private void setupCategories() {
        categoryAdapter = new CategoryAdapter(categories, categoryIcons, 
                category -> navigateToSearch(category));

        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupEditorialSections() {
        recommendedAdapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.ProductClickListener() {
            @Override
            public void onProductClick(Product product) { openProductDetails(product); }
            @Override
            public void onAddToCartClick(Product product) { addToCart(product); }
            @Override
            public void onCompareClick(Product product) { }
        });

        binding.rvRecommendations.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvRecommendations.setAdapter(recommendedAdapter);

        trendingAdapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.ProductClickListener() {
            @Override
            public void onProductClick(Product product) { openProductDetails(product); }
            @Override
            public void onAddToCartClick(Product product) { addToCart(product); }
            @Override
            public void onCompareClick(Product product) {}
        });

        binding.rvTrendingDeals.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrendingDeals.setAdapter(trendingAdapter);
    }

    private void setupScrollBehavior() {
        binding.homeScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldX, oldY) -> {
            float translationY = Math.max(0, scrollY);
            binding.animatedBg.setTranslationY(-translationY * 0.4f);
            
            float alpha = Math.min(1f, (float) scrollY / 300f);
            binding.searchBarContainer.setAlpha(0.7f + (alpha * 0.3f));
            binding.searchBarContainer.setElevation(alpha * 12f);
        });
    }

    private void loadData() {
        List<Product> allProducts = DummyData.getAllProducts();
        if (allProducts != null && !allProducts.isEmpty()) {
            recommendedAdapter.updateProducts(allProducts.subList(0, Math.min(10, allProducts.size())));
            trendingAdapter.updateProducts(allProducts.subList(Math.max(0, allProducts.size() - 8), allProducts.size()));
        }
    }

    private void navigateToSearch(String category) {
        if (getActivity() instanceof MainActivity) {
            Bundle args = new Bundle();
            if (category != null) args.putString("category", category);
            ((MainActivity) getActivity()).navigateToTab(R.id.nav_search, args);
        }
    }

    private void startVoiceSearch() {
        Toast.makeText(requireContext(), "Voice Assistant: listening...", Toast.LENGTH_SHORT).show();
    }

    private void openProductDetails(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void addToCart(Product product) {
        if (product.getBestPricePlatform() != null) {
            cartManager.addToCart(product, product.getBestPricePlatform());
            Toast.makeText(requireContext(), "Added to comparison", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
