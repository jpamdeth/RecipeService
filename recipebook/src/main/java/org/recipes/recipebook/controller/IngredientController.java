package org.recipes.recipebook.controller;

import java.net.URI;
import java.util.UUID;

import org.recipes.recipebook.dto.IngredientRequest;
import org.recipes.recipebook.dto.IngredientResponse;
import org.recipes.recipebook.dto.PageResponse;
import org.recipes.recipebook.mapper.IngredientMapper;
import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.service.IngredientService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController()
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/{id}")
    public IngredientResponse getIngredientById(@PathVariable UUID id) {
        return IngredientMapper.toResponse(ingredientService.getIngredientById(id));
    }

    @GetMapping("")
    public PageResponse<IngredientResponse> getAllIngredients(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.fromPage(ingredientService.getAllIngredients(pageable)
            .map(IngredientMapper::toResponse)
        );
    }

    @PostMapping("")
    public ResponseEntity<IngredientResponse> createIngredient(@Valid @RequestBody IngredientRequest ingredient) {
        Ingredient created = ingredientService.createIngredient(IngredientMapper.toEntity(ingredient));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity
            .created(location)
            .body(IngredientMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public IngredientResponse updateIngredient(@PathVariable UUID id, @Valid @RequestBody IngredientRequest ingredient) {
        Ingredient updated = ingredientService.updateIngredient(IngredientMapper.toEntity(ingredient), id);
        return IngredientMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIngredient(@PathVariable UUID id) {
        ingredientService.deleteIngredient(id);
    }
}
