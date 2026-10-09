package com.buysmart.ai.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product implements Parcelable {
    private String productId;
    private String name;
    private String category;
    private String subCategory;
    private String brand;
    private List<String> images;
    private String description;
    private String specifications;
    private double rating;
    private int reviewCount;
    private Map<String, PlatformPrice> platformPrices;
    private double lowestPrice;
    private double highestPrice;
    private String currency;
    private boolean inStock;
    private List<String> tags;
    private long createdAt;
    private long updatedAt;

    public Product() {
        this.images = new ArrayList<>();
        this.platformPrices = new HashMap<>();
        this.tags = new ArrayList<>();
    }

    protected Product(Parcel in) {
        productId = in.readString();
        name = in.readString();
        category = in.readString();
        subCategory = in.readString();
        brand = in.readString();
        images = in.createStringArrayList();
        description = in.readString();
        specifications = in.readString();
        rating = in.readDouble();
        reviewCount = in.readInt();
        
        int size = in.readInt();
        platformPrices = new HashMap<>(size);
        for(int i = 0; i < size; i++){
            String key = in.readString();
            PlatformPrice value = in.readParcelable(PlatformPrice.class.getClassLoader());
            platformPrices.put(key, value);
        }

        lowestPrice = in.readDouble();
        highestPrice = in.readDouble();
        currency = in.readString();
        inStock = in.readByte() != 0;
        tags = in.createStringArrayList();
        createdAt = in.readLong();
        updatedAt = in.readLong();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(brand);
        dest.writeStringList(images);
        dest.writeString(description);
        dest.writeString(specifications);
        dest.writeDouble(rating);
        dest.writeInt(reviewCount);
        
        if (platformPrices == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(platformPrices.size());
            for (Map.Entry<String, PlatformPrice> entry : platformPrices.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeParcelable(entry.getValue(), flags);
            }
        }

        dest.writeDouble(lowestPrice);
        dest.writeDouble(highestPrice);
        dest.writeString(currency);
        dest.writeByte((byte) (inStock ? 1 : 0));
        dest.writeStringList(tags);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public Map<String, PlatformPrice> getPlatformPrices() { return platformPrices; }
    public void setPlatformPrices(Map<String, PlatformPrice> platformPrices) { this.platformPrices = platformPrices; }

    public double getLowestPrice() { return lowestPrice; }
    public void setLowestPrice(double lowestPrice) { this.lowestPrice = lowestPrice; }

    public double getHighestPrice() { return highestPrice; }
    public void setHighestPrice(double highestPrice) { this.highestPrice = highestPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isInStock() { return inStock; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public PlatformPrice getBestPricePlatform() {
        if (platformPrices == null || platformPrices.isEmpty()) return null;
        PlatformPrice best = null;
        double lowest = Double.MAX_VALUE;
        for (PlatformPrice price : platformPrices.values()) {
            if (price.getPrice() < lowest && price.isInStock()) {
                lowest = price.getPrice();
                best = price;
            }
        }
        return best;
    }

    public PlatformPrice getFastestDeliveryPlatform() {
        if (platformPrices == null || platformPrices.isEmpty()) return null;
        PlatformPrice fastest = null;
        int minDays = Integer.MAX_VALUE;
        for (PlatformPrice price : platformPrices.values()) {
            if (price.getDeliveryDays() < minDays && price.isInStock()) {
                minDays = price.getDeliveryDays();
                fastest = price;
            }
        }
        return fastest;
    }

    public double calculateValueScore() {
        // More Accurate AI Value Score Algorithm
        // 1. Price Factor (45%): Discount from highest recorded price
        double discountRatio = (highestPrice - lowestPrice) / (highestPrice > 0 ? highestPrice : 1);
        double priceScore = Math.min(discountRatio * 150, 100); // 0.2 discount -> 30 points

        // 2. Rating Factor (35%): Based on 0-5 stars
        double ratingScore = (rating / 5.0) * 100;

        // 3. Popularity Factor (20%): Based on review count (max out at 2000 reviews)
        double reviewScore = Math.min(reviewCount / 2000.0, 1.0) * 100;

        double finalScore = (priceScore * 0.45) + (ratingScore * 0.35) + (reviewScore * 0.20);
        
        // Add penalty if out of stock
        if (!inStock) finalScore *= 0.5;

        return Math.min(Math.max(finalScore, 10), 99); // Clamp between 10-99
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("name", name);
        map.put("category", category);
        map.put("subCategory", subCategory);
        map.put("brand", brand);
        map.put("images", images);
        map.put("description", description);
        map.put("specifications", specifications);
        map.put("rating", rating);
        map.put("reviewCount", reviewCount);
        map.put("platformPrices", platformPrices);
        map.put("lowestPrice", lowestPrice);
        map.put("highestPrice", highestPrice);
        map.put("currency", currency);
        map.put("inStock", inStock);
        map.put("tags", tags);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }
}
