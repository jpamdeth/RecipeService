package org.recipes.recipebook.repository;

import java.util.List;
import java.util.UUID;

import org.recipes.recipebook.model.RecipeIngredient;
import org.recipes.recipebook.model.RecipeIngredientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId>{
    List<RecipeIngredient> findRecipeIngredientsByRecipeId(UUID id);
}
