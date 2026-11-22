package org.recipes.recipebook.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;

import org.recipes.recipebook.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Recipe r where r.id = :id")
    Optional<Recipe> findByIdForUpdate(UUID id);
}
