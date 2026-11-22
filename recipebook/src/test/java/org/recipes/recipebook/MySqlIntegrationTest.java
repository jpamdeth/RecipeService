package org.recipes.recipebook;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class MySqlIntegrationTest {

    private static final String SEEDED_RECIPE = "89e5f524-f211-47f6-a293-b89af579c541";
    private static final String CHEESE = "8f6b5018-f840-4d82-8327-53dee9b7fc26";
    private static final String BREAD = "18ffe751-be13-4346-a7e1-0b552589e4e3";

    @Container
    static final MySQLContainer mysql = new MySQLContainer("mysql:9.7")
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
        mvc.perform(delete(ingredientPath)).andExpect(status().isNoContent());
        mvc.perform(get(ingredientPath)).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {CHEESE, "ab7ff905-2db5-48fc-be4a-504f99dd7391"})
    void ingredientCreationRejectsClientIds(String id) throws Exception {
        mvc.perform(post("/ingredients").contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"id":"%s","name":"Replacement cheese","amount":1,"unit":"slices"}
                """.formatted(id)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("Omit the id when creating an ingredient. The server assigns it."));
        mvc.perform(get("/ingredients/" + CHEESE))
            .andExpect(jsonPath("$.name").value("cheese"))
            .andExpect(jsonPath("$.amount").value(12));
    }

    @Test
    void deletingAReferencedIngredientReturnsConflictAndKeepsTheLink() throws Exception {
        mvc.perform(delete("/ingredients/" + CHEESE))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("Remove this ingredient from its recipes before deleting it."));
        mvc.perform(get("/recipes/" + SEEDED_RECIPE))
            .andExpect(jsonPath("$.ingredients.length()").value(2));
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

    private static String pathFromLocation(String location) {
        return URI.create(location).getPath();
    }
}
