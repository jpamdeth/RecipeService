package org.recipes.recipebook.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.dto.RecipeIngredientRequest;
import org.recipes.recipebook.model.RecipeIngredient;

class RecipeIngredientMapperTest {

    @Test
    void toEntityMapsRecipeIngredientRequest() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        RecipeIngredientRequest request = new RecipeIngredientRequest(
            recipeId,
            ingredientId,
            2,
            "slices");

        RecipeIngredient recipeIngredient = RecipeIngredientMapper.toEntity(request);

        assertEquals(recipeId, recipeIngredient.getRecipeId());
        assertEquals(ingredientId, recipeIngredient.getIngredientId());
        assertEquals(2, recipeIngredient.getAmount());
        assertEquals("slices", recipeIngredient.getUnit());
    }
}
