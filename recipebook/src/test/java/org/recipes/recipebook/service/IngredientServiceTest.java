package org.recipes.recipebook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recipes.recipebook.TestObjects;
import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.repository.IngredientRepository;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SuppressWarnings("null")
public class IngredientServiceTest {
  
  @Mock
  private IngredientRepository ingredientRepository;

  @InjectMocks
  private IngredientService ingredientService;

  @Test
  void createIngredient_ShouldReturnCreatedIngredient() {
    when(ingredientRepository.save(TestObjects.ingredient)).thenReturn(TestObjects.ingredient);

    Ingredient createdIngredient = ingredientService.createIngredient(TestObjects.ingredient);

    assertEquals(TestObjects.ingredient, createdIngredient);
    verify(ingredientRepository, times(1)).save(TestObjects.ingredient);
  }

  @Test
  void updateIngredient_ShouldReturnUpdatedIngredient() {
    when(ingredientRepository.save(TestObjects.ingredient)).thenReturn(TestObjects.ingredient);

    Ingredient updatedIngredient = ingredientService.updateIngredient(TestObjects.ingredient, TestObjects.ingredientId);

    assertEquals(TestObjects.ingredient, updatedIngredient);
    verify(ingredientRepository, times(1)).save(TestObjects.ingredient);
  }

  @Test
  void deleteIngredientById_ShouldDeleteIngredient() {
    doNothing().when(ingredientRepository).deleteById(TestObjects.ingredientId);

    ingredientService.deleteIngredient(TestObjects.ingredientId);

    verify(ingredientRepository, times(1)).deleteById(TestObjects.ingredientId);
  }

  @Test
  void getAllIngredients_ShouldReturnAllIngredients() {
    when(this.ingredientRepository.findAll()).thenReturn(TestObjects.ingredientList);

    Iterable<Ingredient> ingredients = ingredientService.getAllIngredients();

    assertEquals(TestObjects.ingredientList, ingredients);
    verify(ingredientRepository, times(1)).findAll();
  }

  @Test
  void getIngredientById_ShouldReturnIngredient() {
    when(this.ingredientRepository.findById(TestObjects.ingredientId)).thenReturn(java.util.Optional.of(TestObjects.ingredient));

    Ingredient foundIngredient = ingredientService.getIngredientById(TestObjects.ingredientId);

    assertEquals(TestObjects.ingredient, foundIngredient);
    verify(ingredientRepository, times(1)).findById(TestObjects .ingredientId);
  }
}
