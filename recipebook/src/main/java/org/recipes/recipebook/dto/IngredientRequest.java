package org.recipes.recipebook.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record IngredientRequest(
    UUID id,
    @NotBlank @Size(max = 50) String name,
    @Size(max = 50) String type,
    @Size(max = 50) String state,
    @PositiveOrZero int amount,
    @Size(max = 25) String unit
) {
}
