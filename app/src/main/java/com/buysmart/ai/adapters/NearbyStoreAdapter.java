package com.buysmart.ai.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ItemNearbyStoreBinding;
import com.buysmart.ai.models.NearbyStore;

import java.util.List;

public class NearbyStoreAdapter extends RecyclerView.Adapter<NearbyStoreAdapter.ViewHolder> {

    private List<NearbyStore> stores;
    private StoreClickListener listener;

    public interface StoreClickListener {
        void onStoreClick(NearbyStore store);
    }

    public NearbyStoreAdapter(List<NearbyStore> stores, StoreClickListener listener) {
        this.stores = stores;
        this.listener = listener;
    }

    public void updateStores(List<NearbyStore> newStores) {
        this.stores = newStores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNearbyStoreBinding binding = ItemNearbyStoreBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NearbyStore store = stores.get(position);
        holder.bind(store);
    }

    @Override
    public int getItemCount() {
        return stores != null ? stores.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemNearbyStoreBinding binding;

        ViewHolder(ItemNearbyStoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NearbyStore store) {
            binding.tvStoreName.setText(store.getName());
            binding.tvAddress.setText(store.getAddress());
            binding.tvDistance.setText(store.getFormattedDistance());
            binding.tvRating.setText(String.format("%.1f", store.getRating()));

            // Open status
            if (store.isOpen()) {
                binding.tvOpenStatus.setText(R.string.open_now);
                binding.tvOpenStatus.setTextColor(binding.getRoot().getContext()
                        .getResources().getColor(R.color.secondary, null));
            } else {
                binding.tvOpenStatus.setText(R.string.closed);
                binding.tvOpenStatus.setTextColor(binding.getRoot().getContext()
                        .getResources().getColor(R.color.error, null));
            }

            // Load image
            if (store.getImageUrl() != null) {
                Glide.with(binding.getRoot().getContext())
                        .load(store.getImageUrl())
                        .placeholder(R.drawable.ic_location)
                        .into(binding.ivStoreImage);
            }

            // Click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onStoreClick(store);
            });

            binding.btnDirections.setOnClickListener(v -> {
                // Open directions in maps
                if (store.getLocation() != null) {
                    String uri = String.format("geo:%f,%f?q=%f,%f(%s)",
                            store.getLocation().latitude, store.getLocation().longitude,
                            store.getLocation().latitude, store.getLocation().longitude,
                            store.getName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    binding.getRoot().getContext().startActivity(intent);
                }
            });

            binding.btnCall.setOnClickListener(v -> {
                if (store.getPhone() != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, 
                            Uri.parse("tel:" + store.getPhone()));
                    binding.getRoot().getContext().startActivity(intent);
                }
            });
        }
    }
}
