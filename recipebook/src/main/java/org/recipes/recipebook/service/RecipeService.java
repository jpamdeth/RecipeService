package org.recipes.recipebook.service;

import java.util.UUID;

import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe createRecipe(@NonNull Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public Recipe getRecipeById(@NonNull UUID id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public void deleteRecipeById(@NonNull UUID id) {
        recipeRepository.deleteById(id);
    }

    public Recipe updateRecipe(@NonNull Recipe recipe, @NonNull UUID id) {
        if (!recipe.getId().equals(id))
            throw new IllegalArgumentException("The recipe id does not match the id provided");
        return recipeRepository.save(recipe);
    }

    public Iterable<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
}
