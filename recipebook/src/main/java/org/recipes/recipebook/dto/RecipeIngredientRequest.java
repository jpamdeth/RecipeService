package org.recipes.recipebook.dto;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecipeIngredientRequest(
    UUID recipeId,
    @JsonAlias("id") @NotNull UUID ingredientId,
    @Positive int amount,
    @NotBlank @Size(max = 25) String unit
) {
}
