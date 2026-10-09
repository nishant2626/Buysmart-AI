package com.buysmart.ai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.buysmart.ai.R;
import com.buysmart.ai.databinding.ItemCouponBinding;
import com.buysmart.ai.models.Coupon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    private List<Coupon> coupons;
    private CouponClickListener listener;

    public interface CouponClickListener {
        void onCopyClick(Coupon coupon);
        void onApplyClick(Coupon coupon);
    }

    public CouponAdapter(List<Coupon> coupons, CouponClickListener listener) {
        this.coupons = coupons;
        this.listener = listener;
    }

    public void updateCoupons(List<Coupon> newCoupons) {
        this.coupons = newCoupons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCouponBinding binding = ItemCouponBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        holder.bind(coupon);
    }

    @Override
    public int getItemCount() {
        return coupons != null ? coupons.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCouponBinding binding;

        ViewHolder(ItemCouponBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Coupon coupon) {
            binding.tvCouponCode.setText(coupon.getCode());
            binding.tvDescription.setText(coupon.getDescription());
            binding.tvPlatform.setText(coupon.getPlatform());
            binding.tvDiscount.setText(coupon.getFormattedDiscount());
            binding.tvSuccessRate.setText(String.format("%d%% %s", 
                    coupon.getSuccessRate(), coupon.getSuccessRateText()));

            // Success rate color
            binding.tvSuccessRate.setTextColor(coupon.getSuccessRateColor());

            // Verified badge
            binding.tvVerified.setVisibility(coupon.isVerified() ? 
                    android.view.View.VISIBLE : android.view.View.GONE);

            // Expiry date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String expiryDate = sdf.format(new Date(coupon.getValidUntil()));
            binding.tvExpires.setText(String.format("Expires: %s", expiryDate));

            // Minimum order
            if (coupon.getMinimumOrder() > 0) {
                binding.tvMinOrder.setText(String.format("Min order: ₹%.0f", coupon.getMinimumOrder()));
                binding.tvMinOrder.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvMinOrder.setVisibility(android.view.View.GONE);
            }

            // Platform color
            int platformColor = getPlatformColor(coupon.getPlatform());
            binding.viewPlatformColor.setBackgroundColor(platformColor);

            // Click listeners
            binding.btnCopy.setOnClickListener(v -> {
                if (listener != null) listener.onCopyClick(coupon);
            });

            binding.btnApply.setOnClickListener(v -> {
                if (listener != null) listener.onApplyClick(coupon);
            });
        }

        private int getPlatformColor(String platform) {
            switch (platform.toLowerCase()) {
                case "amazon":
                    return ContextCompat.getColor(binding.getRoot().getContext(), R.color.amazon);
                case "flipkart":
                    return ContextCompat.getColor(binding.getRoot().getContext(), R.color.flipkart);
                case "meesho":
                    return ContextCompat.getColor(binding.getRoot().getContext(), R.color.meesho);
                case "myntra":
                    return ContextCompat.getColor(binding.getRoot().getContext(), R.color.myntra);
                case "nykaa":
                    return ContextCompat.getColor(binding.getRoot().getContext(), R.color.nykaa);
                default:
                    return ContextCompat.getColor(binding.getRoot().getContext(), R.color.primary);
            }
        }
    }
}
