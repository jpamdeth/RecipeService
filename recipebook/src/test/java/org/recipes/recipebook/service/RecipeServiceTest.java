package org.recipes.recipebook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recipes.recipebook.helper.TestObjects;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.repository.RecipeIngredientRepository;
import org.recipes.recipebook.repository.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SuppressWarnings("null")
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void createRecipe_ShouldReturnCreatedRecipe() {
        when(recipeRepository.save(TestObjects.recipe)).thenReturn(TestObjects.recipe);

        Recipe createdRecipe = recipeService.createRecipe(TestObjects.recipe);

        assertNotNull(createdRecipe);
        assertEquals(TestObjects.recipe.getTitle(), createdRecipe.getTitle());
        verify(recipeRepository, times(1)).save(TestObjects.recipe);
    }

    @Test
    void getRecipeById_ShouldReturnRecipe() {
        when(recipeRepository.findById(TestObjects.recipeId)).thenReturn(java.util.Optional.of(TestObjects.recipe));

        Recipe foundRecipe = recipeService.getRecipeById(TestObjects.recipeId);

        assertNotNull(foundRecipe);
        assertEquals(TestObjects.recipe.getTitle(), foundRecipe.getTitle());
        verify(recipeRepository, times(1)).findById(TestObjects.recipeId);
    }

    @Test
    void deleteRecipeById_ShouldDeleteRecipe() {
        doNothing().when(recipeRepository).deleteById(TestObjects.recipeId);

        recipeService.deleteRecipeById(TestObjects.recipeId);

        verify(recipeRepository, times(1)).deleteById(TestObjects.recipeId);
    }

    @Test
    void updateRecipe_ShouldReturnUpdatedRecipe() {
        when(recipeRepository.save(TestObjects.recipe)).thenReturn(TestObjects.recipe);

        Recipe updatedRecipe = recipeService.updateRecipe(TestObjects.recipe, TestObjects.recipeId);

        assertNotNull(updatedRecipe);
        assertEquals(TestObjects.recipe.getTitle(), updatedRecipe.getTitle());
        verify(recipeRepository, times(1)).save(TestObjects.recipe);
    }

    @Test
    void getAllRecipes_ShouldReturnAllRecipes() {
        when(recipeRepository.findAll()).thenReturn(TestObjects.recipes);

        Iterable<Recipe> allRecipes = recipeService.getAllRecipes();

        assertNotNull(allRecipes);
        assertEquals(TestObjects.recipes, allRecipes);
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void makeRecipe_ShouldUseIngredients() {
        when(recipeIngredientRepository.findRecipeIngredientsByRecipeId(TestObjects.recipeId)).thenReturn(TestObjects.recipeIngredientList);
        // useIngredient returns the JPA update row count — 1 means the stock was successfully decremented.
        when(ingredientService.useIngredient(any(), anyInt(), anyString())).thenReturn(1);

        recipeService.makeRecipe(TestObjects.recipeId);

        verify(recipeIngredientRepository, times(1)).findRecipeIngredientsByRecipeId(TestObjects.recipeId);
        verify(ingredientService, times(1)).useIngredient(any(), anyInt(), anyString());
    }

    @Test
    void getRecipeById_ShouldThrow404_WhenMissing() {
        UUID missingId = UUID.randomUUID();
        when(recipeRepository.findById(missingId)).thenReturn(java.util.Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> recipeService.getRecipeById(missingId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void makeRecipe_ShouldThrow404_WhenNoIngredients() {
        when(recipeIngredientRepository.findRecipeIngredientsByRecipeId(TestObjects.recipeId))
            .thenReturn(Collections.emptyList());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> recipeService.makeRecipe(TestObjects.recipeId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        // nothing should be decremented when the recipe is empty/missing
        verify(ingredientService, never()).useIngredient(any(), anyInt(), anyString());
    }

    @Test
    void makeRecipe_ShouldThrow409_WhenStockInsufficient() {
        when(recipeIngredientRepository.findRecipeIngredientsByRecipeId(TestObjects.recipeId))
            .thenReturn(TestObjects.recipeIngredientList);
        // 0 rows updated → stock was insufficient or unit mismatched
        when(ingredientService.useIngredient(any(), anyInt(), anyString())).thenReturn(0);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> recipeService.makeRecipe(TestObjects.recipeId));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void addIngredientsToRecipe_ShouldReject_WhenBodyRecipeIdMismatchesPath() {
        UUID otherRecipeId = UUID.randomUUID();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> recipeService.addIngredientsToRecipe(otherRecipeId, TestObjects.recipeIngredientList));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(recipeIngredientRepository, never()).saveAll(anyList());
    }

    @Test
    void addIngredientsToRecipe_ShouldAddIngredients() {
        when(recipeIngredientRepository.saveAll(TestObjects.recipeIngredientList)).thenReturn(TestObjects.recipeIngredientList);

        recipeService.addIngredientsToRecipe(TestObjects.recipeId, TestObjects.recipeIngredientList);

        verify(recipeIngredientRepository, times(1)).saveAll(TestObjects.recipeIngredientList);
    }
}
