package org.recipes.recipebook.service;

import java.util.List;
import java.util.UUID;

import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;
import org.recipes.recipebook.repository.RecipeIngredientRepository;
import org.recipes.recipebook.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientService ingredientService;

    public RecipeService(RecipeRepository recipeRepository, RecipeIngredientRepository recipeIngredientRepository, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientService = ingredientService;
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

    public void addIngredientsToRecipe(@NonNull List<RecipeIngredient> recipeIngredients) {
        recipeIngredientRepository.saveAll(recipeIngredients);
    }

    public void makeRecipe(@NonNull UUID id) {
        List<RecipeIngredient> ingredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(id);
        for (RecipeIngredient ingredient : ingredients) {
            ingredientService.useIngredient(ingredient.getIngredientId(), ingredient.getAmount(), ingredient.getUnit());
        }
    }
}
