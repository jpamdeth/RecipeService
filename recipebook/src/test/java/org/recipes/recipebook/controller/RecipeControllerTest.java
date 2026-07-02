package org.recipes.recipebook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.RecipebookApplication;
import org.recipes.recipebook.helper.TestObjects;
import org.recipes.recipebook.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

  @MockitoBean
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
    when(this.service.getAllRecipes(PageRequest.of(0, 20)))
        .thenReturn(new PageImpl<>(TestObjects.recipes, PageRequest.of(0, 20), 1));

    mvc.perform(get("/recipes?page=0&size=20"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].id").value(TestObjects.recipeId.toString()))
        .andExpect(jsonPath("$.content[0].title").value(TestObjects.recipe.getTitle()))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));

    verify(this.service).getAllRecipes(PageRequest.of(0, 20));
  }

  @Test
  public void createRecipeSuccess()
  throws Exception {
    when(this.service.createRecipe(TestObjects.recipe)).thenReturn(TestObjects.recipe);

    mvc.perform(post("/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestObjects.recipeBytes))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/recipes/" + TestObjects.recipeId))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TestObjects.recipeString));

    verify(this.service).createRecipe(TestObjects.recipe);
  }

  @Test
  public void createRecipeValidationFailure()
  throws Exception {
    mvc.perform(post("/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.detail").value("Validation failed"))
        .andExpect(jsonPath("$.errors").isArray());
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
  public void updateRecipeIdMismatch()
  throws Exception {
    when(this.service.updateRecipe(any(), eq(TestObjects.recipeId)))
        .thenThrow(new IllegalArgumentException("The recipe id does not match the id provided"));

    mvc.perform(put("/recipes/" + TestObjects.recipeId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestObjects.recipeBytes))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.detail").value("The recipe id does not match the id provided"));
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

    verify(this.service).addIngredientsToRecipe(TestObjects.recipeId, TestObjects.recipeIngredientList);
  }

  @Test
  public void getRecipeNotFound()
  throws Exception {
    when(this.service.getRecipeById(TestObjects.recipeId))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

    mvc.perform(get("/recipes/" + TestObjects.recipeId))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.title").value("Not Found"))
        .andExpect(jsonPath("$.detail").value("Recipe not found"));
  }

  @Test
  public void makeRecipeConflict()
  throws Exception {
    doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock"))
        .when(this.service).makeRecipe(TestObjects.recipeId);

    mvc.perform(post("/recipes/" + TestObjects.recipeId + "/make"))
        .andExpect(status().isConflict())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.title").value("Conflict"))
        .andExpect(jsonPath("$.detail").value("Insufficient stock"));
  }

  @Test
  public void makeRecipeSuccess()
  throws Exception {
    mvc.perform(post("/recipes/" + TestObjects.recipeId + "/make"))
        .andExpect(status().isNoContent());

    verify(this.service).makeRecipe(TestObjects.recipeId);
  }
}
