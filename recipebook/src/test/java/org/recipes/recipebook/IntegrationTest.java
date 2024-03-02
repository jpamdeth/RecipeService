package org.recipes.recipebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.helper.TestObjects;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = {
    RecipebookApplication.class
  })
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class IntegrationTest {

  @Autowired
  MockMvc mvc;

  @Test
  void testRecipes() 
  throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    MvcResult result = mvc.perform(post("/recipes")
      .contentType(MediaType.APPLICATION_JSON)
      .content(TestObjects.recipeBytes))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    Recipe recipe1 = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);

    result = mvc.perform(get("/recipes/" + recipe1.getId().toString()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    Recipe retrieved = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);
    assert(recipe1.equals(retrieved));

    result = mvc.perform(post("/recipes")
      .contentType(MediaType.APPLICATION_JSON)
      .content(TestObjects.recipeBytes))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    Recipe recipe2 = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);

    result = mvc.perform(get("/recipes"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    Recipe[] recipes = mapper.readValue(result.getResponse().getContentAsString(), Recipe[].class);

    assert(recipes.length == 2);
    List<Recipe> recipeList = Arrays.asList(recipes);
    assert(recipeList.contains(recipe1));
    assert(recipeList.contains(recipe2));

    recipe1.setTitle("Updated Name");

    result = mvc.perform(put("/recipes/" + recipe1.getId().toString())
      .contentType(MediaType.APPLICATION_JSON)
      .content(mapper.writeValueAsBytes(recipe1)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    retrieved = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);
    assertEquals("Updated Name", retrieved.getTitle());
  }  
}
