package org.recipes.recipebook;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.recipes.recipebook.model.Ingredient;
import org.recipes.recipebook.model.Recipe;
import org.recipes.recipebook.model.RecipeIngredient;

public class TestObjects {

    public static UUID recipeId = UUID.randomUUID();
    public static Recipe recipe = new Recipe(recipeId, "Test Recipe", "Test Description", "Test Category", "Test Directions", Collections.emptyList());
    public static String recipeString = "{\"id\": \"" + recipeId.toString() + "\", \"title\": \"Test Recipe\", \"description\": \"Test Description\", \"category\": \"Test Category\", \"directions\": \"Test Directions\", \"ingredients\": []}";
    public static byte[] recipeBytes = recipeString.getBytes();

    public static List<Recipe> recipes = Collections.singletonList(recipe);
    public static String recipesString = "[{\"id\": \"" + recipeId.toString() + "\", \"title\": \"Test Recipe\", \"description\": \"Test Description\", \"category\": \"Test Category\", \"directions\": \"Test Directions\", \"ingredients\": []}]";

    public static UUID ingredientId = UUID.randomUUID();
    public static Ingredient ingredient = new Ingredient(ingredientId, "Test Ingredient", "Test Type", "Test State", 1, "Test Unit");
    public static String ingredientString = "{\"id\": \"" + ingredientId.toString() + "\", \"name\": \"Test Ingredient\", \"type\": \"Test Type\", \"state\": \"Test State\", \"amount\": 1, \"unit\": \"Test Unit\"}";
    public static byte[] ingredientBytes = ingredientString.getBytes();

    public static List<Ingredient> ingredientList = Collections.singletonList(ingredient);
    public static String ingredientListString = "[{\"id\": \"" + ingredientId.toString() + "\", \"name\": \"Test Ingredient\", \"type\": \"Test Type\", \"state\": \"Test State\", \"amount\": 1, \"unit\": \"Test Unit\"}]";
    public static byte[] ingredientListBytes = ingredientListString.getBytes();

    public static RecipeIngredient recipeIngredient = new RecipeIngredient(recipeId, ingredientId, 1, "Test Unit");
    public static String recipeIngredientString = "{\"recipeId\": \"" + recipeId.toString() + "\", \"ingredientId\": \"" + ingredientId.toString() + "\", \"amount\": 1, \"unit\": \"Test Unit\"}";
    public static byte[] recipeIngredientBytes = recipeIngredientString.getBytes();

    public static List<RecipeIngredient> recipeIngredientList = Collections.singletonList(recipeIngredient);
    public static String recipeIngredientListString = "[{\"recipeId\": \"" + recipeId.toString() + "\", \"ingredientId\": \"" + ingredientId.toString() + "\", \"amount\": 1, \"unit\": \"Test Unit\"}]";
    public static byte[] recipeIngredientListBytes = recipeIngredientListString.getBytes();
}
