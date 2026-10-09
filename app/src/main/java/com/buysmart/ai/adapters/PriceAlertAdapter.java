package com.buysmart.ai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ItemPriceAlertBinding;
import com.buysmart.ai.models.PriceAlert;

import java.util.List;

public class PriceAlertAdapter extends RecyclerView.Adapter<PriceAlertAdapter.AlertViewHolder> {

    private List<PriceAlert> alerts;
    private AlertClickListener listener;

    public interface AlertClickListener {
        void onAlertClick(PriceAlert alert);
        void onDeleteClick(PriceAlert alert);
        void onToggleClick(PriceAlert alert, boolean isActive);
        void onTestDropClick(PriceAlert alert); // New test method
    }

    public PriceAlertAdapter(List<PriceAlert> alerts, AlertClickListener listener) {
        this.alerts = alerts;
        this.listener = listener;
    }

    public void updateAlerts(List<PriceAlert> newAlerts) {
        this.alerts = newAlerts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPriceAlertBinding binding = ItemPriceAlertBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AlertViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        PriceAlert alert = alerts.get(position);
        holder.bind(alert);
    }

    @Override
    public int getItemCount() {
        return alerts != null ? alerts.size() : 0;
    }

    class AlertViewHolder extends RecyclerView.ViewHolder {
        private ItemPriceAlertBinding binding;

        AlertViewHolder(ItemPriceAlertBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PriceAlert alert) {
            binding.tvProductName.setText(alert.getProductName());
            binding.tvTargetPrice.setText(String.format("Target: ₹%,.0f", alert.getTargetPrice()));
            binding.tvCurrentPrice.setText(String.format("Current: ₹%,.0f", alert.getCurrentPrice()));
            
            if (alert.getProductImage() != null) {
                Glide.with(binding.getRoot().getContext())
                        .load(alert.getProductImage())
                        .placeholder(R.drawable.ic_app_logo)
                        .into(binding.ivProductImage);
            }

            if (alert.isNotified()) {
                binding.tvStatus.setText("🔥 Price Reached!");
                binding.tvStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.secondary));
            } else if (alert.isActive()) {
                binding.tvStatus.setText("Active");
                binding.tvStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.primary));
            } else {
                binding.tvStatus.setText("Paused");
                binding.tvStatus.setTextColor(binding.getRoot().getContext().getColor(R.color.on_surface_variant));
            }

            binding.switchActive.setChecked(alert.isActive());

            // Click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onAlertClick(alert);
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(alert);
            });

            binding.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) listener.onToggleClick(alert, isChecked);
            });

            binding.btnTestDrop.setOnClickListener(v -> {
                if (listener != null) listener.onTestDropClick(alert);
            });
        }
    }
}
