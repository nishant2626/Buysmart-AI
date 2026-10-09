package com.buysmart.ai.models;

import java.util.List;

public class Recipe {
    private String id;
    private String name;
    private String imageUrl;
    private List<String> ingredientProductIds;
    private String timeToCook;

    public Recipe(String id, String name, String imageUrl, List<String> ingredientProductIds, String timeToCook) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.ingredientProductIds = ingredientProductIds;
        this.timeToCook = timeToCook;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getIngredientProductIds() { return ingredientProductIds; }
    public String getTimeToCook() { return timeToCook; }
}
