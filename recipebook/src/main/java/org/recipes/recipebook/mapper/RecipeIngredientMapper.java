package org.recipes.recipebook.mapper;

import org.recipes.recipebook.dto.RecipeIngredientRequest;
import org.recipes.recipebook.model.RecipeIngredient;

public final class RecipeIngredientMapper {

    private RecipeIngredientMapper() {
    }

    public static RecipeIngredient toEntity(RecipeIngredientRequest request) {
        return new RecipeIngredient(
            request.recipeId(),
            request.ingredientId(),
            request.amount(),
            request.unit());
    }
}
