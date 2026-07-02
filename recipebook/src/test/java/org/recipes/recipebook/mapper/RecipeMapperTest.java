package org.recipes.recipebook.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.dto.IngredientRequest;
import org.recipes.recipebook.dto.RecipeRequest;
import org.recipes.recipebook.dto.RecipeResponse;
import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.model.Recipe;

class RecipeMapperTest {

    @Test
    void toEntityMapsRecipeRequestAndNestedIngredients() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        RecipeRequest request = new RecipeRequest(
            recipeId,
            "Pancakes",
            "Breakfast stack",
            "breakfast",
            "Mix and cook.",
            List.of(new IngredientRequest(
                ingredientId,
                "Flour",
                "baking",
                "shelf stable",
                5,
                "cups")));

        Recipe recipe = RecipeMapper.toEntity(request);

        assertEquals(recipeId, recipe.getId());
        assertEquals("Pancakes", recipe.getTitle());
        assertEquals("Breakfast stack", recipe.getDescription());
        assertEquals("breakfast", recipe.getCategory());
        assertEquals("Mix and cook.", recipe.getDirections());
        assertEquals(1, recipe.getIngredients().size());
        assertEquals(ingredientId, recipe.getIngredients().get(0).getId());
    }

    @Test
    void toEntityUsesEmptyIngredientListWhenRequestIngredientsAreNull() {
        RecipeRequest request = new RecipeRequest(
            UUID.randomUUID(),
            "Pancakes",
            null,
            null,
            null,
            null);

        Recipe recipe = RecipeMapper.toEntity(request);

        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    void toResponseMapsRecipeEntityAndNestedIngredients() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        Recipe recipe = new Recipe(
            recipeId,
            "Pancakes",
            "Breakfast stack",
            "breakfast",
            "Mix and cook.",
            List.of(new Ingredient(
                ingredientId,
                "Flour",
                "baking",
                "shelf stable",
                5,
                "cups")));

        RecipeResponse response = RecipeMapper.toResponse(recipe);

        assertEquals(recipeId, response.id());
        assertEquals("Pancakes", response.title());
        assertEquals("Breakfast stack", response.description());
        assertEquals("breakfast", response.category());
        assertEquals("Mix and cook.", response.directions());
        assertEquals(1, response.ingredients().size());
        assertEquals(ingredientId, response.ingredients().get(0).id());
    }

    @Test
    void toResponseUsesEmptyIngredientListWhenEntityIngredientsAreNull() {
        Recipe recipe = new Recipe(
            UUID.randomUUID(),
            "Pancakes",
            null,
            null,
            null,
            null);

        RecipeResponse response = RecipeMapper.toResponse(recipe);

        assertTrue(response.ingredients().isEmpty());
    }
}
