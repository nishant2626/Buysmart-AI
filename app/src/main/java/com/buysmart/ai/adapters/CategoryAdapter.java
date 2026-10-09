package com.buysmart.ai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buysmart.ai.databinding.ItemCategoryBinding;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private String[] categories;
    private int[] icons;
    private CategoryClickListener listener;

    public interface CategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(String[] categories, int[] icons, CategoryClickListener listener) {
        this.categories = categories;
        this.icons = icons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories[position];
        int icon = icons[position];
        holder.bind(category, icon);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.length : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ItemCategoryBinding binding;

        CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String category, int icon) {
            binding.tvCategoryName.setText(category);
            binding.ivCategoryIcon.setImageResource(icon);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onCategoryClick(category);
            });
        }
    }
}
