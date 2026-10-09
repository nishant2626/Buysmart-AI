package com.buysmart.ai.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceHistory {
    private String productId;
    private String platform;
    private List<PricePoint> pricePoints;
    private double currentPrice;
    private double lowestPrice;
    private double highestPrice;
    private long lowestPriceDate;
    private long highestPriceDate;
    private double averagePrice;
    private double priceChangePercentage;

    public PriceHistory() {
        this.pricePoints = new ArrayList<>();
    }

    public static class PricePoint {
        private double price;
        private long timestamp;

        public PricePoint() {}

        public PricePoint(double price, long timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public List<PricePoint> getPricePoints() { return pricePoints; }
    public void setPricePoints(List<PricePoint> pricePoints) { this.pricePoints = pricePoints; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getLowestPrice() { return lowestPrice; }
    public void setLowestPrice(double lowestPrice) { this.lowestPrice = lowestPrice; }

    public double getHighestPrice() { return highestPrice; }
    public void setHighestPrice(double highestPrice) { this.highestPrice = highestPrice; }

    public long getLowestPriceDate() { return lowestPriceDate; }
    public void setLowestPriceDate(long lowestPriceDate) { this.lowestPriceDate = lowestPriceDate; }

    public long getHighestPriceDate() { return highestPriceDate; }
    public void setHighestPriceDate(long highestPriceDate) { this.highestPriceDate = highestPriceDate; }

    public double getAveragePrice() { return averagePrice; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }

    public double getPriceChangePercentage() { return priceChangePercentage; }
    public void setPriceChangePercentage(double priceChangePercentage) { this.priceChangePercentage = priceChangePercentage; }

    public void calculateStatistics() {
        if (pricePoints == null || pricePoints.isEmpty()) return;

        double sum = 0;
        lowestPrice = Double.MAX_VALUE;
        highestPrice = Double.MIN_VALUE;

        for (PricePoint point : pricePoints) {
            double price = point.getPrice();
            sum += price;

            if (price < lowestPrice) {
                lowestPrice = price;
                lowestPriceDate = point.getTimestamp();
            }

            if (price > highestPrice) {
                highestPrice = price;
                highestPriceDate = point.getTimestamp();
            }
        }

        averagePrice = sum / pricePoints.size();
        currentPrice = pricePoints.get(pricePoints.size() - 1).getPrice();

        if (pricePoints.size() > 1) {
            double firstPrice = pricePoints.get(0).getPrice();
            priceChangePercentage = ((currentPrice - firstPrice) / firstPrice) * 100;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("platform", platform);
        map.put("currentPrice", currentPrice);
        map.put("lowestPrice", lowestPrice);
        map.put("highestPrice", highestPrice);
        map.put("lowestPriceDate", lowestPriceDate);
        map.put("highestPriceDate", highestPriceDate);
        map.put("averagePrice", averagePrice);
        map.put("priceChangePercentage", priceChangePercentage);
        return map;
    }
}
