package com.buysmart.ai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.activities.ProductDetailsActivity;
import com.buysmart.ai.adapters.ProductAdapter;
import com.buysmart.ai.databinding.BottomSheetFilterBinding;
import com.buysmart.ai.databinding.BottomSheetSortBinding;
import com.buysmart.ai.databinding.FragmentSearchBinding;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.DummyData;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final int VOICE_SEARCH_REQUEST = 100;

    private FragmentSearchBinding binding;
    private ProductAdapter productAdapter;
    private CartManager cartManager;
    private Handler searchHandler;
    private Runnable searchRunnable;
    
    private List<Product> masterList = new ArrayList<>();
    private List<Product> filteredList = new ArrayList<>();
    
    private String selectedCategory = "All";
    private float minPrice = 0;
    private float maxPrice = 200000;
    private float minRating = 0;
    private int sortType = 0; 
    
    private boolean isProgrammaticChange = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCategory = getArguments().getString("category", "All");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartManager = new CartManager(requireContext());
        searchHandler = new Handler(Looper.getMainLooper());

        setupSearchView();
        setupRecyclerView();
        setupFilters();
        setupRecentSearches();

        // APPLY CATEGORY FILTER FROM HOME
        if (selectedCategory != null && !selectedCategory.equals("All")) {
            isProgrammaticChange = true;
            binding.etSearch.setText(selectedCategory);
            isProgrammaticChange = false;
            performSearch(selectedCategory);
        } else {
            performSearch(""); 
        }
    }

    private void setupSearchView() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isProgrammaticChange) return;

                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                if (s.length() > 0) {
                    binding.btnClear.setVisibility(View.VISIBLE);
                    searchRunnable = () -> {
                        selectedCategory = "All"; // Reset only on manual typing
                        performSearch(s.toString());
                    };
                    searchHandler.postDelayed(searchRunnable, 500);
                } else {
                    binding.btnClear.setVisibility(View.GONE);
                    selectedCategory = "All";
                    performSearch("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnVoiceSearch.setOnClickListener(v -> startVoiceSearch());
        binding.btnClear.setOnClickListener(v -> {
            selectedCategory = "All";
            binding.etSearch.setText("");
        });
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
                            android.widget.Toast.makeText(requireContext(), "Added to cart", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCompareClick(Product product) {
                        openPriceComparison(product);
                    }
                });

        binding.rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void setupFilters() {
        binding.btnFilter.setOnClickListener(v -> showFilterDialog());
        binding.btnSort.setOnClickListener(v -> showSortDialog());
    }

    private void performSearch(String query) {
        if (binding == null) return;
        showLoading();

        List<Product> allProducts = DummyData.getAllProducts();
        masterList.clear();
        
        for (Product product : allProducts) {
            // Check Query (Matches Name, Brand, or Category)
            boolean matchesQuery = query.isEmpty() || 
                                  product.getName().toLowerCase().contains(query.toLowerCase()) ||
                                  product.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                                  product.getBrand().toLowerCase().contains(query.toLowerCase());
            
            // Check Strict Category Filter
            boolean matchesCategory = selectedCategory.equals("All") || 
                                     product.getCategory().equalsIgnoreCase(selectedCategory);

            if (matchesQuery && matchesCategory) {
                masterList.add(product);
            }
        }

        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        filteredList.clear();
        for (Product p : masterList) {
            boolean matchesPrice = p.getLowestPrice() >= minPrice && p.getLowestPrice() <= maxPrice;
            boolean matchesRating = p.getRating() >= minRating;

            if (matchesPrice && matchesRating) {
                filteredList.add(p);
            }
        }

        // Apply Sorting
        if (sortType == 1) { // Low to High
            Collections.sort(filteredList, (p1, p2) -> Double.compare(p1.getLowestPrice(), p2.getLowestPrice()));
        } else if (sortType == 2) { // High to Low
            Collections.sort(filteredList, (p1, p2) -> Double.compare(p2.getLowestPrice(), p1.getLowestPrice()));
        } else if (sortType == 3) { // Rating
            Collections.sort(filteredList, (p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));
        }

        if (binding != null) {
            hideLoading();
            if (filteredList.isEmpty()) {
                showNoResults();
            } else {
                showResults(filteredList);
            }
        }
    }

    private void showFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        BottomSheetFilterBinding filterBinding = BottomSheetFilterBinding.inflate(getLayoutInflater());
        dialog.setContentView(filterBinding.getRoot());

        filterBinding.sliderPrice.setValues(minPrice, maxPrice);
        filterBinding.tvMinPrice.setText("₹" + (int)minPrice);
        filterBinding.tvMaxPrice.setText("₹" + (int)maxPrice + (maxPrice >= 200000 ? "+" : ""));

        filterBinding.sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minPrice = values.get(0);
            maxPrice = values.get(1);
            filterBinding.tvMinPrice.setText("₹" + (int)minPrice);
            filterBinding.tvMaxPrice.setText("₹" + (int)maxPrice + (maxPrice >= 200000 ? "+" : ""));
        });

        String[] categories = {"All", "Electronics", "Fashion", "Home & Living", "Beauty", "Sports", "Books", "Toys", "Groceries"};
        for (String cat : categories) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_category_chip, filterBinding.cgCategories, false);
            chip.setText(cat);
            chip.setCheckable(true);
            chip.setChecked(cat.equalsIgnoreCase(selectedCategory));
            filterBinding.cgCategories.addView(chip);
        }

        if (minRating >= 4) filterBinding.chip4plus.setChecked(true);
        else if (minRating >= 3) filterBinding.chip3plus.setChecked(true);

        filterBinding.btnApplyFilter.setOnClickListener(v -> {
            int checkedId = filterBinding.cgCategories.getCheckedChipId();
            if (checkedId != View.NO_ID) {
                Chip selectedChip = filterBinding.cgCategories.findViewById(checkedId);
                selectedCategory = selectedChip.getText().toString();
            }

            if (filterBinding.chip4plus.isChecked()) minRating = 4.0f;
            else if (filterBinding.chip3plus.isChecked()) minRating = 3.0f;
            else minRating = 0;

            performSearch(binding.etSearch.getText().toString());
            dialog.dismiss();
        });

        filterBinding.btnClearFilter.setOnClickListener(v -> {
            minPrice = 0; maxPrice = 200000; minRating = 0; selectedCategory = "All";
            performSearch("");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showSortDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        BottomSheetSortBinding sortBinding = BottomSheetSortBinding.inflate(getLayoutInflater());
        dialog.setContentView(sortBinding.getRoot());

        if (sortType == 0) sortBinding.rbRelevance.setChecked(true);
        else if (sortType == 1) sortBinding.rbPriceLow.setChecked(true);
        else if (sortType == 2) sortBinding.rbPriceHigh.setChecked(true);
        else if (sortType == 3) sortBinding.rbRating.setChecked(true);

        sortBinding.btnApplySort.setOnClickListener(v -> {
            int checkedId = sortBinding.rgSort.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_relevance) sortType = 0;
            else if (checkedId == R.id.rb_price_low) sortType = 1;
            else if (checkedId == R.id.rb_price_high) sortType = 2;
            else if (checkedId == R.id.rb_rating) sortType = 3;

            applyFiltersAndSort();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showResults(List<Product> products) {
        binding.layoutRecentSearches.setVisibility(View.GONE);
        binding.layoutNoResults.setVisibility(View.GONE);
        binding.rvProducts.setVisibility(View.VISIBLE);
        productAdapter.updateProducts(products);
    }

    private void showNoResults() {
        binding.rvProducts.setVisibility(View.GONE);
        binding.layoutRecentSearches.setVisibility(View.GONE);
        binding.layoutNoResults.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvProducts.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void setupRecentSearches() {
        List<String> recentSearches = new ArrayList<>();
        recentSearches.add("iPhone 15");
        recentSearches.add("Samsung Galaxy");
        recentSearches.add("Headphones");
        recentSearches.add("Smart Watch");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, recentSearches);
        binding.listRecentSearches.setAdapter(adapter);

        binding.listRecentSearches.setOnItemClickListener((parent, view, position, id) -> {
            String query = recentSearches.get(position);
            binding.etSearch.setText(query);
            selectedCategory = "All";
            performSearch(query);
        });
    }

    private void startVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Search for products...");
        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST);
        } catch (Exception e) {}
    }

    private void openProductDetails(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void openPriceComparison(Product product) {
        Intent intent = new Intent(requireContext(), com.buysmart.ai.activities.PriceComparisonActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_SEARCH_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                binding.etSearch.setText(results.get(0));
                selectedCategory = "All";
                performSearch(results.get(0));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
        binding = null;
    }
}
