package org.recipes.recipebook.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreateRecipeDto {
    private String title;
    private String description;
    private String category;
    private String directions;
    private List<CreateIngredientDto> ingredients;
}
