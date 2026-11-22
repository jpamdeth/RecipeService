-- Run in the newly Flyway-initialized target database. The restored backup
-- belongs in the separate recipe_legacy schema. Keep app writers stopped.
DELIMITER $$

DROP PROCEDURE IF EXISTS import_recipe_legacy$$
CREATE PROCEDURE import_recipe_legacy()
SQL SECURITY INVOKER
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    IF DATABASE() = 'recipe_legacy'
        OR NOT EXISTS (SELECT 1 FROM flyway_schema_history WHERE version = '1' AND type = 'SQL' AND success = 1)
        OR (SELECT COUNT(*) FROM ingredient) <> 2
        OR (SELECT COUNT(*) FROM recipe) <> 1
        OR (SELECT COUNT(*) FROM recipe_ingredient) <> 2
        OR (SELECT COUNT(*) FROM ingredient WHERE amount = 12 AND unit = 'slices' AND (
            (id = '8f6b5018-f840-4d82-8327-53dee9b7fc26' AND name = 'cheese' AND type = 'dairy' AND state = 'refrigerated')
            OR (id = '18ffe751-be13-4346-a7e1-0b552589e4e3' AND name = 'bread' AND type = 'baked goods' AND state = 'shelf stable')
        )) <> 2
        OR NOT EXISTS (
            SELECT 1 FROM recipe WHERE id = '89e5f524-f211-47f6-a293-b89af579c541'
                AND title = 'grilled cheese sandwich' AND category = 'lunch'
                AND directions = 'butter and toast bread with cheese inside' AND description = 'description'
        )
        OR (SELECT COUNT(*) FROM recipe_ingredient
            WHERE recipe_id = '89e5f524-f211-47f6-a293-b89af579c541' AND amount = 2 AND unit = 'slices'
              AND ingredient_id IN ('8f6b5018-f840-4d82-8327-53dee9b7fc26', '18ffe751-be13-4346-a7e1-0b552589e4e3')
        ) <> 2
    THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Import requires a fresh Flyway target with only the original seed rows';
    END IF;

    IF EXISTS (SELECT 1 FROM recipe_legacy.ingredient WHERE amount < 0)
        OR EXISTS (
            SELECT 1 FROM recipe_legacy.recipe_ingredient
            WHERE recipe_id IS NULL OR ingredient_id IS NULL OR amount IS NULL OR amount <= 0
                OR unit IS NULL OR unit NOT REGEXP '[^[:space:]]'
        )
    THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Legacy ingredients contain invalid stock, missing IDs, quantities, or units';
    END IF;

    DELETE FROM recipe_ingredient;
    DELETE FROM recipe;
    DELETE FROM ingredient;

    INSERT INTO ingredient (id, name, type, state, amount, unit)
        SELECT id, name, type, state, COALESCE(amount, 0), unit FROM recipe_legacy.ingredient;
    INSERT INTO recipe (id, title, category, directions, description)
        SELECT id, title, category, directions, description FROM recipe_legacy.recipe;
    INSERT INTO recipe_ingredient (recipe_id, ingredient_id, amount, unit)
        SELECT recipe_id, ingredient_id, amount, unit FROM recipe_legacy.recipe_ingredient;

    COMMIT;
END$$
CALL import_recipe_legacy()$$
DROP PROCEDURE import_recipe_legacy$$

DELIMITER ;
