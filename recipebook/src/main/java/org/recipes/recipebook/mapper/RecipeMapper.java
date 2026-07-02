package org.recipes.recipebook.mapper;

import java.util.List;

import org.recipes.recipebook.dto.IngredientResponse;
import org.recipes.recipebook.dto.RecipeRequest;
import org.recipes.recipebook.dto.RecipeResponse;
import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.model.Recipe;

public final class RecipeMapper {

    private RecipeMapper() {
    }

    public static Recipe toEntity(RecipeRequest request) {
        List<Ingredient> ingredients = request.ingredients() == null
            ? List.of()
            : request.ingredients().stream()
                .map(IngredientMapper::toEntity)
                .toList();

        return new Recipe(
            request.id(),
            request.title(),
            request.description(),
            request.category(),
            request.directions(),
            ingredients);
    }

    public static RecipeResponse toResponse(Recipe recipe) {
        List<IngredientResponse> ingredients = recipe.getIngredients() == null
            ? List.of()
            : recipe.getIngredients().stream()
                .map(IngredientMapper::toResponse)
                .toList();

        return new RecipeResponse(
            recipe.getId(),
            recipe.getTitle(),
            recipe.getDescription(),
            recipe.getCategory(),
            recipe.getDirections(),
            ingredients);
    }
}
