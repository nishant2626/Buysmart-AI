package com.buysmart.ai.models;

import com.google.firebase.firestore.PropertyName;
import java.util.HashMap;
import java.util.Map;

public class PriceAlert {
    private String alertId;
    private String userId;
    private String productId;
    private String productName;
    private String productImage;
    private double targetPrice;
    private double currentPrice;
    private String platform;
    private boolean active;
    private boolean notified;
    private long createdAt;
    private long triggeredAt;

    public PriceAlert() {}

    public PriceAlert(String userId, String productId, double targetPrice) {
        this.userId = userId;
        this.productId = productId;
        this.targetPrice = targetPrice;
        this.active = true;
        this.notified = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(double targetPrice) { this.targetPrice = targetPrice; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    @PropertyName("active")
    public boolean isActive() { return active; }
    @PropertyName("active")
    public void setActive(boolean active) { this.active = active; }

    @PropertyName("notified")
    public boolean isNotified() { return notified; }
    @PropertyName("notified")
    public void setNotified(boolean notified) { this.notified = notified; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(long triggeredAt) { this.triggeredAt = triggeredAt; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("alertId", alertId);
        map.put("userId", userId);
        map.put("productId", productId);
        map.put("productName", productName);
        map.put("productImage", productImage);
        map.put("targetPrice", targetPrice);
        map.put("currentPrice", currentPrice);
        map.put("platform", platform);
        map.put("active", active);
        map.put("notified", notified);
        map.put("createdAt", createdAt);
        map.put("triggeredAt", triggeredAt);
        return map;
    }
}
