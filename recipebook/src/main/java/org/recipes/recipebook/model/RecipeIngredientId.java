package org.recipes.recipebook.model;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RecipeIngredientId {
    @Column(name = "recipe_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID recipeId;

    @Column(name = "ingredient_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID ingredientId;
}
