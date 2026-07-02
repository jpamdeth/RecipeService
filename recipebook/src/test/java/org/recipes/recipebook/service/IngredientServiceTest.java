package org.recipes.recipebook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recipes.recipebook.helper.TestObjects;
import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.repository.IngredientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

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
    PageRequest pageable = PageRequest.of(0, 20);
    Page<Ingredient> page = new PageImpl<>(TestObjects.ingredientList, pageable, TestObjects.ingredientList.size());
    when(this.ingredientRepository.findAll(pageable)).thenReturn(page);

    Page<Ingredient> ingredients = ingredientService.getAllIngredients(pageable);

    assertEquals(TestObjects.ingredientList, ingredients.getContent());
    verify(ingredientRepository, times(1)).findAll(pageable);
  }

  @Test
  void getIngredientById_ShouldReturnIngredient() {
    when(this.ingredientRepository.findById(TestObjects.ingredientId)).thenReturn(java.util.Optional.of(TestObjects.ingredient));

    Ingredient foundIngredient = ingredientService.getIngredientById(TestObjects.ingredientId);

    assertEquals(TestObjects.ingredient, foundIngredient);
    verify(ingredientRepository, times(1)).findById(TestObjects .ingredientId);
  }

  @Test
  void getIngredientById_ShouldThrow404_WhenMissing() {
    UUID missingId = UUID.randomUUID();
    when(this.ingredientRepository.findById(missingId)).thenReturn(Optional.empty());

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> ingredientService.getIngredientById(missingId));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }
}
