package org.recipes.recipebook.dto;

import lombok.Data;

@Data
public class CreateIngredientDto {
    private String name;
    private String amount;
    private String unit;
}
