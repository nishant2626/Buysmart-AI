package com.buysmart.ai.utils;

import com.buysmart.ai.models.Coupon;
import com.buysmart.ai.models.PlatformPrice;
import com.buysmart.ai.models.PriceHistory;
import com.buysmart.ai.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class PriceComparisonAPI {

    private static final String BASE_URL = "https://api.buysmart.ai/v1/";
    private static PriceComparisonAPI instance;
    private final ApiService apiService;

    public interface ApiService {
        @GET("products/search")
        Call<List<Product>> searchProducts(@Query("q") String query, 
                                            @Query("page") int page,
                                            @Query("limit") int limit);

        @GET("products/{id}")
        Call<Product> getProduct(@Path("id") String productId);

        @GET("products/{id}/prices")
        Call<Map<String, PlatformPrice>> getProductPrices(@Path("id") String productId);

        @GET("products/{id}/history")
        Call<PriceHistory> getPriceHistory(@Path("id") String productId, 
                                           @Query("platform") String platform);

        @GET("products/trending")
        Call<List<Product>> getTrendingProducts(@Query("limit") int limit);

        @GET("products/deals")
        Call<List<Product>> getDeals(@Query("category") String category);

        @GET("coupons")
        Call<List<Coupon>> getCoupons(@Query("platform") String platform);

        @GET("coupons/validate")
        Call<Coupon> validateCoupon(@Query("code") String code, 
                                     @Query("amount") double amount);
    }

    private PriceComparisonAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized PriceComparisonAPI getInstance() {
        if (instance == null) {
            instance = new PriceComparisonAPI();
        }
        return instance;
    }

    public void searchProducts(String query, int page, int limit, 
                                final ApiCallback<List<Product>> callback) {
        Call<List<Product>> call = apiService.searchProducts(query, page, limit);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch products");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getProduct(String productId, final ApiCallback<Product> callback) {
        Call<Product> call = apiService.getProduct(productId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Product not found");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getProductPrices(String productId, final ApiCallback<Map<String, PlatformPrice>> callback) {
        Call<Map<String, PlatformPrice>> call = apiService.getProductPrices(productId);
        call.enqueue(new Callback<Map<String, PlatformPrice>>() {
            @Override
            public void onResponse(Call<Map<String, PlatformPrice>> call, 
                                   Response<Map<String, PlatformPrice>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch prices");
                }
            }

            @Override
            public void onFailure(Call<Map<String, PlatformPrice>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getPriceHistory(String productId, String platform, 
                                final ApiCallback<PriceHistory> callback) {
        Call<PriceHistory> call = apiService.getPriceHistory(productId, platform);
        call.enqueue(new Callback<PriceHistory>() {
            @Override
            public void onResponse(Call<PriceHistory> call, Response<PriceHistory> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch price history");
                }
            }

            @Override
            public void onFailure(Call<PriceHistory> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getTrendingProducts(int limit, final ApiCallback<List<Product>> callback) {
        Call<List<Product>> call = apiService.getTrendingProducts(limit);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch trending products");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getCoupons(String platform, final ApiCallback<List<Coupon>> callback) {
        Call<List<Coupon>> call = apiService.getCoupons(platform);
        call.enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch coupons");
                }
            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Mock data for demo purposes
    public static List<Product> getMockProducts() {
        List<Product> products = new ArrayList<>();

        Product p1 = new Product();
        p1.setProductId("p1");
        p1.setName("Samsung Galaxy S24 Ultra");
        p1.setCategory("Electronics");
        p1.setBrand("Samsung");
        p1.setDescription("The ultimate Galaxy experience with AI-powered features");
        p1.setRating(4.7);
        p1.setReviewCount(1250);
        p1.setLowestPrice(124999);
        p1.setHighestPrice(134999);
        p1.setCurrency("INR");
        p1.setInStock(true);
        
        Map<String, PlatformPrice> prices1 = new HashMap<>();
        PlatformPrice amazon1 = new PlatformPrice();
        amazon1.setPlatform("Amazon");
        amazon1.setPrice(124999);
        amazon1.setOriginalPrice(134999);
        amazon1.setDeliveryDays(2);
        amazon1.setRating(4.5);
        amazon1.setInStock(true);
        prices1.put("Amazon", amazon1);
        
        PlatformPrice flipkart1 = new PlatformPrice();
        flipkart1.setPlatform("Flipkart");
        flipkart1.setPrice(126999);
        flipkart1.setOriginalPrice(134999);
        flipkart1.setDeliveryDays(3);
        flipkart1.setRating(4.3);
        flipkart1.setInStock(true);
        prices1.put("Flipkart", flipkart1);
        
        p1.setPlatformPrices(prices1);
        products.add(p1);

        Product p2 = new Product();
        p2.setProductId("p2");
        p2.setName("Apple iPhone 15 Pro Max");
        p2.setCategory("Electronics");
        p2.setBrand("Apple");
        p2.setDescription("The most powerful iPhone ever with titanium design");
        p2.setRating(4.8);
        p2.setReviewCount(2100);
        p2.setLowestPrice(159900);
        p2.setHighestPrice(169900);
        p2.setCurrency("INR");
        p2.setInStock(true);
        
        Map<String, PlatformPrice> prices2 = new HashMap<>();
        PlatformPrice amazon2 = new PlatformPrice();
        amazon2.setPlatform("Amazon");
        amazon2.setPrice(159900);
        amazon2.setOriginalPrice(169900);
        amazon2.setDeliveryDays(1);
        amazon2.setRating(4.7);
        amazon2.setInStock(true);
        prices2.put("Amazon", amazon2);
        
        PlatformPrice flipkart2 = new PlatformPrice();
        flipkart2.setPlatform("Flipkart");
        flipkart2.setPrice(161900);
        flipkart2.setOriginalPrice(169900);
        flipkart2.setDeliveryDays(2);
        flipkart2.setRating(4.6);
        flipkart2.setInStock(true);
        prices2.put("Flipkart", flipkart2);
        
        p2.setPlatformPrices(prices2);
        products.add(p2);

        Product p3 = new Product();
        p3.setProductId("p3");
        p3.setName("Sony WH-1000XM5 Headphones");
        p3.setCategory("Electronics");
        p3.setBrand("Sony");
        p3.setDescription("Industry-leading noise canceling headphones");
        p3.setRating(4.6);
        p3.setReviewCount(850);
        p3.setLowestPrice(26990);
        p3.setHighestPrice(34990);
        p3.setCurrency("INR");
        p3.setInStock(true);
        
        Map<String, PlatformPrice> prices3 = new HashMap<>();
        PlatformPrice amazon3 = new PlatformPrice();
        amazon3.setPlatform("Amazon");
        amazon3.setPrice(26990);
        amazon3.setOriginalPrice(34990);
        amazon3.setDeliveryDays(2);
        amazon3.setRating(4.5);
        amazon3.setInStock(true);
        prices3.put("Amazon", amazon3);
        
        PlatformPrice flipkart3 = new PlatformPrice();
        flipkart3.setPlatform("Flipkart");
        flipkart3.setPrice(27990);
        flipkart3.setOriginalPrice(34990);
        flipkart3.setDeliveryDays(3);
        flipkart3.setRating(4.4);
        flipkart3.setInStock(true);
        prices3.put("Flipkart", flipkart3);
        
        p3.setPlatformPrices(prices3);
        products.add(p3);

        return products;
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
