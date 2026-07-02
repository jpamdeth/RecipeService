package org.recipes.recipebook.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeRequest(
    UUID id,
    @NotBlank @Size(max = 100) String title,
    @Size(max = 255) String description,
    @Size(max = 100) String category,
    @Size(max = 2000) String directions,
    List<@Valid IngredientRequest> ingredients
) {
}
