package org.recipes.recipebook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recipes.recipebook.TestObjects;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.repository.RecipeIngredientRepository;
import org.recipes.recipebook.repository.RecipeRepository;
import org.springframework.test.context.ActiveProfiles;

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
        doNothing().when(ingredientService).useIngredient(any(), anyInt(), anyString());

        recipeService.makeRecipe(TestObjects.recipeId);

        verify(recipeIngredientRepository, times(1)).findRecipeIngredientsByRecipeId(TestObjects.recipeId);
        verify(ingredientService, times(1)).useIngredient(any(), anyInt(), anyString());
    }

    @Test
    void addIngredientsToRecipe_ShouldAddIngredients() {
        when(recipeIngredientRepository.saveAll(TestObjects.recipeIngredientList)).thenReturn(TestObjects.recipeIngredientList);

        recipeService.addIngredientsToRecipe(TestObjects.recipeIngredientList);

        verify(recipeIngredientRepository, times(1)).saveAll(TestObjects.recipeIngredientList);
    }
}
