package com.buysmart.ai.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.adapters.ReviewAdapter;
import com.buysmart.ai.databinding.ActivityProductDetailsBinding;
import com.buysmart.ai.databinding.DialogSetPriceAlertBinding;
import com.buysmart.ai.models.BuyRecommendation;
import com.buysmart.ai.models.PlatformPrice;
import com.buysmart.ai.models.PriceAlert;
import com.buysmart.ai.models.PriceHistory;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.models.User;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.DummyData;
import com.buysmart.ai.utils.FirebaseManager;
import com.buysmart.ai.utils.SessionManager;
import com.google.android.gms.tasks.Task;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private ActivityProductDetailsBinding binding;
    private Product product;
    private CartManager cartManager;
    private FirebaseManager firebaseManager;
    private SessionManager sessionManager;
    private boolean isProductSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = new CartManager(this);
        firebaseManager = FirebaseManager.getInstance();
        sessionManager = new SessionManager(this);

        product = getIntent().getParcelableExtra("product");
        if (product == null) {
            String productId = getIntent().getStringExtra("productId");
            if (productId != null) loadProduct(productId);
            else { finish(); return; }
        } else {
            checkIfSaved();
            displayProductDetails();
            setupPriceChart();
            setupSmartRecommendation();
            setupReviews();
        }

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProduct(String productId) {
        firebaseManager.getProduct(productId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                product = task.getResult().toObject(Product.class);
                if (product != null) {
                    checkIfSaved();
                    displayProductDetails();
                    setupPriceChart();
                    setupSmartRecommendation();
                    setupReviews();
                }
            }
        });
    }

    private void checkIfSaved() {
        User user = sessionManager.getUser();
        if (user != null && user.getSavedProducts() != null) {
            isProductSaved = user.getSavedProducts().contains(product.getProductId());
            updateSaveButtonUI();
        }
    }

    private void updateSaveButtonUI() {
        if (isProductSaved) {
            binding.btnSave.setText(R.string.saved);
            binding.btnSave.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
            binding.btnSave.setIconTintResource(R.color.error);
        } else {
            binding.btnSave.setText(R.string.save_for_later);
            binding.btnSave.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border));
            binding.btnSave.setIconTintResource(R.color.primary);
        }
    }

    private void toggleSaveProduct() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Login to save products", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isProductSaved) {
            firebaseManager.removeSavedProduct(userId, product.getProductId(), (Task<Void> task) -> {
                if (task.isSuccessful()) {
                    isProductSaved = false;
                    updateUserSession();
                    updateSaveButtonUI();
                    Toast.makeText(this, R.string.removed, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            firebaseManager.saveProduct(userId, product.getProductId(), (Task<Void> task) -> {
                if (task.isSuccessful()) {
                    isProductSaved = true;
                    updateUserSession();
                    updateSaveButtonUI();
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUserSession() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;
        firebaseManager.getUser(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                User user = task.getResult().toObject(User.class);
                if (user != null) sessionManager.updateUser(user);
            }
        });
    }

    private void displayProductDetails() {
        if (product == null) return;
        binding.tvProductName.setText(product.getName());
        binding.tvProductBrand.setText(product.getBrand());
        binding.tvDescription.setText(product.getDescription());
        binding.tvSpecifications.setText(product.getSpecifications());
        binding.tvRating.setText(String.format(Locale.getDefault(), "%.1f", product.getRating()));
        binding.tvReviewCount.setText(String.format(Locale.getDefault(), "(%d reviews)", product.getReviewCount()));

        binding.tvLowestPrice.setText(String.format(Locale.getDefault(), "₹%,.0f", product.getLowestPrice()));
        if (product.getHighestPrice() > product.getLowestPrice()) {
            binding.tvHighestPrice.setText(String.format(Locale.getDefault(), "₹%,.0f", product.getHighestPrice()));
            binding.tvHighestPrice.setPaintFlags(binding.tvHighestPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvHighestPrice.setVisibility(View.VISIBLE);
        }

        double valueScore = product.calculateValueScore();
        binding.tvValueScore.setText(String.format(Locale.getDefault(), "AI Value Score: %.0f/100", valueScore));
        binding.progressValueScore.setProgress((int) valueScore);

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(this).load(product.getImages().get(0)).placeholder(R.drawable.ic_app_logo).into(binding.ivProductImage);
        }
        
        PlatformPrice bestPrice = product.getBestPricePlatform();
        if (bestPrice != null) {
            binding.tvBestPricePlatform.setText(String.format("Best price on %s", bestPrice.getPlatform()));
        }
    }

    private void setupClickListeners() {
        binding.btnComparePrices.setOnClickListener(v -> {
            Intent intent = new Intent(this, PriceComparisonActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });

        binding.btnAddToCart.setOnClickListener(v -> {
            if (product != null && product.getBestPricePlatform() != null) {
                cartManager.addToCart(product, product.getBestPricePlatform());
                Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSave.setOnClickListener(v -> toggleSaveProduct());
        binding.btnShare.setOnClickListener(v -> shareProduct());
        binding.btnSetAlert.setOnClickListener(v -> showPriceAlertDialog());
        binding.chartPriceHistory.setOnClickListener(v -> openFullPriceHistory());
        
        binding.btnBuyNow.setOnClickListener(v -> {
            PlatformPrice best = product.getBestPricePlatform();
            if (best != null && best.getProductUrl() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(best.getProductUrl()));
                startActivity(intent);
            }
        });
    }

    private void openFullPriceHistory() {
        Intent intent = new Intent(this, PriceHistoryActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void setupPriceChart() {
        if (product == null) return;
        PriceHistory history = DummyData.getPriceHistory(product.getProductId(), "Amazon");
        List<PriceHistory.PricePoint> points = history.getPricePoints();
        if (points == null || points.isEmpty()) return;

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            entries.add(new Entry(i, (float) points.get(i).getPrice()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Price");
        int accentColor = ContextCompat.getColor(this, R.color.accent);
        dataSet.setColor(accentColor);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(accentColor);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(150);

        LineData lineData = new LineData(dataSet);
        binding.chartPriceHistory.setData(lineData);
        binding.chartPriceHistory.getDescription().setEnabled(false);
        binding.chartPriceHistory.getLegend().setEnabled(false);
        binding.chartPriceHistory.getXAxis().setEnabled(false);
        binding.chartPriceHistory.getAxisLeft().setEnabled(false);
        binding.chartPriceHistory.getAxisRight().setEnabled(false);
        binding.chartPriceHistory.animateY(1000);
        binding.chartPriceHistory.invalidate();
    }

    private void setupSmartRecommendation() {
        if (product == null) return;
        PriceHistory history = DummyData.getPriceHistory(product.getProductId(), "Amazon");
        BuyRecommendation recommendation = BuyRecommendation.analyze(product, history);
        binding.tvRecommendationMessage.setText(recommendation.getMessage());
        binding.tvRecommendationReasoning.setText(recommendation.getReasoning());
        binding.tvConfidenceScore.setText(String.format(Locale.getDefault(), "AI Confidence: %.0f%%", recommendation.getConfidenceScore() * 100));
        
        int bgResId;
        switch (recommendation.getType()) {
            case BUY_NOW: bgResId = R.drawable.bg_recommendation_buy; break;
            case WAIT: bgResId = R.drawable.bg_recommendation_wait; break;
            default: bgResId = R.drawable.bg_recommendation_likely; break;
        }
        binding.cardRecommendation.setBackgroundResource(bgResId);
    }

    private void setupReviews() {
        if (product == null) return;
        ReviewAdapter reviewAdapter = new ReviewAdapter(DummyData.getMockReviews());
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(reviewAdapter);
    }

    private void shareProduct() {
        if (product == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, product.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + product.getName() + " on BuySmart AI!");
        startActivity(Intent.createChooser(shareIntent, "Share Product"));
    }

    private void showPriceAlertDialog() {
        if (product == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogSetPriceAlertBinding dialogBinding = DialogSetPriceAlertBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        dialogBinding.tvProductName.setText(product.getName());
        dialogBinding.tvCurrentPrice.setText(String.format(Locale.getDefault(), "₹%,.0f", product.getLowestPrice()));
        dialogBinding.etTargetPrice.setText(String.valueOf((int)(product.getLowestPrice() * 0.9)));
        AlertDialog dialog = builder.create();
        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnCreate.setOnClickListener(v -> {
            String targetPriceStr = dialogBinding.etTargetPrice.getText().toString();
            if (!targetPriceStr.isEmpty()) {
                createPriceAlert(Double.parseDouble(targetPriceStr));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createPriceAlert(double targetPrice) {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) return;
        PriceAlert alert = new PriceAlert(userId, product.getProductId(), targetPrice);
        alert.setProductName(product.getName());
        alert.setCurrentPrice(product.getLowestPrice());
        alert.setProductImage(product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0) : null);
        firebaseManager.createPriceAlert(alert, task -> {
            if (task.isSuccessful()) Toast.makeText(this, R.string.alert_created, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
