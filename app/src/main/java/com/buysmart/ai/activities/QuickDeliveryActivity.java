package com.buysmart.ai.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buysmart.ai.R;
import com.buysmart.ai.adapters.ProductAdapter;
import com.buysmart.ai.adapters.RecipeAdapter;
import com.buysmart.ai.databinding.ActivityQuickDeliveryBinding;
import com.buysmart.ai.models.Product;
import com.buysmart.ai.models.Recipe;
import com.buysmart.ai.utils.CartManager;
import com.buysmart.ai.utils.DummyData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuickDeliveryActivity extends AppCompatActivity {

    private ActivityQuickDeliveryBinding binding;
    private CartManager cartManager;
    private RecipeAdapter recipeAdapter;
    private ProductAdapter essentialsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuickDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = new CartManager(this);

        setupToolbar();
        setupRecipes();
        setupEssentials();
        
        binding.fabCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        // ACCURATE REAL-WORLD INGREDIENTS MAPPING:
        // 1. Paneer Tikka: Organic Paneer (groc_0), Amul Curd (groc_1), Tikka Masala (groc_2), Bell Pepper (groc_3)
        recipes.add(new Recipe("r1", "Paneer Tikka", "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=400", 
                Arrays.asList("groc_0", "groc_1", "groc_2", "groc_3"), "20 Mins"));
        
        // 2. Spicy Maggi: Maggi (groc_4), Maggi Masala (groc_5), Amul Butter (groc_6), Frozen Peas (groc_7)
        recipes.add(new Recipe("r2", "Spicy Maggi", "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=400", 
                Arrays.asList("groc_4", "groc_5", "groc_6", "groc_7"), "10 Mins"));
        
        // 3. Avocado Toast: Bread (groc_8), Fresh Avocado (groc_9), Lemon (groc_10), Chili Flakes (groc_11)
        recipes.add(new Recipe("r3", "Avocado Toast", "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400", 
                Arrays.asList("groc_8", "groc_9", "groc_10", "groc_11"), "10 Mins"));
        
        // 4. Cold Coffee: Nescafe (groc_12), Fresh Milk (groc_13), White Sugar (groc_14), Choco Syrup (groc_15)
        recipes.add(new Recipe("r4", "Cold Coffee", "https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=400", 
                Arrays.asList("groc_12", "groc_13", "groc_14", "groc_15"), "5 Mins"));
        
        // 5. Egg Sandwich: Eggs (groc_16), Mayonnaise (groc_17), Black Pepper (groc_18), Bread (groc_8)
        recipes.add(new Recipe("r5", "Egg Sandwich", "https://images.unsplash.com/photo-1528733918455-5a59687cedf0?w=400", 
                Arrays.asList("groc_16", "groc_17", "groc_18", "groc_8"), "12 Mins"));

        recipeAdapter = new RecipeAdapter(recipes, recipe -> addRecipeIngredientsToCart(recipe));
        binding.rvRecipes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecipes.setAdapter(recipeAdapter);
    }

    private void addRecipeIngredientsToCart(Recipe recipe) {
        List<Product> allProducts = DummyData.getAllProducts();
        int addedCount = 0;
        for (String id : recipe.getIngredientProductIds()) {
            for (Product p : allProducts) {
                if (p.getProductId().equals(id)) {
                    cartManager.addToCart(p, p.getBestPricePlatform());
                    addedCount++;
                    break;
                }
            }
        }
        
        if (addedCount > 0) {
            Toast.makeText(this, "✨ AI: Added " + addedCount + " ingredients for " + recipe.getName() + "!", Toast.LENGTH_LONG).show();
            showFlyingAnimation(null);
        }
    }

    private void setupEssentials() {
        List<Product> allProducts = DummyData.getAllProducts();
        List<Product> groceries = new ArrayList<>();
        for (Product p : allProducts) {
            if ("Groceries".equals(p.getCategory())) {
                groceries.add(p);
            }
        }

        essentialsAdapter = new ProductAdapter(groceries, new ProductAdapter.ProductClickListener() {
            @Override
            public void onProductClick(Product product) {}

            @Override
            public void onAddToCartClick(Product product) {
                cartManager.addToCart(product, product.getBestPricePlatform());
                showFlyingAnimation(product);
                Toast.makeText(QuickDeliveryActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompareClick(Product product) {}
        });

        binding.rvEssentials.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEssentials.setAdapter(essentialsAdapter);
    }

    private void showFlyingAnimation(Product product) {
        final ImageView flyingIcon = new ImageView(this);
        flyingIcon.setImageResource(R.drawable.ic_groceries); 
        flyingIcon.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
        binding.rootLayout.addView(flyingIcon);
        flyingIcon.setX(binding.rootLayout.getWidth() / 2f - 60);
        flyingIcon.setY(binding.rootLayout.getHeight() / 2f - 60);

        flyingIcon.animate()
                .x(binding.fabCart.getX())
                .y(binding.fabCart.getY())
                .setDuration(1000)
                .alpha(0.3f)
                .scaleX(0.2f)
                .scaleY(0.2f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.rootLayout.removeView(flyingIcon);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
