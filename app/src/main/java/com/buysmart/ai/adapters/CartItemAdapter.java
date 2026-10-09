package com.buysmart.ai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ItemCartBinding;
import com.buysmart.ai.models.CartItem;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private List<CartItem> items;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int quantity);
        void onRemoveClick(CartItem item);
        void onItemClick(CartItem item);
    }

    public CartItemAdapter(List<CartItem> items, CartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ItemCartBinding binding;

        CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItem item) {
            binding.tvProductName.setText(item.getProductName());
            binding.tvPlatform.setText(item.getPlatform());
            binding.tvPrice.setText(String.format("₹%.2f", item.getPrice()));
            binding.tvQuantity.setText(String.valueOf(item.getQuantity()));
            binding.tvPrice.setText(String.format("₹%.2f", item.getTotalPrice()));

            if (item.getOriginalPrice() > item.getPrice()) {
                double savings = item.getTotalSavings();
                binding.tvSavings.setText(String.format("Save ₹%.2f", savings));
                binding.tvSavings.setVisibility(View.VISIBLE);
            } else {
                binding.tvSavings.setVisibility(View.GONE);
            }

            // Load image
            if (item.getProductImage() != null) {
                Glide.with(binding.getRoot().getContext())
                        .load(item.getProductImage())
                        .placeholder(R.drawable.ic_home)
                        .into(binding.ivProductImage);
            }

            // Click listeners
            binding.btnDecrease.setOnClickListener(v -> {
                int newQty = item.getQuantity() - 1;
                if (newQty >= 1) {
                    if (listener != null) listener.onQuantityChanged(item, newQty);
                }
            });

            binding.btnIncrease.setOnClickListener(v -> {
                int newQty = item.getQuantity() + 1;
                if (listener != null) listener.onQuantityChanged(item, newQty);
            });

            binding.btnRemove.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveClick(item);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }
}
