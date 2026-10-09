package com.buysmart.ai.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ItemProductBinding;
import com.buysmart.ai.models.PriceHistory;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.utils.DummyData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private ProductClickListener listener;

    public interface ProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
        void onCompareClick(Product product);
    }

    public ProductAdapter(List<Product> products, ProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            binding.tvProductName.setText(product.getName());
            binding.tvProductBrand.setText(product.getBrand());
            binding.tvPrice.setText(String.format("₹%,.0f", product.getLowestPrice()));
            
            if (product.getHighestPrice() > product.getLowestPrice()) {
                binding.tvOriginalPrice.setText(String.format("₹%,.0f", product.getHighestPrice()));
                binding.tvOriginalPrice.setVisibility(View.VISIBLE);
                binding.tvOriginalPrice.setPaintFlags(
                    binding.tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                
                double discount = ((product.getHighestPrice() - product.getLowestPrice()) 
                        / product.getHighestPrice()) * 100;
                binding.tvDiscount.setText(String.format("%.0f%% OFF", discount));
                binding.tvDiscount.setVisibility(View.VISIBLE);
            } else {
                binding.tvOriginalPrice.setVisibility(View.GONE);
                binding.tvDiscount.setVisibility(View.GONE);
            }

            binding.tvRating.setText(String.format("%.1f", product.getRating()));
            binding.tvReviewCount.setText(String.format("(%d)", product.getReviewCount()));

            // Load image
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(product.getImages().get(0))
                        .placeholder(R.drawable.ic_app_logo)
                        .error(R.drawable.ic_app_logo)
                        .centerCrop()
                        .into(binding.ivProductImage);
            }

            // Value score logic
            double valueScore = product.calculateValueScore();
            binding.tvValueScore.setText(String.format("%.0f", valueScore));
            binding.progressValueScore.setProgress((int) valueScore);
            
            // Dynamic Color for Value Score
            int scoreColor;
            if (valueScore >= 80) scoreColor = Color.parseColor("#10B981"); // Green
            else if (valueScore >= 50) scoreColor = Color.parseColor("#F59E0B"); // Amber
            else scoreColor = Color.parseColor("#EF4444"); // Red
            binding.tvValueScore.setTextColor(scoreColor);

            // --- SPARKLINE DATA FIX ---
            setupSparkline(product);

            // Click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });

            binding.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCartClick(product);
            });

            binding.btnCompare.setOnClickListener(v -> {
                if (listener != null) listener.onCompareClick(product);
            });
        }

        private void setupSparkline(Product product) {
            PriceHistory history = DummyData.getPriceHistory(product.getProductId(), "Amazon");
            List<PriceHistory.PricePoint> points = history.getPricePoints();
            
            if (points == null || points.isEmpty()) {
                binding.sparkline.setVisibility(View.GONE);
                return;
            }
            binding.sparkline.setVisibility(View.VISIBLE);

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                entries.add(new Entry(i, (float) points.get(i).getPrice()));
            }

            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
            dataSet.setLineWidth(1.5f);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawFilled(true);
            dataSet.setFillAlpha(40);
            dataSet.setFillColor(ContextCompat.getColor(itemView.getContext(), R.color.primary_container));

            LineData lineData = new LineData(dataSet);
            binding.sparkline.setData(lineData);
            
            // Minimalist styling for sparkline
            binding.sparkline.getDescription().setEnabled(false);
            binding.sparkline.getLegend().setEnabled(false);
            binding.sparkline.getXAxis().setEnabled(false);
            binding.sparkline.getAxisLeft().setEnabled(false);
            binding.sparkline.getAxisRight().setEnabled(false);
            binding.sparkline.setTouchEnabled(false);
            binding.sparkline.invalidate();
        }
    }
}
