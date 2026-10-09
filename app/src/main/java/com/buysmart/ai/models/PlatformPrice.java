package com.buysmart.ai.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class PlatformPrice implements Parcelable {
    private String platform;
    private double price;
    private double originalPrice;
    private double discount;
    private String currency;
    private int deliveryDays;
    private String deliveryType;
    private double rating;
    private int reviewCount;
    private boolean inStock;
    private String productUrl;
    private String sellerName;
    private boolean isOfficial;
    private Map<String, String> coupons;
    
    // Impact Features: Sustainability
    private boolean isEcoFriendly; // e.g., Bicycle/EV delivery
    private double co2Savings; // in grams

    public PlatformPrice() {
        this.coupons = new HashMap<>();
    }

    protected PlatformPrice(Parcel in) {
        platform = in.readString();
        price = in.readDouble();
        originalPrice = in.readDouble();
        discount = in.readDouble();
        currency = in.readString();
        deliveryDays = in.readInt();
        deliveryType = in.readString();
        rating = in.readDouble();
        reviewCount = in.readInt();
        inStock = in.readByte() != 0;
        productUrl = in.readString();
        sellerName = in.readString();
        isOfficial = in.readByte() != 0;
        isEcoFriendly = in.readByte() != 0;
        co2Savings = in.readDouble();
    }

    public static final Creator<PlatformPrice> CREATOR = new Creator<PlatformPrice>() {
        @Override
        public PlatformPrice createFromParcel(Parcel in) {
            return new PlatformPrice(in);
        }

        @Override
        public PlatformPrice[] newArray(int size) {
            return new PlatformPrice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(platform);
        dest.writeDouble(price);
        dest.writeDouble(originalPrice);
        dest.writeDouble(discount);
        dest.writeString(currency);
        dest.writeInt(deliveryDays);
        dest.writeString(deliveryType);
        dest.writeDouble(rating);
        dest.writeInt(reviewCount);
        dest.writeByte((byte) (inStock ? 1 : 0));
        dest.writeString(productUrl);
        dest.writeString(sellerName);
        dest.writeByte((byte) (isOfficial ? 1 : 0));
        dest.writeByte((byte) (isEcoFriendly ? 1 : 0));
        dest.writeDouble(co2Savings);
    }

    // Getters and Setters
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public int getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(int deliveryDays) { this.deliveryDays = deliveryDays; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public boolean isOfficial() { return isOfficial; }
    public void setOfficial(boolean official) { isOfficial = official; }

    public Map<String, String> getCoupons() { return coupons; }
    public void setCoupons(Map<String, String> coupons) { this.coupons = coupons; }

    public boolean isEcoFriendly() { return isEcoFriendly; }
    public void setEcoFriendly(boolean ecoFriendly) { isEcoFriendly = ecoFriendly; }

    public double getCo2Savings() { return co2Savings; }
    public void setCo2Savings(double co2Savings) { this.co2Savings = co2Savings; }

    public double getDiscountPercentage() {
        if (originalPrice > 0 && price < originalPrice) {
            return Math.round(((originalPrice - price) / originalPrice) * 100);
        }
        return 0;
    }

    public double getSavings() {
        return originalPrice - price;
    }

    public String getFormattedDelivery() {
        if (deliveryDays == 0) {
            return "10-Min Dash";
        } else if (deliveryDays == 1) {
            return "Next Day";
        } else {
            return deliveryDays + " Days";
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("platform", platform);
        map.put("price", price);
        map.put("originalPrice", originalPrice);
        map.put("discount", discount);
        map.put("currency", currency);
        map.put("deliveryDays", deliveryDays);
        map.put("deliveryType", deliveryType);
        map.put("rating", rating);
        map.put("reviewCount", reviewCount);
        map.put("inStock", inStock);
        map.put("productUrl", productUrl);
        map.put("sellerName", sellerName);
        map.put("isOfficial", isOfficial);
        map.put("coupons", coupons);
        map.put("isEcoFriendly", isEcoFriendly);
        map.put("co2Savings", co2Savings);
        return map;
    }
}
