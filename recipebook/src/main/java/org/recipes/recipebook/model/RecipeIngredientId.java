package org.recipes.recipebook.model;

import java.util.UUID;

import lombok.Data;

@Data
public class RecipeIngredientId {
    private UUID recipeId;
    private UUID ingredientId;

    public RecipeIngredientId() {
    }

    public RecipeIngredientId(UUID recipeId, UUID ingredientId) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }
}
