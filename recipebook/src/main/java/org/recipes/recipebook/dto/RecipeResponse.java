package org.recipes.recipebook.dto;

import java.util.List;
import java.util.UUID;

public record RecipeResponse(
    UUID id,
    String title,
    String description,
    String category,
    String directions,
    List<IngredientResponse> ingredients
) {
}
