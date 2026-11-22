package org.recipes.recipebook.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "recipe_ingredient")
public class RecipeIngredient {
    @EmbeddedId
    private RecipeIngredientId id;

    @MapsId("recipeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @MapsId("ingredientId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "amount")
    @Positive
    @Setter
    private int amount;

    @Column(name = "unit")
    @NotBlank
    @Size(max = 25)
    @Setter
    private String unit;

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, int amount, String unit) {
        this.id = new RecipeIngredientId(recipe.getId(), ingredient.getId());
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
    }

    public UUID getIngredientId() {
        return ingredient.getId();
    }
}
