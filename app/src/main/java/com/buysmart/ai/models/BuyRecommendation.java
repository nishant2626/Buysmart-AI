package com.buysmart.ai.models;

public class BuyRecommendation {
    public enum RecommendationType {
        BUY_NOW,
        WAIT,
        PRICE_LIKELY_TO_DROP
    }

    private RecommendationType type;
    private String message;
    private String detailedAnalysis;
    private double confidenceScore;
    private double predictedLowestPrice;
    private long predictedDropDate;
    private double currentPrice;
    private double averagePrice;
    private double lowestHistoricalPrice;
    private String reasoning;

    public BuyRecommendation() {}

    public static BuyRecommendation analyze(Product product, PriceHistory history) {
        BuyRecommendation recommendation = new BuyRecommendation();
        recommendation.currentPrice = product.getLowestPrice();
        recommendation.averagePrice = history.getAveragePrice();
        recommendation.lowestHistoricalPrice = history.getLowestPrice();

        // Analyze price trends
        double priceChange = history.getPriceChangePercentage();
        double priceVsAverage = ((recommendation.currentPrice - recommendation.averagePrice) / recommendation.averagePrice) * 100;
        double priceVsLowest = ((recommendation.currentPrice - recommendation.lowestHistoricalPrice) / recommendation.lowestHistoricalPrice) * 100;

        // Calculate confidence score based on data quality
        recommendation.confidenceScore = calculateConfidenceScore(history);

        // Determine recommendation
        if (priceVsLowest <= 5) {
            // Price is within 5% of historical low
            recommendation.type = RecommendationType.BUY_NOW;
            recommendation.message = "Best time to buy is now!";
            recommendation.reasoning = "Current price is near the historical lowest price.";
        } else if (priceChange < -10) {
            // Price has dropped significantly recently
            recommendation.type = RecommendationType.BUY_NOW;
            recommendation.message = "Great deal! Price is dropping.";
            recommendation.reasoning = "Price has decreased by " + String.format("%.1f", -priceChange) + "% recently.";
        } else if (priceVsAverage > 15) {
            // Price is significantly above average
            recommendation.type = RecommendationType.WAIT;
            recommendation.message = "Consider waiting for a better price.";
            recommendation.reasoning = "Current price is " + String.format("%.1f", priceVsAverage) + "% above average.";
            recommendation.predictedLowestPrice = recommendation.averagePrice * 0.9;
            recommendation.predictedDropDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000); // 1 week
        } else {
            // Neutral - price likely to drop
            recommendation.type = RecommendationType.PRICE_LIKELY_TO_DROP;
            recommendation.message = "Price may drop soon.";
            recommendation.reasoning = "Based on historical patterns, price fluctuations suggest a potential drop.";
            recommendation.predictedLowestPrice = recommendation.averagePrice * 0.95;
            recommendation.predictedDropDate = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000); // 2 weeks
        }

        recommendation.detailedAnalysis = generateDetailedAnalysis(recommendation, history);
        return recommendation;
    }

    private static double calculateConfidenceScore(PriceHistory history) {
        int dataPoints = history.getPricePoints() != null ? history.getPricePoints().size() : 0;
        long timeSpan = 0;
        if (dataPoints > 1) {
            timeSpan = history.getPricePoints().get(dataPoints - 1).getTimestamp() 
                    - history.getPricePoints().get(0).getTimestamp();
        }

        double score = 0;
        // More data points = higher confidence
        score += Math.min(dataPoints / 30.0, 0.4); // Max 0.4 for 30+ data points
        // Longer time span = higher confidence
        score += Math.min(timeSpan / (90L * 24 * 60 * 60 * 1000), 0.4); // Max 0.4 for 90+ days
        // Base confidence
        score += 0.2;

        return Math.min(score, 1.0);
    }

    private static String generateDetailedAnalysis(BuyRecommendation rec, PriceHistory history) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("Price Analysis:\n");
        analysis.append(String.format("• Current Price: ₹%.2f\n", rec.currentPrice));
        analysis.append(String.format("• Average Price: ₹%.2f\n", rec.averagePrice));
        analysis.append(String.format("• Lowest Price: ₹%.2f\n", rec.lowestHistoricalPrice));
        analysis.append(String.format("• Price Change: %.1f%%\n", history.getPriceChangePercentage()));
        analysis.append(String.format("• Confidence: %.0f%%\n", rec.confidenceScore * 100));
        return analysis.toString();
    }

    // Getters
    public RecommendationType getType() { return type; }
    public void setType(RecommendationType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDetailedAnalysis() { return detailedAnalysis; }
    public void setDetailedAnalysis(String detailedAnalysis) { this.detailedAnalysis = detailedAnalysis; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public double getPredictedLowestPrice() { return predictedLowestPrice; }
    public void setPredictedLowestPrice(double predictedLowestPrice) { this.predictedLowestPrice = predictedLowestPrice; }

    public long getPredictedDropDate() { return predictedDropDate; }
    public void setPredictedDropDate(long predictedDropDate) { this.predictedDropDate = predictedDropDate; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getAveragePrice() { return averagePrice; }
    public void setAveragePrice(double averagePrice) { this.averagePrice = averagePrice; }

    public double getLowestHistoricalPrice() { return lowestHistoricalPrice; }
    public void setLowestHistoricalPrice(double lowestHistoricalPrice) { this.lowestHistoricalPrice = lowestHistoricalPrice; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public int getRecommendationColor() {
        switch (type) {
            case BUY_NOW:
                return 0xFF10B981; // Emerald Green
            case WAIT:
                return 0xFFEF4444; // Red
            case PRICE_LIKELY_TO_DROP:
                return 0xFFF59E0B; // Amber
            default:
                return 0xFF6B7280; // Gray
        }
    }

    public int getRecommendationIcon() {
        switch (type) {
            case BUY_NOW:
                return android.R.drawable.ic_menu_send;
            case WAIT:
                return android.R.drawable.ic_menu_recent_history;
            case PRICE_LIKELY_TO_DROP:
                return android.R.drawable.ic_menu_info_details;
            default:
                return android.R.drawable.ic_menu_help;
        }
    }
}
