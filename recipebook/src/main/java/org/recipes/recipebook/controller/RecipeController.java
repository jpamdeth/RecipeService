package org.recipes.recipebook.controller;

import java.util.List;
import java.net.URI;
import java.util.UUID;

import org.recipes.recipebook.dto.RecipeIngredientRequest;
import org.recipes.recipebook.dto.RecipeRequest;
import org.recipes.recipebook.dto.RecipeResponse;
import org.recipes.recipebook.dto.PageResponse;
import org.recipes.recipebook.mapper.RecipeIngredientMapper;
import org.recipes.recipebook.mapper.RecipeMapper;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;
import org.recipes.recipebook.service.RecipeService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController()
@RequestMapping("/recipes")
public class RecipeController {
    
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping("/{id}")
    public RecipeResponse getRecipe(@PathVariable UUID id) {
        return RecipeMapper.toResponse(recipeService.getRecipeById(id));
    }

    @GetMapping("")
    public PageResponse<RecipeResponse> getAllRecipes(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.fromPage(recipeService.getAllRecipes(pageable)
            .map(RecipeMapper::toResponse)
        );
    }
    
    @PostMapping("")
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest recipe) {
        Recipe created = recipeService.createRecipe(RecipeMapper.toEntity(recipe));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity
            .created(location)
            .body(RecipeMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public RecipeResponse updateRecipe(@PathVariable UUID id, @Valid @RequestBody RecipeRequest recipe) {
        Recipe updated = recipeService.updateRecipe(RecipeMapper.toEntity(recipe), id);
        return RecipeMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipeById(id);
    }

    @PostMapping("/{id}/ingredients")
    public void addIngredientsToRecipe(@PathVariable UUID id, @Valid @RequestBody List<@Valid RecipeIngredientRequest> ingredients) {
        List<RecipeIngredient> recipeIngredients = ingredients.stream()
            .map(ingredient -> RecipeIngredientMapper.toEntity(ingredient))
            .toList();
        recipeService.addIngredientsToRecipe(id, recipeIngredients);
    }

    @PostMapping("/{id}/make")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeRecipe(@PathVariable UUID id) {
        recipeService.makeRecipe(id);
    }
}
