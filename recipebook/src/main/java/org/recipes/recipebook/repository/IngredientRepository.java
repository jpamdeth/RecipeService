package org.recipes.recipebook.repository;

import java.util.UUID;

import org.recipes.recipebook.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID>{
    
    
}
