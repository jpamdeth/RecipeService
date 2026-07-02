package org.recipes.recipebook.service;

import java.util.List;
import java.util.UUID;

import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;
import org.recipes.recipebook.repository.RecipeIngredientRepository;
import org.recipes.recipebook.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.NonNull;

@Service
public class RecipeService {
    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);

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
        return recipeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));
    }

    public void deleteRecipeById(@NonNull UUID id) {
        recipeRepository.deleteById(id);
    }

    public Recipe updateRecipe(@NonNull Recipe recipe, @NonNull UUID id) {
        if (!recipe.getId().equals(id))
            throw new IllegalArgumentException("The recipe id does not match the id provided");
        return recipeRepository.save(recipe);
    }

    public Page<Recipe> getAllRecipes(@NonNull Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    public void addIngredientsToRecipe(@NonNull UUID recipeId, @NonNull List<RecipeIngredient> recipeIngredients) {
        // Each body element carries its own recipeId; reject any that disagree with the path
        // so a caller cannot attach ingredients to an arbitrary recipe via a crafted payload.
        for (RecipeIngredient ri : recipeIngredients) {
            if (ri.getRecipeId() == null) {
                ri.setRecipeId(recipeId);
            } else if (!ri.getRecipeId().equals(recipeId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "RecipeIngredient.recipeId " + ri.getRecipeId()
                        + " does not match path id " + recipeId);
            }
        }
        recipeIngredientRepository.saveAll(recipeIngredients);
    }

    @Transactional
    public void makeRecipe(@NonNull UUID id) {
        List<RecipeIngredient> ingredients = recipeIngredientRepository.findRecipeIngredientsByRecipeId(id);
        if (ingredients.isEmpty()) {
            log.warn("makeRecipe: recipe {} has no ingredients or does not exist", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Recipe " + id + " has no ingredients or does not exist");
        }
        for (RecipeIngredient ingredient : ingredients) {
            int rowsUpdated = ingredientService.useIngredient(
                ingredient.getIngredientId(), ingredient.getAmount(), ingredient.getUnit());
            if (rowsUpdated == 0) {
                // Rolls back the whole transaction — partial stock consumption would corrupt the pantry.
                log.error("makeRecipe: insufficient stock or unit mismatch for ingredient {} (recipe {}, requested {} {})",
                    ingredient.getIngredientId(), id, ingredient.getAmount(), ingredient.getUnit());
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Insufficient stock or unit mismatch for ingredient " + ingredient.getIngredientId());
            }
        }
    }
}
