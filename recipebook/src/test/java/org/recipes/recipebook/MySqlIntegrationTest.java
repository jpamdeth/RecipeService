package org.recipes.recipebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.MountableFile;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class MySqlIntegrationTest {

    private static final String SEEDED_RECIPE = "89e5f524-f211-47f6-a293-b89af579c541";
    private static final String CHEESE = "8f6b5018-f840-4d82-8327-53dee9b7fc26";
    private static final String BREAD = "18ffe751-be13-4346-a7e1-0b552589e4e3";

    @Container
    static final MySQLContainer mysql = new MySQLContainer("mysql:8.4")
        .withImagePullPolicy(imageName -> false)
        .withDatabaseName("recipes")
        .withUsername("recipe")
        .withPassword("recipe");

    @Autowired
    MockMvc mvc;

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Test
    void flywayMigratesMySqlAndSeededRecipeCanBeRead() throws Exception {
        mvc.perform(get("/recipes/" + SEEDED_RECIPE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("grilled cheese sandwich"))
            .andExpect(jsonPath("$.ingredients.length()").value(2));
    }

    @Test
    void recipeWritesPersistRequirementsAndPreserveLinksWhenOmitted() throws Exception {
        String ingredientPath = pathFromLocation(mvc.perform(post("/ingredients")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"QA flour","amount":5,"unit":"cups"}
                """))
            .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location"));
        String ingredientId = ingredientPath.substring("/ingredients/".length());
        String recipePath = pathFromLocation(mvc.perform(post("/recipes")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title":"QA pancakes","ingredients":[{"id":"%s","amount":2,"unit":"cups"}]}
                """.formatted(ingredientId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.ingredients[0].amount").value(2))
            .andReturn().getResponse().getHeader("Location"));

        mvc.perform(put(recipePath).contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title":"Renamed pancakes"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ingredients[0].id").value(ingredientId))
            .andExpect(jsonPath("$.ingredients[0].amount").value(2));
        mvc.perform(get(recipePath))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Renamed pancakes"))
            .andExpect(jsonPath("$.ingredients.length()").value(1));

        mvc.perform(post(recipePath + "/ingredients").contentType(MediaType.APPLICATION_JSON)
            .content("""
                [{"ingredientId":"%s","amount":3,"unit":"cups"}]
                """.formatted(ingredientId)))
            .andExpect(status().isOk());
        mvc.perform(post(recipePath + "/make")).andExpect(status().isNoContent());
        mvc.perform(get(ingredientPath)).andExpect(jsonPath("$.amount").value(2));

        mvc.perform(put(ingredientPath).contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"QA flour","amount":7,"unit":"cups"}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ingredientId))
            .andExpect(jsonPath("$.amount").value(7));
        mvc.perform(put(recipePath).contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title":"No ingredients","ingredients":[]}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ingredients.length()").value(0));
        mvc.perform(get(recipePath)).andExpect(jsonPath("$.ingredients.length()").value(0));
    }

    @Test
    void makeRollsBackAlreadyConsumedStockOnConflict() throws Exception {
        String recipePath = pathFromLocation(mvc.perform(post("/recipes").contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title":"QA stock conflict","ingredients":[
                    {"ingredientId":"%s","amount":2,"unit":"slices"},
                    {"ingredientId":"%s","amount":50,"unit":"slices"}
                ]}
                """.formatted(BREAD, CHEESE)))
            .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location"));
        mvc.perform(post(recipePath + "/make")).andExpect(status().isConflict());
        mvc.perform(get("/ingredients/" + BREAD)).andExpect(jsonPath("$.amount").value(12));
    }

    @Test
    void missingAndInvalidWritesReturnClientErrors() throws Exception {
        String missing = UUID.randomUUID().toString();
        mvc.perform(put("/recipes/" + missing).contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"title":"Missing"}
                """)).andExpect(status().isNotFound());
        mvc.perform(put("/ingredients/" + missing).contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name":"Missing","amount":1,"unit":"cups"}
                """)).andExpect(status().isNotFound());
        mvc.perform(post("/recipes/" + SEEDED_RECIPE + "/ingredients").contentType(MediaType.APPLICATION_JSON)
            .content("""
                [{"ingredientId":"%s","amount":0,"unit":"slices"}]
                """.formatted(CHEESE))).andExpect(status().isBadRequest());
        mvc.perform(post("/recipes/" + SEEDED_RECIPE + "/ingredients").contentType(MediaType.APPLICATION_JSON)
            .content("""
                [{"ingredientId":"%s","amount":1,"unit":"cups"}]
                """.formatted(missing))).andExpect(status().isNotFound());
    }

    @Test
    void legacyImportPreservesDataAndRollsBackOnIncompatibleRows() throws Exception {
        mysql.copyFileToContainer(MountableFile.forClasspathResource("legacy-schema.sql"), "/tmp/legacy-schema.sql");
        mysql.copyFileToContainer(MountableFile.forHostPath(Path.of("db/import-legacy.sql")), "/tmp/import-legacy.sql");
        assertMysqlSuccess(mysqlCommand("""
            CREATE DATABASE recipe_legacy;
            CREATE DATABASE recipe_import;
            GRANT ALL ON recipe_import.* TO 'recipe'@'%';
            """));
        assertMysqlSuccess(mysqlScript("recipe_legacy", "/tmp/legacy-schema.sql"));
        String targetUrl = mysql.getJdbcUrl().replace("/recipes?", "/recipe_import?");
        Flyway.configure().dataSource(targetUrl, mysql.getUsername(), mysql.getPassword()).load().migrate();

        assertMysqlSuccess(mysqlCommand("""
            UPDATE recipe_legacy.recipe SET title = 'Family grilled cheese';
            UPDATE recipe_legacy.ingredient SET amount = 8 WHERE id = '%s';
            UPDATE recipe_legacy.recipe_ingredient SET amount = 3 WHERE ingredient_id = '%s';
            DELETE FROM recipe_legacy.recipe_ingredient WHERE ingredient_id = '%s';
            INSERT INTO recipe_legacy.recipe_ingredient
                SELECT * FROM recipe_legacy.recipe_ingredient WHERE ingredient_id = '%s';
            """.formatted(CHEESE, CHEESE, BREAD, CHEESE)));
        ExecResult invalid = mysqlScript("recipe_import", "/tmp/import-legacy.sql");
        assertNotEquals(0, invalid.getExitCode(), invalid.getStdout());
        assertEquals("grilled cheese sandwich", query("SELECT title FROM recipe_import.recipe"));
        assertEquals("2", query("SELECT COUNT(*) FROM recipe_legacy.recipe_ingredient"));

        assertMysqlSuccess(mysqlCommand("DELETE FROM recipe_legacy.recipe_ingredient LIMIT 1"));
        assertMysqlSuccess(mysqlScript("recipe_import", "/tmp/import-legacy.sql"));
        assertEquals("Family grilled cheese", query("SELECT title FROM recipe_import.recipe"));
        assertEquals("8", query("SELECT amount FROM recipe_import.ingredient WHERE id = '" + CHEESE + "'"));
        assertEquals("3", query("SELECT amount FROM recipe_import.recipe_ingredient"));
        assertEquals("1", query("SELECT COUNT(*) FROM recipe_import.flyway_schema_history WHERE version = '1'"));
        assertNotEquals(0, mysqlScript("recipe_import", "/tmp/import-legacy.sql").getExitCode());
    }

    private static String pathFromLocation(String location) {
        return URI.create(location).getPath();
    }

    private static ExecResult mysqlCommand(String sql) throws Exception {
        return mysql.execInContainer("sh", "-c",
            "export MYSQL_PWD=\"$MYSQL_ROOT_PASSWORD\"; exec mysql --user=root --batch --skip-column-names -e \"$1\"",
            "mysql", sql);
    }

    private static ExecResult mysqlScript(String schema, String path) throws Exception {
        return mysql.execInContainer("sh", "-c",
            "export MYSQL_PWD=\"$MYSQL_ROOT_PASSWORD\"; exec mysql --user=root --database=\"$1\" < \"$2\"",
            "mysql", schema, path);
    }

    private static String query(String sql) throws Exception {
        ExecResult result = mysqlCommand(sql);
        assertMysqlSuccess(result);
        return result.getStdout().strip();
    }

    private static void assertMysqlSuccess(ExecResult result) {
        assertEquals(0, result.getExitCode(), result.getStderr());
    }
}
