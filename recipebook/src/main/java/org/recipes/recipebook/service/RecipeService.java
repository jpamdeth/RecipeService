package org.recipes.recipebook.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.recipes.recipebook.dto.RecipeIngredientRequest;
import org.recipes.recipebook.dto.RecipeRequest;
import org.recipes.recipebook.mapper.RecipeMapper;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;
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
    private final IngredientService ingredientService;

    public RecipeService(RecipeRepository recipeRepository, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
    }

    @Transactional
    public Recipe createRecipe(@NonNull RecipeRequest request) {
        Recipe recipe = RecipeMapper.toEntity(request);
        if (request.ingredients() != null) {
            writeIngredients(recipe, request.ingredients(), false);
        }
        return recipeRepository.save(recipe);
    }

    public Recipe getRecipeById(@NonNull UUID id) {
        return recipeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));
    }

    public void deleteRecipeById(@NonNull UUID id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    public Recipe updateRecipe(@NonNull RecipeRequest request, @NonNull UUID id) {
        if (request.id() != null && !id.equals(request.id())) {
            throw new IllegalArgumentException("The recipe id does not match the id provided");
        }
        Recipe recipe = getRecipeForUpdate(id);
        RecipeMapper.updateDetails(recipe, request);
        if (request.ingredients() != null) {
            writeIngredients(recipe, request.ingredients(), true);
        }
        return recipe;
    }

    public Page<Recipe> getAllRecipes(@NonNull Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    @Transactional
    public void addIngredientsToRecipe(@NonNull UUID recipeId, @NonNull List<RecipeIngredientRequest> ingredients) {
        writeIngredients(getRecipeForUpdate(recipeId), ingredients, false);
    }

    private Recipe getRecipeForUpdate(UUID id) {
        return recipeRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " not found"));
    }

    private void writeIngredients(Recipe recipe, List<RecipeIngredientRequest> ingredients, boolean replace) {
        Map<UUID, RecipeIngredientRequest> requested = new LinkedHashMap<>();
        for (RecipeIngredientRequest entry : ingredients) {
            if (entry == null || entry.ingredientId() == null || entry.amount() <= 0
                    || entry.unit() == null || entry.unit().isBlank() || entry.unit().length() > 25) {
                throw new IllegalArgumentException("Each recipe ingredient needs an id, a positive amount, and a unit of up to 25 characters");
            }
            if (entry.recipeId() != null && !entry.recipeId().equals(recipe.getId())) {
                throw new IllegalArgumentException("RecipeIngredient.recipeId does not match the recipe id");
            }
            if (requested.putIfAbsent(entry.ingredientId(), entry) != null) {
                throw new IllegalArgumentException("Duplicate recipe ingredient " + entry.ingredientId());
            }
        }

        List<RecipeIngredient> links = recipe.getIngredients();
        if (replace) {
            links.removeIf(link -> !requested.containsKey(link.getIngredientId()));
        }
        for (RecipeIngredient link : links) {
            RecipeIngredientRequest entry = requested.remove(link.getIngredientId());
            if (entry != null) {
                link.setAmount(entry.amount());
                link.setUnit(entry.unit());
            }
        }
        for (RecipeIngredientRequest entry : requested.values()) {
            var ingredient = ingredientService.getIngredientById(entry.ingredientId());
            links.add(new RecipeIngredient(recipe, ingredient, entry.amount(), entry.unit()));
        }
    }

    @Transactional
    public void makeRecipe(@NonNull UUID id) {
        List<RecipeIngredient> ingredients = getRecipeForUpdate(id).getIngredients();
        if (ingredients.isEmpty()) {
            log.warn("makeRecipe: recipe {} has no ingredients", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe " + id + " has no ingredients");
        }
        // Update shared pantry rows in a consistent order across recipes.
        List<RecipeIngredient> ordered = ingredients.stream()
            .sorted(Comparator.comparing(RecipeIngredient::getIngredientId))
            .toList();
        for (RecipeIngredient ingredient : ordered) {
            int rowsUpdated = ingredientService.useIngredient(
                ingredient.getIngredientId(), ingredient.getAmount(), ingredient.getUnit());
            if (rowsUpdated == 0) {
                log.warn("makeRecipe: insufficient stock or unit mismatch for ingredient {} (recipe {}, requested {} {})",
                    ingredient.getIngredientId(), id, ingredient.getAmount(), ingredient.getUnit());
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Insufficient stock or unit mismatch for ingredient " + ingredient.getIngredientId());
            }
        }
    }
}
