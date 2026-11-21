package org.recipes.recipebook.service;

import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.repository.IngredientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.NonNull;

import java.util.UUID;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Page<Ingredient> getAllIngredients(@NonNull Pageable pageable) {
        return ingredientRepository.findAll(pageable);
    }

    public Ingredient getIngredientById(@NonNull UUID id) {
        return ingredientRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient " + id + " not found"));
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

    /**
     * Decrement the stock of an ingredient atomically.
     * Returns the number of rows updated — 0 means the row was missing, the
     * unit did not match, or stock was insufficient.
     */
    public int useIngredient(@NonNull UUID id, int amount, String unit) {
        return ingredientRepository.useIngredient(id, amount, unit);
    }
}
