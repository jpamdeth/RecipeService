package org.recipes.recipebook.repository;

import java.util.List;
import java.util.UUID;

import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID>{
    public List<Recipe> findByIngredientsContaining(Ingredient ingredient);
}
