package com.buysmart.ai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.buysmart.ai.databinding.ItemOrderBinding;
import com.buysmart.ai.models.CartItem;
import com.buysmart.ai.models.Order;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        
        String idText = "Order #";
        if (order.getOrderId() != null && order.getOrderId().length() >= 8) {
            idText += order.getOrderId().substring(0, 8).toUpperCase();
        } else {
            idText += "UNKNOWN";
        }
        holder.binding.tvOrderId.setText(idText);
        
        holder.binding.tvOrderDate.setText("Placed on " + sdf.format(new Date(order.getOrderDate())));
        holder.binding.tvOrderAmount.setText(String.format("₹%,.2f", order.getTotalAmount()));
        holder.binding.tvOrderPlatform.setText("Via " + (order.getPlatform() != null ? order.getPlatform() : "Marketplace"));
        holder.binding.tvOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Processing");

        StringBuilder itemsSummary = new StringBuilder();
        if (order.getItems() != null) {
            for (int i = 0; i < order.getItems().size(); i++) {
                itemsSummary.append(order.getItems().get(i).getProductName());
                if (i < order.getItems().size() - 1) itemsSummary.append(", ");
            }
        }
        holder.binding.tvOrderItems.setText(itemsSummary.toString());
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;
        OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
