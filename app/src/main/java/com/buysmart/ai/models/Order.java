package com.buysmart.ai.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order implements Parcelable {
    private String orderId;
    private String userId;
    private List<CartItem> items;
    private double totalAmount;
    private double savings;
    private String platform;
    private String status; 
    private long orderDate;
    private String address;
    private String paymentMethod;
    private double co2Saved; // Impact Feature: Sustainability Score

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = System.currentTimeMillis();
        this.status = "Confirmed";
    }

    protected Order(Parcel in) {
        orderId = in.readString();
        userId = in.readString();
        items = in.createTypedArrayList(CartItem.CREATOR);
        totalAmount = in.readDouble();
        savings = in.readDouble();
        platform = in.readString();
        status = in.readString();
        orderDate = in.readLong();
        address = in.readString();
        paymentMethod = in.readString();
        co2Saved = in.readDouble();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }
        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(userId);
        dest.writeTypedList(items);
        dest.writeDouble(totalAmount);
        dest.writeDouble(savings);
        dest.writeString(platform);
        dest.writeString(status);
        dest.writeLong(orderDate);
        dest.writeString(address);
        dest.writeString(paymentMethod);
        dest.writeDouble(co2Saved);
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getSavings() { return savings; }
    public void setSavings(double savings) { this.savings = savings; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getOrderDate() { return orderDate; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public double getCo2Saved() { return co2Saved; }
    public void setCo2Saved(double co2Saved) { this.co2Saved = co2Saved; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("userId", userId);
        List<Map<String, Object>> itemsMap = new ArrayList<>();
        if (items != null) {
            for (CartItem item : items) {
                itemsMap.add(item.toMap());
            }
        }
        map.put("items", itemsMap);
        map.put("totalAmount", totalAmount);
        map.put("savings", savings);
        map.put("platform", platform);
        map.put("status", status);
        map.put("orderDate", orderDate);
        map.put("address", address);
        map.put("paymentMethod", paymentMethod);
        map.put("co2Saved", co2Saved);
        return map;
    }
}
