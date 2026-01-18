package org.recipes.recipebook.repository;

import java.util.UUID;

import org.recipes.recipebook.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID>{

    @Modifying
    @Query("UPDATE Ingredient i SET i.amount = i.amount - :amount WHERE i.id = :id AND i.unit = :unit")
    public void useIngredient(UUID id, int amount, String unit);
}
