package org.recipes.recipebook.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.RecipebookApplication;
import org.recipes.recipebook.helper.TestObjects;
import org.recipes.recipebook.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = {
    RecipebookApplication.class
  })
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class IngredientControllerTest {
    
  @Autowired
  MockMvc mvc;

  @MockBean
  IngredientService service;

  @Autowired
  IngredientController controller;

  @Test
  public void createIngredientSuccess()
  throws Exception {
    when(this.service.createIngredient(TestObjects.ingredient)).thenReturn(TestObjects.ingredient);

    mvc.perform(post("/ingredients")
        .contentType(MediaType.APPLICATION_JSON)
        .content(TestObjects.ingredientBytes))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TestObjects.ingredientString));

    verify(this.service).createIngredient(TestObjects.ingredient);
  }

    @Test
    public void updateIngredientSuccess()
    throws Exception {
        when(this.service.updateIngredient(TestObjects.ingredient, TestObjects.ingredientId)).thenReturn(TestObjects.ingredient);

        mvc.perform(put("/ingredients/" + TestObjects.ingredientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestObjects.ingredientBytes))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(TestObjects.ingredientString));

        verify(this.service).updateIngredient(TestObjects.ingredient, TestObjects.ingredientId);
    }

    @Test
    public void getAllIngredientsSuccess()
    throws Exception {
        when(this.service.getAllIngredients()).thenReturn(TestObjects.ingredientList);

        mvc.perform(get("/ingredients"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(TestObjects.ingredientListString));

        verify(this.service).getAllIngredients();
    }

    @Test
    public void getIngredientByIdSuccess()
    throws Exception {
        when(this.service.getIngredientById(TestObjects.ingredientId)).thenReturn(TestObjects.ingredient);

        mvc.perform(get("/ingredients/" + TestObjects.ingredientId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(TestObjects.ingredientString));

        verify(this.service).getIngredientById(TestObjects.ingredientId);
    }

    @Test
    public void deleteIngredientSuccess()
    throws Exception {
        mvc.perform(delete("/ingredients/" + TestObjects.ingredientId))
            .andExpect(status().isNoContent());

        verify(this.service).deleteIngredient(TestObjects.ingredientId);
    }
}
