package org.recipes.recipebook.repository;

import java.util.List;
import java.util.UUID;

import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    List<Recipe> findByIngredientsContaining(Ingredient ingredient);
}
