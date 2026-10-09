package com.buysmart.ai.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class CartItem implements Parcelable {
    private String cartItemId;
    private String productId;
    private String productName;
    private String productImage;
    private String platform;
    private double price;
    private double originalPrice;
    private int quantity;
    private String currency;
    private long addedAt;

    public CartItem() {}

    public CartItem(Product product, PlatformPrice platformPrice) {
        this.productId = product.getProductId();
        this.productName = product.getName();
        this.productImage = product.getImages() != null && !product.getImages().isEmpty() 
                ? product.getImages().get(0) : null;
        this.platform = platformPrice.getPlatform();
        this.price = platformPrice.getPrice();
        this.originalPrice = platformPrice.getOriginalPrice();
        this.currency = platformPrice.getCurrency();
        this.quantity = 1;
        this.addedAt = System.currentTimeMillis();
    }

    protected CartItem(Parcel in) {
        cartItemId = in.readString();
        productId = in.readString();
        productName = in.readString();
        productImage = in.readString();
        platform = in.readString();
        price = in.readDouble();
        originalPrice = in.readDouble();
        quantity = in.readInt();
        currency = in.readString();
        addedAt = in.readLong();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cartItemId);
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(productImage);
        dest.writeString(platform);
        dest.writeDouble(price);
        dest.writeDouble(originalPrice);
        dest.writeInt(quantity);
        dest.writeString(currency);
        dest.writeLong(addedAt);
    }

    // Getters and Setters
    public String getCartItemId() { return cartItemId; }
    public void setCartItemId(String cartItemId) { this.cartItemId = cartItemId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }

    public double getTotalPrice() {
        return price * quantity;
    }

    public double getTotalSavings() {
        return (originalPrice - price) * quantity;
    }

    public double getDiscountPercentage() {
        if (originalPrice > 0 && price < originalPrice) {
            return Math.round(((originalPrice - price) / originalPrice) * 100);
        }
        return 0;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("cartItemId", cartItemId);
        map.put("productId", productId);
        map.put("productName", productName);
        map.put("productImage", productImage);
        map.put("platform", platform);
        map.put("price", price);
        map.put("originalPrice", originalPrice);
        map.put("quantity", quantity);
        map.put("currency", currency);
        map.put("addedAt", addedAt);
        return map;
    }
}
