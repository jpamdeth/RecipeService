package org.recipes.recipebook.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.RecipebookApplication;
import org.recipes.recipebook.TestObjects;
import org.recipes.recipebook.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = {
    RecipebookApplication.class
  })
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class RecipeControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  RecipeService service;
  
  @Autowired
  RecipeController controller;

  @Test
  public void getRecipeSuccess()
  throws Exception {
    when(this.service.getRecipeById(TestObjects.recipeId)).thenReturn(TestObjects.recipe);

    mvc.perform(get("/recipes/" + TestObjects.recipeId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TestObjects.recipeString));

    verify(this.service).getRecipeById(TestObjects.recipeId);
  }

  @Test
  public void getAllRecipesSuccess()
  throws Exception {
    when(this.service.getAllRecipes()).thenReturn(TestObjects.recipes);

    mvc.perform(get("/recipes"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TestObjects.recipesString));

    verify(this.service).getAllRecipes();
  }

  @Test
  public void createRecipeSuccess()
  throws Exception {
    when(this.service.createRecipe(TestObjects.recipe)).thenReturn(TestObjects.recipe);

    mvc.perform(post("/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestObjects.recipeBytes))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TestObjects.recipeString));

    verify(this.service).createRecipe(TestObjects.recipe);
  }

  @Test
  public void updateRecipeSuccess()
  throws Exception {
    when(this.service.updateRecipe(TestObjects.recipe, TestObjects.recipeId)).thenReturn(TestObjects.recipe);

    mvc.perform(put("/recipes/" + TestObjects.recipeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestObjects.recipeBytes))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TestObjects.recipeString));

    verify(this.service).updateRecipe(TestObjects.recipe, TestObjects.recipeId);
  }

  @Test
  public void deleteRecipeSuccess()
  throws Exception {
    mvc.perform(delete("/recipes/" + TestObjects.recipeId))
        .andExpect(status().isNoContent());

    verify(this.service).deleteRecipeById(TestObjects.recipeId);
  }

  @Test
  public void addIngredientsToRecipeSuccess()
  throws Exception {
    mvc.perform(post("/recipes/" + TestObjects.recipeId + "/ingredients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestObjects.recipeIngredientListBytes))
        .andExpect(status().isOk());

    verify(this.service).addIngredientsToRecipe(TestObjects.recipeIngredientList);
  }

  @Test
  public void makeRecipeSuccess()
  throws Exception {
    mvc.perform(post("/recipes/" + TestObjects.recipeId + "/make"))
        .andExpect(status().isNoContent());

    verify(this.service).makeRecipe(TestObjects.recipeId);
  }
}
