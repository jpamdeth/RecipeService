package org.recipes.recipebook.dto;

import java.util.UUID;

public record IngredientResponse(
    UUID id,
    String name,
    String type,
    String state,
    int amount,
    String unit
) {
}
