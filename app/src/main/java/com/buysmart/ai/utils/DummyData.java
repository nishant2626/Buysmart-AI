package com.buysmart.ai.utils;

import com.buysmart.ai.models.Coupon;
import com.buysmart.ai.models.NearbyStore;
import com.buysmart.ai.models.PlatformPrice;
import com.buysmart.ai.models.PriceHistory;
import com.buysmart.ai.models.Product;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DummyData {

    private static final Random random = new Random();
    private static List<Product> cachedAllProducts;

    public static List<Product> getAllProducts() {
        if (cachedAllProducts == null) {
            cachedAllProducts = new ArrayList<>();
            cachedAllProducts.addAll(getElectronics());
            cachedAllProducts.addAll(getFashion());
            cachedAllProducts.addAll(getHomeAndLiving());
            cachedAllProducts.addAll(getBeautyProducts());
            cachedAllProducts.addAll(getSportsAndFitness());
            cachedAllProducts.addAll(getBooks());
            cachedAllProducts.addAll(getToys());
            cachedAllProducts.addAll(getGroceries());
        }
        return new ArrayList<>(cachedAllProducts);
    }

    private static final String[] ELEC_IMGS = {"photo-1511707171634-5f897ff02aa9", "photo-1598327105666-5b89351aff97", "photo-1517336714731-489689fd1ca8", "photo-1544244015-0df4b3ffc6b0", "photo-1610945415295-d9bff064e876", "photo-1695048133142-1a20484d256e"};
    private static final String[] FASH_IMGS = {"photo-1542291026-7eec264c27ff", "photo-1587563871167-1ee9c731aefb", "photo-1541099649105-f69ad21f3246", "photo-1596755094514-f87e34085b2c", "photo-1556821840-3a63f95609a7", "photo-1525966222134-fcfa99b8ae77"};
    private static final String[] HOME_IMGS = {"photo-1558317374-a354d5f6d4da", "photo-1584269600519-112d071b35e6", "photo-1594620302200-9a762244a156", "photo-1584622650111-993a426fbf0a", "photo-1518455027359-f3f8164ba6bd", "photo-1592078615290-033ee584e267"};
    private static final String[] BEAU_IMGS = {"photo-1522338242992-e1a54906a8da", "photo-1586495777744-4413f21062fa", "photo-1556228720-195a672e8a03", "photo-1556228515-01ff1ee9c25b", "photo-1556228444-77473038ac0b", "photo-1556228515-ce2f910f1806"};
    private static final String[] SPOR_IMGS = {"photo-1576243345690-8e4b728a3fa3", "photo-1518611012118-696072aa579a", "photo-1546519638-68e109498ffc", "photo-1617083270696-092f876c10b0", "photo-1599447421416-3414500d18a5", "photo-1514989940723-e8e51635b782"};
    private static final String[] BOOK_IMGS = {"photo-1544947950-fa07a98d237f", "photo-1592492159418-39f319320569", "photo-1589998059171-988d887df646", "photo-1512820790803-83ca734da794", "photo-1541963463532-d68292c34b19", "photo-1589829085413-56de8ae18c73"};
    private static final String[] TOY_IMGS = {"photo-1558060370-d644479cb6f7", "photo-1596461404969-9ae70f2830c1", "photo-1531651008558-ed175874f331", "photo-1594736797933-d0501ba2fe65", "photo-1566576721346-d4a3b4eaad5b", "photo-1515488764276-beab7607c1e6"};
    private static final String[] GROC_IMGS = {"photo-1542838132-92c53300491e", "photo-1583258292688-d0213dc5a3a8", "photo-1506617498519-afad5b50fff5"};

    public static List<Product> getElectronics() {
        List<Product> products = new ArrayList<>();
        String[] items = {"Samsung Galaxy S24", "iPhone 15 Pro", "Pixel 8 Pro", "OnePlus 12", "Sony XM5", "MacBook M3", "iPad Pro", "Dell XPS 13", "PS5 Slim", "Nintendo Switch", "Bose Ultra", "Canon R6", "GoPro 12", "Logitech MX", "ASUS ROG"};
        for (int i = 0; i < 15; i++) {
            double basePrice = 5000 + random.nextInt(140000);
            Product p = createProduct("elec_" + i, items[i % items.length] + " " + (i + 1), "Electronics", "TechBrand", basePrice, basePrice * 1.2, 4.0 + random.nextDouble(), 500 + random.nextInt(5000));
            p.setImages(getImagesFromPool(ELEC_IMGS, i));
            addPlatformPrices(p, basePrice, false);
            products.add(p);
        }
        return products;
    }

    public static List<Product> getFashion() {
        List<Product> products = new ArrayList<>();
        String[] items = {"Air Max 90", "Ultraboost", "511 Slim Jeans", "Oversized Hoodie", "Summer Shirt", "Running Sneakers", "Cotton Chinos", "Denim Jacket", "Classic Watch", "Aviator Glasses"};
        for (int i = 0; i < 15; i++) {
            double basePrice = 1000 + random.nextInt(15000);
            Product p = createProduct("fash_" + i, items[i % items.length] + " V" + i, "Fashion", "StyleHouse", basePrice, basePrice * 1.5, 3.8 + random.nextDouble() * 1.2, 100 + random.nextInt(10000));
            p.setImages(getImagesFromPool(FASH_IMGS, i));
            addPlatformPrices(p, basePrice, false);
            products.add(p);
        }
        return products;
    }

    public static List<Product> getHomeAndLiving() {
        List<Product> products = new ArrayList<>();
        String[] items = {"V12 Vacuum", "Air Fryer XL", "BILLY Bookcase", "Smart Fridge", "Washer Dryer", "Grinder Mixer", "Ortho Mattress", "L-Shape Sofa", "Dining Table", "Study Desk"};
        for (int i = 0; i < 15; i++) {
            double basePrice = 2000 + random.nextInt(80000);
            Product p = createProduct("home_" + i, items[i % items.length] + " " + i, "Home & Living", "LivingPro", basePrice, basePrice * 1.3, 4.2 + random.nextDouble() * 0.8, 50 + random.nextInt(2000));
            p.setImages(getImagesFromPool(HOME_IMGS, i));
            addPlatformPrices(p, basePrice, false);
            products.add(p);
        }
        return products;
    }

    public static List<Product> getBeautyProducts() {
        List<Product> products = new ArrayList<>();
        String[] items = {"Matte Lipstick", "Night Serum", "Moisture Surge", "Vitamin C Oil", "Black Mascara", "Hair Repair", "Face Wash", "Anthelios Sunscreen", "Antiacne Gel", "Hand Cream"};
        for (int i = 0; i < 15; i++) {
            double basePrice = 200 + random.nextInt(10000);
            Product p = createProduct("beau_" + i, items[i % items.length] + " " + i, "Beauty", "GlowUp", basePrice, basePrice * 1.2, 4.3 + random.nextDouble() * 0.7, 1000 + random.nextInt(15000));
            p.setImages(getImagesFromPool(BEAU_IMGS, i));
            addPlatformPrices(p, basePrice, false);
            products.add(p);
        }
        return products;
    }

    public static List<Product> getSportsAndFitness() {
        List<Product> products = new ArrayList<>();
        String[] items = {"Charge 6 Tracker", "Fenix 7 Solar", "Official NBA Ball", "Eco Yoga Mat", "Pure Drive Racket", "Elite Gloves", "Gym Bag", "Training Shoes", "Swim Goggles", "Dumbbell Set"};
        for (int i = 0; i < 15; i++) {
            double basePrice = 500 + random.nextInt(40000);
            Product p = createProduct("sport_" + i, items[i % items.length] + " " + i, "Sports", "Athelite", basePrice, basePrice * 1.4, 4.4 + random.nextDouble() * 0.6, 200 + random.nextInt(8000));
            p.setImages(getImagesFromPool(SPOR_IMGS, i));
            addPlatformPrices(p, basePrice, false);
            products.add(p);
        }
        return products;
    }

    public static List<Product> getBooks() {
        List<Product> products = new ArrayList<>();
        String[] items = {"Atomic Habits", "Psychology of Money", "Deep Work", "Zero to One", "Sapiens", "Thinking Fast", "The Alchemist", "Educated", "Principles", "12 Rules"};
        for (int i = 0; i < 15; i++) {
            double basePrice = 200 + random.nextInt(2000);
            Product p = createProduct("book_" + i, items[i % items.length] + " Ed." + i, "Books", "TopAuthor", basePrice, basePrice * 1.6, 4.6 + random.nextDouble() * 0.4, 5000 + random.nextInt(50000));
            p.setImages(getImagesFromPool(BOOK_IMGS, i));
            addPlatformPrices(p, basePrice, false);
            products.add(p);
        }
        return products;
    }

    public static List<Product> getToys() {
        List<Product> products = new ArrayList<>();
        String[] items = {"Falcon LEGO", "Technic Ferrari", "Hogwarts Castle", "Barbie House", "Elite Blaster", "Monopoly Deluxe", "Learning Bot", "Soft Plush", "Remote Car", "Action Figure"};
        for (int i = 0; i < 15; i++) {
            double basePrice = 500 + random.nextInt(20000);
            Product p = createProduct("toy_" + i, items[i % items.length] + " " + i, "Toys", "KidsJoy", basePrice, basePrice * 1.2, 4.3 + random.nextDouble() * 0.7, 100 + random.nextInt(5000));
            p.setImages(getImagesFromPool(TOY_IMGS, i));
            addPlatformPrices(p, basePrice, false);
            products.add(p);
        }
        return products;
    }

    public static List<Product> getGroceries() {
        List<Product> products = new ArrayList<>();
        String[] items = {
            "Organic Paneer 200g", "Amul Fresh Curd", "Everest Paneer Tikka Masala", "Fresh Red Bell Pepper", 
            "Maggi 2-Minute Noodles", "Maggi Masala-e-Magic", "Amul Butter 100g", "Safal Frozen Peas", 
            "Whole Wheat Bread", "Fresh Avocado", "Lemon", "Sprig Chili Flakes", 
            "Nescafe Instant Coffee", "Fresh Organic Milk", "Trust White Sugar", "Hershey's Chocolate Syrup", 
            "Farm Fresh Eggs (12)", "Hellman's Mayonnaise", "Catch Black Pepper"
        };
        for (int i = 0; i < items.length; i++) {
            double basePrice = 20 + random.nextInt(500);
            Product p = createProduct("groc_" + i, items[i], "Groceries", "EverFresh", basePrice, basePrice * 1.15, 4.2 + random.nextDouble() * 0.8, 1000 + random.nextInt(30000));
            p.setImages(getImagesFromPool(GROC_IMGS, i));
            addPlatformPrices(p, basePrice, true); // Mark groceries as potentially Eco-Friendly
            products.add(p);
        }
        return products;
    }

    private static Product createProduct(String id, String name, String cat, String brand, double low, double high, double rating, int reviews) {
        Product p = new Product();
        p.setProductId(id);
        p.setName(name);
        p.setCategory(cat);
        p.setBrand(brand);
        p.setLowestPrice(low);
        p.setHighestPrice(high);
        p.setRating(Math.min(5.0, rating));
        p.setReviewCount(reviews);
        p.setCurrency("INR");
        p.setInStock(true);
        p.setDescription("The " + name + " is a high-quality product from " + brand + " in the " + cat + " category.");
        p.setSpecifications("Brand: " + brand + "\nCondition: New\nRating: " + String.format("%.1f", rating));
        return p;
    }

    private static void addPlatformPrices(Product p, double basePrice, boolean isGrocery) {
        Map<String, PlatformPrice> prices = new HashMap<>();
        boolean flipCheapest = random.nextBoolean();
        double p1 = basePrice;
        double p2 = basePrice + 20 + random.nextInt(100);
        
        if (isGrocery) {
            // SwiftDash Platforms
            prices.put("Blinkit", createPlatformPrice("Blinkit", flipCheapest ? p1 : p2, basePrice * 1.2, true, 45 + random.nextInt(20)));
            prices.put("Zepto", createPlatformPrice("Zepto", flipCheapest ? p2 : p1, basePrice * 1.2, true, 40 + random.nextInt(30)));
            prices.put("Instamart", createPlatformPrice("Instamart", basePrice + 10, basePrice * 1.2, true, 30 + random.nextInt(40)));
        } else {
            // General Marketplace Platforms
            prices.put("Amazon", createPlatformPrice("Amazon", flipCheapest ? p1 : p2, basePrice * 1.2, false, 0));
            prices.put("Flipkart", createPlatformPrice("Flipkart", flipCheapest ? p2 : p1, basePrice * 1.2, false, 0));
            prices.put("Myntra", createPlatformPrice("Myntra", basePrice + 150, basePrice * 1.3, false, 0));
        }
        
        p.setPlatformPrices(prices);
    }

    private static PlatformPrice createPlatformPrice(String platform, double price, double original, boolean eco, double co2) {
        PlatformPrice pp = new PlatformPrice();
        pp.setPlatform(platform);
        pp.setPrice(price);
        pp.setOriginalPrice(original);
        pp.setDeliveryDays(0);
        pp.setRating(4.0 + random.nextDouble());
        pp.setInStock(true);
        pp.setCurrency("INR");
        pp.setEcoFriendly(eco);
        pp.setCo2Savings(co2);
        pp.setProductUrl("https://www." + platform.toLowerCase() + ".com");
        return pp;
    }

    private static List<String> getImagesFromPool(String[] pool, int index) {
        List<String> images = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String imgId = pool[(index + i) % pool.length];
            images.add("https://images.unsplash.com/" + imgId + "?w=800&q=80");
        }
        return images;
    }

    public static List<Coupon> getAllCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        String[] platforms = {"Blinkit", "Zepto", "Instamart", "Amazon", "Flipkart"};
        String[] codes = {"QUICK20", "FLASH500", "FRESH30", "FIRSTBUY", "SMARTPAY", "ECOSAVER"};
        for (int i = 0; i < codes.length; i++) {
            Coupon c = new Coupon();
            c.setCouponId("cp_" + i);
            c.setCode(codes[i]);
            c.setPlatform(platforms[i % platforms.length]);
            c.setDiscountPercentage(10 + (i * 5));
            c.setMinimumOrder(500);
            c.setVerified(true);
            c.setActive(true);
            c.setDescription("Get " + c.getDiscountPercentage() + "% OFF on " + c.getPlatform());
            c.setValidUntil(System.currentTimeMillis() + 864000000L);
            coupons.add(c);
        }
        return coupons;
    }

    public static PriceHistory getPriceHistory(String productId, String platform) {
        PriceHistory history = new PriceHistory();
        history.setProductId(productId);
        history.setPlatform(platform);
        List<PriceHistory.PricePoint> points = new ArrayList<>();
        long now = System.currentTimeMillis();
        double basePrice = 1000 + random.nextInt(50000);
        for (int i = 30; i >= 0; i--) {
            double price = basePrice * (1 + (Math.sin(i * 0.5) * 0.1));
            points.add(new PriceHistory.PricePoint(price, now - (i * 86400000L)));
        }
        history.setPricePoints(points);
        history.calculateStatistics();
        return history;
    }

    public static List<NearbyStore> getNearbyStores() { return new ArrayList<>(); }
    public static List<String> getMockReviews() { return new ArrayList<>(); }
}
