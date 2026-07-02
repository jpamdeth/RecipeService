package org.recipes.recipebook.repository;

import java.util.UUID;

import org.recipes.recipebook.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {

    /**
     * Atomically decrements the stock of an ingredient when its unit matches.
     * The amount >= :amount guard prevents the row from going negative; the
     * caller can detect "not enough stock" via the returned row count (0 = no-op).
     *
     * @return number of rows updated (0 if stock was insufficient, unit mismatched, or id missing)
     */
    @Modifying
    @Query("UPDATE Ingredient i SET i.amount = i.amount - :amount "
         + "WHERE i.id = :id AND i.unit = :unit AND i.amount >= :amount")
    int useIngredient(UUID id, int amount, String unit);
}
