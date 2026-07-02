package org.recipes.recipebook.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.dto.IngredientRequest;
import org.recipes.recipebook.dto.IngredientResponse;
import org.recipes.recipebook.model.Ingredient;

class IngredientMapperTest {

    @Test
    void toEntityMapsIngredientRequest() {
        UUID id = UUID.randomUUID();
        IngredientRequest request = new IngredientRequest(
            id,
            "Flour",
            "baking",
            "shelf stable",
            5,
            "cups");

        Ingredient ingredient = IngredientMapper.toEntity(request);

        assertEquals(id, ingredient.getId());
        assertEquals("Flour", ingredient.getName());
        assertEquals("baking", ingredient.getType());
        assertEquals("shelf stable", ingredient.getState());
        assertEquals(5, ingredient.getAmount());
        assertEquals("cups", ingredient.getUnit());
    }

    @Test
    void toResponseMapsIngredientEntity() {
        UUID id = UUID.randomUUID();
        Ingredient ingredient = new Ingredient(
            id,
            "Flour",
            "baking",
            "shelf stable",
            5,
            "cups");

        IngredientResponse response = IngredientMapper.toResponse(ingredient);

        assertEquals(id, response.id());
        assertEquals("Flour", response.name());
        assertEquals("baking", response.type());
        assertEquals("shelf stable", response.state());
        assertEquals(5, response.amount());
        assertEquals("cups", response.unit());
    }
}
