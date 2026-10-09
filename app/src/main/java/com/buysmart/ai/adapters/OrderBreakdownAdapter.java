package com.buysmart.ai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.buysmart.ai.databinding.ItemOrderBreakdownBinding;
import com.buysmart.ai.models.CartItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBreakdownAdapter extends RecyclerView.Adapter<OrderBreakdownAdapter.ViewHolder> {

    private List<Map.Entry<String, List<CartItem>>> platformGroups;

    public OrderBreakdownAdapter(List<CartItem> items) {
        Map<String, List<CartItem>> groups = new HashMap<>();
        for (CartItem item : items) {
            if (!groups.containsKey(item.getPlatform())) {
                groups.put(item.getPlatform(), new ArrayList<>());
            }
            groups.get(item.getPlatform()).add(item);
        }
        this.platformGroups = new ArrayList<>(groups.entrySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBreakdownBinding binding = ItemOrderBreakdownBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, List<CartItem>> entry = platformGroups.get(position);
        holder.bind(entry.getKey(), entry.getValue());
    }

    @Override
    public int getItemCount() {
        return platformGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemOrderBreakdownBinding binding;

        ViewHolder(ItemOrderBreakdownBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String platform, List<CartItem> items) {
            binding.tvPlatformName.setText(platform);
            StringBuilder itemsText = new StringBuilder();
            double total = 0;
            for (CartItem item : items) {
                itemsText.append(item.getProductName()).append(" (x").append(item.getQuantity()).append("), ");
                total += item.getTotalPrice();
            }
            if (itemsText.length() > 2) itemsText.setLength(itemsText.length() - 2);
            binding.tvItemsList.setText(itemsText.toString());
            binding.tvPlatformSubtotal.setText(String.format("₹%,.2f", total));
        }
    }
}
