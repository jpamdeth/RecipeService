CREATE TABLE ingredient (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    state VARCHAR(50),
    amount INT NOT NULL DEFAULT 0,
    unit VARCHAR(25),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE recipe (
    id VARCHAR(36) NOT NULL,
    title VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    directions VARCHAR(2000),
    description VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE recipe_ingredient (
    recipe_id VARCHAR(36) NOT NULL,
    ingredient_id VARCHAR(36) NOT NULL,
    amount INT NOT NULL,
    unit VARCHAR(25) NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_id),
    CONSTRAINT fk_recipe_ingredient_recipe
        FOREIGN KEY (recipe_id) REFERENCES recipe (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_recipe_ingredient_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredient (id),
    INDEX idx_recipe_ingredient_recipe_id (recipe_id),
    INDEX idx_recipe_ingredient_ingredient_id (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO ingredient (id, name, type, state, amount, unit)
VALUES
    ('8f6b5018-f840-4d82-8327-53dee9b7fc26', 'cheese', 'dairy', 'refrigerated', 12, 'slices'),
    ('18ffe751-be13-4346-a7e1-0b552589e4e3', 'bread', 'baked goods', 'shelf stable', 12, 'slices');

INSERT INTO recipe (id, title, category, directions, description)
VALUES (
    '89e5f524-f211-47f6-a293-b89af579c541',
    'grilled cheese sandwich',
    'lunch',
    'butter and toast bread with cheese inside',
    'description'
);

INSERT INTO recipe_ingredient (recipe_id, ingredient_id, amount, unit)
VALUES
    ('89e5f524-f211-47f6-a293-b89af579c541', '8f6b5018-f840-4d82-8327-53dee9b7fc26', 2, 'slices'),
    ('89e5f524-f211-47f6-a293-b89af579c541', '18ffe751-be13-4346-a7e1-0b552589e4e3', 2, 'slices');
