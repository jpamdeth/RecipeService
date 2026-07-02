package org.recipes.recipebook.dto;

import java.util.UUID;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecipeIngredientRequest(
    UUID recipeId,
    UUID ingredientId,
    @Positive int amount,
    @Size(max = 25) String unit
) {
}
