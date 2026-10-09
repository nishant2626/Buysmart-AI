package com.buysmart.ai.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coupon {
    private String couponId;
    private String code;
    private String description;
    private String platform;
    private double discountAmount;
    private double discountPercentage;
    private double minimumOrder;
    private double maximumDiscount;
    private String category;
    private List<String> applicableProducts;
    private int successRate;
    private int usageCount;
    private long validFrom;
    private long validUntil;
    private boolean isVerified;
    private boolean isActive;
    private String terms;
    private String addedBy;
    private long createdAt;

    public Coupon() {
        this.applicableProducts = new ArrayList<>();
    }

    // Getters and Setters
    public String getCouponId() { return couponId; }
    public void setCouponId(String couponId) { this.couponId = couponId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

    public double getMinimumOrder() { return minimumOrder; }
    public void setMinimumOrder(double minimumOrder) { this.minimumOrder = minimumOrder; }

    public double getMaximumDiscount() { return maximumDiscount; }
    public void setMaximumDiscount(double maximumDiscount) { this.maximumDiscount = maximumDiscount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getApplicableProducts() { return applicableProducts; }
    public void setApplicableProducts(List<String> applicableProducts) { this.applicableProducts = applicableProducts; }

    public int getSuccessRate() { return successRate; }
    public void setSuccessRate(int successRate) { this.successRate = successRate; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    public long getValidFrom() { return validFrom; }
    public void setValidFrom(long validFrom) { this.validFrom = validFrom; }

    public long getValidUntil() { return validUntil; }
    public void setValidUntil(long validUntil) { this.validUntil = validUntil; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isValid() {
        long now = System.currentTimeMillis();
        return isActive && isVerified && now >= validFrom && now <= validUntil;
    }

    public boolean isApplicableToOrder(double orderAmount) {
        return isValid() && orderAmount >= minimumOrder;
    }

    public double calculateDiscount(double orderAmount) {
        if (!isApplicableToOrder(orderAmount)) {
            return 0;
        }

        double discount = 0;
        if (discountPercentage > 0) {
            discount = orderAmount * (discountPercentage / 100);
        } else if (discountAmount > 0) {
            discount = discountAmount;
        }

        if (maximumDiscount > 0) {
            discount = Math.min(discount, maximumDiscount);
        }

        return discount;
    }

    public String getFormattedDiscount() {
        if (discountPercentage > 0) {
            return (int) discountPercentage + "% OFF";
        } else if (discountAmount > 0) {
            return "₹" + (int) discountAmount + " OFF";
        }
        return "DISCOUNT";
    }

    public String getSuccessRateText() {
        if (successRate >= 90) return "Very High";
        if (successRate >= 70) return "High";
        if (successRate >= 50) return "Medium";
        return "Low";
    }

    public int getSuccessRateColor() {
        if (successRate >= 90) return 0xFF10B981; // Emerald
        if (successRate >= 70) return 0xFF3B82F6; // Blue
        if (successRate >= 50) return 0xFFF59E0B; // Amber
        return 0xFFEF4444; // Red
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("couponId", couponId);
        map.put("code", code);
        map.put("description", description);
        map.put("platform", platform);
        map.put("discountAmount", discountAmount);
        map.put("discountPercentage", discountPercentage);
        map.put("minimumOrder", minimumOrder);
        map.put("maximumDiscount", maximumDiscount);
        map.put("category", category);
        map.put("applicableProducts", applicableProducts);
        map.put("successRate", successRate);
        map.put("usageCount", usageCount);
        map.put("validFrom", validFrom);
        map.put("validUntil", validUntil);
        map.put("isVerified", isVerified);
        map.put("isActive", isActive);
        map.put("terms", terms);
        map.put("addedBy", addedBy);
        map.put("createdAt", createdAt);
        return map;
    }
}
