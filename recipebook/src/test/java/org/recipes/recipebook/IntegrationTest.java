package org.recipes.recipebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.recipes.recipebook.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

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

  // Recipe JSON without ID - let the database generate it
  private static final String NEW_RECIPE_JSON = "{\"title\": \"Test Recipe\", \"description\": \"Test Description\", \"category\": \"Test Category\", \"directions\": \"Test Directions\", \"ingredients\": []}";

  @Test
  void testRecipes() 
  throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    MvcResult result = mvc.perform(post("/recipes")
      .contentType(MediaType.APPLICATION_JSON)
      .content(NEW_RECIPE_JSON))
      .andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    Recipe recipe1 = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);

    result = mvc.perform(get("/recipes/" + recipe1.getId().toString()))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    Recipe retrieved = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);
    assertEquals(recipe1, retrieved);

    result = mvc.perform(post("/recipes")
      .contentType(MediaType.APPLICATION_JSON)
      .content(NEW_RECIPE_JSON))
      .andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    Recipe recipe2 = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);

    result = mvc.perform(get("/recipes?page=0&size=20"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    JsonNode recipesPage = mapper.readTree(result.getResponse().getContentAsString());
    Recipe[] recipes = mapper.treeToValue(recipesPage.get("content"), Recipe[].class);

    assertEquals(2, recipes.length);
    List<Recipe> recipeList = List.of(recipes);
    assertTrue(recipeList.contains(recipe1));
    assertTrue(recipeList.contains(recipe2));
    assertEquals(0, recipesPage.get("page").asInt());
    assertEquals(20, recipesPage.get("size").asInt());
    assertEquals(2, recipesPage.get("totalElements").asInt());

    // Refresh recipe1 from the database state before updating
    result = mvc.perform(get("/recipes/" + recipe1.getId().toString()))
      .andExpect(status().isOk())
      .andReturn();
    recipe1 = mapper.readValue(result.getResponse().getContentAsString(), Recipe.class);
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
