package org.recipes.recipebook.service;

import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.repository.IngredientRepository;
import org.recipes.recipebook.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public IngredientService(IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(@NonNull UUID id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public Ingredient createIngredient(@NonNull Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public Ingredient updateIngredient(Ingredient ingredient, UUID id) {
        if (!ingredient.getId().equals(id))
            throw new IllegalArgumentException("The ingredient id does not match the id provided");
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(@NonNull UUID id) {
        ingredientRepository.deleteById(id);
    }

    public List<Recipe> getRecipesByIngredient(Ingredient ingredient) {
        return recipeRepository.findByIngredientsContaining(ingredient);
    }
}
