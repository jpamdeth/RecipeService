package org.recipes.recipebook.mapper;

import java.util.ArrayList;
import java.util.List;

import org.recipes.recipebook.dto.IngredientResponse;
import org.recipes.recipebook.dto.RecipeRequest;
import org.recipes.recipebook.dto.RecipeResponse;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;

public final class RecipeMapper {

    private RecipeMapper() {
    }

    public static Recipe toEntity(RecipeRequest request) {
        Recipe recipe = new Recipe(null, null, null, null, null, new ArrayList<>());
        updateDetails(recipe, request);
        return recipe;
    }

    public static void updateDetails(Recipe recipe, RecipeRequest request) {
        recipe.setTitle(request.title());
        recipe.setDescription(request.description());
        recipe.setCategory(request.category());
        recipe.setDirections(request.directions());
    }

    public static RecipeResponse toResponse(Recipe recipe) {
        List<IngredientResponse> ingredients = recipe.getIngredients() == null
            ? List.of()
            : recipe.getIngredients().stream()
                .map(RecipeMapper::toIngredientResponse)
                .toList();

        return new RecipeResponse(
            recipe.getId(),
            recipe.getTitle(),
            recipe.getDescription(),
            recipe.getCategory(),
            recipe.getDirections(),
            ingredients);
    }

    private static IngredientResponse toIngredientResponse(RecipeIngredient link) {
        var ingredient = link.getIngredient();
        return new IngredientResponse(ingredient.getId(), ingredient.getName(), ingredient.getType(),
            ingredient.getState(), link.getAmount(), link.getUnit());
    }
}
