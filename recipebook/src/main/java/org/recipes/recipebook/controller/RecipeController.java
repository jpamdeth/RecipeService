package org.recipes.recipebook.controller;

import java.util.List;
import java.util.UUID;

import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;
import org.recipes.recipebook.service.RecipeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;

@RestController()
@RequestMapping("/recipes")
public class RecipeController {
    
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable UUID id) {
        return recipeService.getRecipeById(id);
    }

    @GetMapping("")
    public Iterable<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }
    
    @PostMapping("")
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.createRecipe(recipe);
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable UUID id, @RequestBody Recipe recipe) {
        return recipeService.updateRecipe(recipe, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipeById(id);
    }

    @PostMapping("/{id}/ingredients")
    public void addIngredientsToRecipe(@PathVariable UUID id, @RequestBody List<RecipeIngredient> ingredients) {
        recipeService.addIngredientsToRecipe(ingredients);
    }

    @PostMapping("/{id}/make")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeRecipe(@PathVariable UUID id) {
        recipeService.makeRecipe(id);
    }
}
