package org.recipes.recipebook;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recipes.recipebook.controller.RecipeController;
import org.recipes.recipebook.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = {
    RecipebookApplication.class
  })
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RecipeControllerTest {

    @Mock
    private RecipeService service;
    
    @InjectMocks
    private RecipeController controller;

    @Autowired
    private MockMvc mvc;

    @Test
	void contextLoads() throws Exception {
		assertNotNull(controller);
	}

    @Test
    public void createRecipeSuccess()
    throws Exception {
        String recipe = "{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"title\": \"Test Recipe\", \"description\": \"Test Description\", \"category\": \"Test Category\", \"directions\": \"Test Directions\", \"ingredients\": []}";

        mvc.perform(post("/recipes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(recipe.getBytes()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
