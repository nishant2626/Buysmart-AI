package com.buysmart.ai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ItemPlatformPriceBinding;
import com.buysmart.ai.models.PlatformPrice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlatformPriceAdapter extends RecyclerView.Adapter<PlatformPriceAdapter.ViewHolder> {

    private List<Map.Entry<String, PlatformPrice>> platformPrices;
    private String bestPricePlatform;
    private String fastestDeliveryPlatform;
    private PlatformClickListener listener;

    public interface PlatformClickListener {
        void onBuyClick(PlatformPrice price);
    }

    public PlatformPriceAdapter(Map<String, PlatformPrice> prices, 
                                 String bestPricePlatform, 
                                 String fastestDeliveryPlatform,
                                 PlatformClickListener listener) {
        this.platformPrices = new ArrayList<>(prices.entrySet());
        this.bestPricePlatform = bestPricePlatform;
        this.fastestDeliveryPlatform = fastestDeliveryPlatform;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlatformPriceBinding binding = ItemPlatformPriceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, PlatformPrice> entry = platformPrices.get(position);
        holder.bind(entry.getKey(), entry.getValue());
    }

    @Override
    public int getItemCount() {
        return platformPrices != null ? platformPrices.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemPlatformPriceBinding binding;

        ViewHolder(ItemPlatformPriceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String platformName, PlatformPrice price) {
            binding.tvPlatformName.setText(platformName);
            binding.tvPrice.setText(String.format("₹%,.2f", price.getPrice()));
            binding.tvDeliveryTime.setText(price.getFormattedDelivery());

            // Sustainability Score (Green Badge)
            if (price.isEcoFriendly()) {
                binding.layoutEco.setVisibility(View.VISIBLE);
                binding.tvCo2Saved.setText(String.format("Eco: -%.0fg CO2", price.getCo2Savings()));
            } else {
                binding.layoutEco.setVisibility(View.GONE);
            }

            // Platform Logo Logic
            setPlatformIcon(platformName);

            binding.btnBuy.setOnClickListener(v -> {
                if (listener != null) listener.onBuyClick(price);
            });
        }

        private void setPlatformIcon(String platform) {
            int iconRes = R.drawable.ic_app_logo; // Default
            switch (platform.toLowerCase()) {
                case "amazon": 
                    binding.ivPlatformLogo.setImageResource(R.drawable.ic_app_logo); // Replace with real Amazon logo if available
                    break;
                case "blinkit":
                case "zepto":
                case "instamart":
                    binding.ivPlatformLogo.setImageResource(R.drawable.ic_groceries);
                    break;
            }
        }
    }
}
