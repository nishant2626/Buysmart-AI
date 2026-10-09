package com.buysmart.ai.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class NearbyStore {
    private String storeId;
    private String name;
    private String address;
    private LatLng location;
    private double distance;
    private String phone;
    private String website;
    private String openingHours;
    private boolean isOpen;
    private double rating;
    private int reviewCount;
    private Map<String, Double> productPrices;
    private String imageUrl;

    public NearbyStore() {
        this.productPrices = new HashMap<>();
    }

    // Getters and Setters
    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LatLng getLocation() { return location; }
    public void setLocation(LatLng location) { this.location = location; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public Map<String, Double> getProductPrices() { return productPrices; }
    public void setProductPrices(Map<String, Double> productPrices) { this.productPrices = productPrices; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFormattedDistance() {
        if (distance < 1) {
            return (int) (distance * 1000) + " m";
        } else {
            return String.format("%.1f km", distance);
        }
    }

    public Double getPriceForProduct(String productId) {
        return productPrices.get(productId);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("storeId", storeId);
        map.put("name", name);
        map.put("address", address);
        if (location != null) {
            map.put("latitude", location.latitude);
            map.put("longitude", location.longitude);
        }
        map.put("distance", distance);
        map.put("phone", phone);
        map.put("website", website);
        map.put("openingHours", openingHours);
        map.put("isOpen", isOpen);
        map.put("rating", rating);
        map.put("reviewCount", reviewCount);
        map.put("productPrices", productPrices);
        map.put("imageUrl", imageUrl);
        return map;
    }

    public static NearbyStore fromMap(Map<String, Object> map) {
        NearbyStore store = new NearbyStore();
        store.setStoreId((String) map.get("storeId"));
        store.setName((String) map.get("name"));
        store.setAddress((String) map.get("address"));
        
        Double lat = (Double) map.get("latitude");
        Double lng = (Double) map.get("longitude");
        if (lat != null && lng != null) {
            store.setLocation(new LatLng(lat, lng));
        }
        
        store.setDistance(map.get("distance") != null ? ((Number) map.get("distance")).doubleValue() : 0);
        store.setPhone((String) map.get("phone"));
        store.setWebsite((String) map.get("website"));
        store.setOpeningHours((String) map.get("openingHours"));
        store.setOpen(map.get("isOpen") != null ? (Boolean) map.get("isOpen") : false);
        store.setRating(map.get("rating") != null ? ((Number) map.get("rating")).doubleValue() : 0);
        store.setReviewCount(map.get("reviewCount") != null ? ((Number) map.get("reviewCount")).intValue() : 0);
        store.setProductPrices((Map<String, Double>) map.get("productPrices"));
        store.setImageUrl((String) map.get("imageUrl"));
        return store;
    }
}
