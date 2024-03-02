CREATE TABLE 
    ingredient 
    ( 
        id     VARCHAR(36) NOT NULL, 
        name   VARCHAR(50) NOT NULL, 
        type   VARCHAR(50), 
        state  VARCHAR(50), 
        amount INT, 
        unit   VARCHAR(25), 
        PRIMARY KEY (id) 
    ) 
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE 
    recipe 
    ( 
        id          VARCHAR(36) NOT NULL, 
        title       VARCHAR(100) NOT NULL, 
        category    VARCHAR(100), 
        directions  VARCHAR(2000), 
        description VARCHAR(255), 
        PRIMARY KEY (id) 
    ) 
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE 
    recipe_ingredient 
    ( 
        recipe_id     VARCHAR(36), 
        ingredient_id VARCHAR(36), 
        amount        INT, 
        unit          VARCHAR(25), 
        FOREIGN KEY (recipe_id) REFERENCES `recipe` (`id`) , 
        FOREIGN KEY (ingredient_id) REFERENCES `ingredient` (`id`), 
        INDEX recipe_id (recipe_id), 
        INDEX ingredient_id (ingredient_id) 
    ) 
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_0900_ai_ci;

insert into ingredient values ("8f6b5018-f840-4d82-8327-53dee9b7fc26", "cheese", "dairy", "refrigerated", 12, "slices");
insert into ingredient values ("18ffe751-be13-4346-a7e1-0b552589e4e3", "bread", "baked goods", "shelf stable", 12, "slices");
insert into recipe values ("89e5f524-f211-47f6-a293-b89af579c541", "grilled cheese sandwich", "lunch", "butter and toast bread with cheese inside", "description");
insert into recipe_ingredient values ("89e5f524-f211-47f6-a293-b89af579c541", "8f6b5018-f840-4d82-8327-53dee9b7fc26", 2, "slices");
insert into recipe_ingredient values ("89e5f524-f211-47f6-a293-b89af579c541", "18ffe751-be13-4346-a7e1-0b552589e4e3", 2, "slices");