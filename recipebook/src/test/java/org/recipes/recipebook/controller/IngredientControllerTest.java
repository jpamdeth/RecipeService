package org.recipes.recipebook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.RecipebookApplication;
import org.recipes.recipebook.helper.TestObjects;
import org.recipes.recipebook.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

  @MockitoBean
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
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/ingredients/" + TestObjects.ingredientId))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(TestObjects.ingredientString));

    verify(this.service).createIngredient(TestObjects.ingredient);
  }

  @Test
  public void createIngredientValidationFailure()
  throws Exception {
    mvc.perform(post("/ingredients")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"\",\"amount\":-1}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.detail").value("Validation failed"))
        .andExpect(jsonPath("$.errors").isArray());
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
    public void updateIngredientIdMismatch()
    throws Exception {
        when(this.service.updateIngredient(any(), eq(TestObjects.ingredientId)))
            .thenThrow(new IllegalArgumentException("The ingredient id does not match the id provided"));

        mvc.perform(put("/ingredients/" + TestObjects.ingredientId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestObjects.ingredientBytes))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.detail").value("The ingredient id does not match the id provided"));
    }

    @Test
    public void getAllIngredientsSuccess()
    throws Exception {
        when(this.service.getAllIngredients(PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(TestObjects.ingredientList, PageRequest.of(0, 20), 1));

        mvc.perform(get("/ingredients?page=0&size=20"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content[0].id").value(TestObjects.ingredientId.toString()))
            .andExpect(jsonPath("$.content[0].name").value(TestObjects.ingredient.getName()))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(20))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1));

        verify(this.service).getAllIngredients(PageRequest.of(0, 20));
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
