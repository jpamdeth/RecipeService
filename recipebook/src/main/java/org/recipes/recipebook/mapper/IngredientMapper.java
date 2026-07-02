package org.recipes.recipebook.mapper;

import org.recipes.recipebook.dto.IngredientRequest;
import org.recipes.recipebook.dto.IngredientResponse;
import org.recipes.recipebook.model.Ingredient;

public final class IngredientMapper {

    private IngredientMapper() {
    }

    public static Ingredient toEntity(IngredientRequest request) {
        return new Ingredient(
            request.id(),
            request.name(),
            request.type(),
            request.state(),
            request.amount(),
            request.unit());
    }

    public static IngredientResponse toResponse(Ingredient ingredient) {
        return new IngredientResponse(
            ingredient.getId(),
            ingredient.getName(),
            ingredient.getType(),
            ingredient.getState(),
            ingredient.getAmount(),
            ingredient.getUnit());
    }
}
