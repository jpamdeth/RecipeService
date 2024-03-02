package org.recipes.recipebook.model;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recipe_ingredient")
@IdClass(RecipeIngredientId.class)
public class RecipeIngredient {
    @Id
    @Column(name = "recipe_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID recipeId;

    @Id
    @Column(name = "ingredient_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID ingredientId;

    @Column(name = "amount")
    private int amount;

    @Column(name = "unit")
    private String unit;
}
