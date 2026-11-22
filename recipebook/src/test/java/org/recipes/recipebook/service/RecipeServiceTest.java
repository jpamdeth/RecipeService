package org.recipes.recipebook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recipes.recipebook.helper.TestObjects;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;
import org.recipes.recipebook.repository.RecipeRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void createsRecipe() {
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(call -> call.getArgument(0));
        Recipe created = recipeService.createRecipe(TestObjects.recipeRequest);
        assertEquals(TestObjects.recipe.getTitle(), created.getTitle());
        verify(recipeRepository).save(created);
    }

    @Test
    void getsRecipeById() {
        when(recipeRepository.findById(TestObjects.recipeId)).thenReturn(Optional.of(TestObjects.recipe));
        assertEquals(TestObjects.recipe, recipeService.getRecipeById(TestObjects.recipeId));
    }

    @Test
    void reportsMissingRecipe() {
        UUID id = UUID.randomUUID();
        when(recipeRepository.findById(id)).thenReturn(Optional.empty());
        var error = assertThrows(ResponseStatusException.class, () -> recipeService.getRecipeById(id));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    @Test
    void deletesRecipeById() {
        recipeService.deleteRecipeById(TestObjects.recipeId);
        verify(recipeRepository).deleteById(TestObjects.recipeId);
    }

    @Test
    void updatesRecipe() {
        Recipe existing = recipeForUpdate(TestObjects.recipeId);
        assertSame(existing, recipeService.updateRecipe(TestObjects.recipeRequest, TestObjects.recipeId));
        assertEquals(TestObjects.recipe.getTitle(), existing.getTitle());
    }

    @Test
    void getsPageOfRecipes() {
        var pageable = PageRequest.of(0, 20);
        when(recipeRepository.findAll(pageable)).thenReturn(new PageImpl<>(TestObjects.recipes, pageable, 1));
        assertEquals(TestObjects.recipes, recipeService.getAllRecipes(pageable).getContent());
    }

    @Test
    void makesRecipeAndConsumesStock() {
        Recipe recipe = recipeForUpdate(TestObjects.recipeId);
        recipe.getIngredients().add(new RecipeIngredient(recipe, TestObjects.ingredient, 1, "Test Unit"));
        when(ingredientService.useIngredient(TestObjects.ingredientId, 1, "Test Unit")).thenReturn(1);

        recipeService.makeRecipe(TestObjects.recipeId);

        verify(ingredientService).useIngredient(TestObjects.ingredientId, 1, "Test Unit");
    }

    @Test
    void rejectsMakingRecipeWithoutIngredients() {
        recipeForUpdate(TestObjects.recipeId);
        var error = assertThrows(ResponseStatusException.class, () -> recipeService.makeRecipe(TestObjects.recipeId));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(ingredientService);
    }

    @Test
    void rejectsMakingRecipeWithInsufficientStock() {
        Recipe recipe = recipeForUpdate(TestObjects.recipeId);
        recipe.getIngredients().add(new RecipeIngredient(recipe, TestObjects.ingredient, 1, "Test Unit"));
        when(ingredientService.useIngredient(TestObjects.ingredientId, 1, "Test Unit")).thenReturn(0);

        var error = assertThrows(ResponseStatusException.class, () -> recipeService.makeRecipe(TestObjects.recipeId));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    @Test
    void rejectsAddingIngredientsToMismatchedRecipeId() {
        UUID otherRecipeId = UUID.randomUUID();
        Recipe recipe = recipeForUpdate(otherRecipeId);
        assertThrows(IllegalArgumentException.class,
            () -> recipeService.addIngredientsToRecipe(otherRecipeId, TestObjects.recipeIngredientRequests));
        assertTrue(recipe.getIngredients().isEmpty());
    }

    @Test
    void addsIngredients() {
        Recipe recipe = recipeForUpdate(TestObjects.recipeId);
        when(ingredientService.getIngredientById(TestObjects.ingredientId)).thenReturn(TestObjects.ingredient);

        recipeService.addIngredientsToRecipe(TestObjects.recipeId, TestObjects.recipeIngredientRequests);

        assertEquals(1, recipe.getIngredients().size());
        assertEquals(TestObjects.ingredientId, recipe.getIngredients().getFirst().getIngredientId());
    }

    private Recipe recipeForUpdate(UUID id) {
        Recipe recipe = new Recipe(id, "Old title", null, null, null, new ArrayList<>());
        when(recipeRepository.findByIdForUpdate(id)).thenReturn(Optional.of(recipe));
        return recipe;
    }
}
