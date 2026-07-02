# Portfolio Modernization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Modernize RecipeService into a polished Spring Boot portfolio API with documented contracts, migrations, CI, and stronger tests.

**Architecture:** Keep the existing layered Spring Boot structure. Add DTOs at the controller boundary, a centralized error handler, Flyway migrations, OpenAPI metadata, and CI/coverage support without expanding the domain.

**Tech Stack:** Java 21, Spring Boot 3.5, Spring MVC, Spring Data JPA, Flyway, MySQL, H2, Gradle, JaCoCo, GitHub Actions, springdoc-openapi.

---

### Task 1: API Contract And Error Handling

**Files:**
- Create: `src/main/java/org/recipes/recipebook/dto/IngredientRequest.java`
- Create: `src/main/java/org/recipes/recipebook/dto/IngredientResponse.java`
- Create: `src/main/java/org/recipes/recipebook/dto/RecipeRequest.java`
- Create: `src/main/java/org/recipes/recipebook/dto/RecipeResponse.java`
- Create: `src/main/java/org/recipes/recipebook/dto/RecipeIngredientRequest.java`
- Create: `src/main/java/org/recipes/recipebook/dto/ApiError.java`
- Create: `src/main/java/org/recipes/recipebook/controller/ApiExceptionHandler.java`
- Modify: `src/main/java/org/recipes/recipebook/controller/RecipeController.java`
- Modify: `src/main/java/org/recipes/recipebook/controller/IngredientController.java`
- Modify: `src/main/java/org/recipes/recipebook/service/RecipeService.java`
- Modify: `src/main/java/org/recipes/recipebook/service/IngredientService.java`
- Test: `src/test/java/org/recipes/recipebook/controller/RecipeControllerTest.java`
- Test: `src/test/java/org/recipes/recipebook/controller/IngredientControllerTest.java`

- [ ] Add failing controller tests for `201 Created`, validation errors, ID mismatch errors, and conflict errors.
- [ ] Add DTO records and mapper helpers in controllers.
- [ ] Add `ApiExceptionHandler` with consistent JSON errors.
- [ ] Update controllers to return `ResponseEntity` and DTOs.
- [ ] Run `./gradlew.bat test` and confirm the API tests pass.

### Task 2: Migrations And Configuration

**Files:**
- Create: `src/main/resources/db/migration/V1__create_recipe_schema.sql`
- Modify: `build.gradle`
- Modify: `compose.yaml`
- Modify: `src/main/resources/application.properties`
- Modify: `src/test/resources/application-test.properties`

- [ ] Add Flyway and springdoc dependencies.
- [ ] Move schema creation and seed data into a Flyway migration with primary keys and non-null join columns.
- [ ] Remove the direct `schema.sql` compose mount.
- [ ] Make actuator health detail visibility environment/profile controlled.
- [ ] Disable Flyway in fast H2 tests unless a test explicitly enables migrations.
- [ ] Run `./gradlew.bat test`.

### Task 3: Test And Quality Polish

**Files:**
- Modify: `build.gradle`
- Modify: `src/test/java/org/recipes/recipebook/IntegrationTest.java`
- Create: `.github/workflows/ci.yml`

- [ ] Add JaCoCo reporting.
- [ ] Replace Java `assert` usage with JUnit assertions.
- [ ] Add GitHub Actions CI for Java 21, Gradle tests, and coverage artifact upload.
- [ ] Run `./gradlew.bat test jacocoTestReport`.

### Task 4: Portfolio Documentation

**Files:**
- Modify: `README.md`
- Delete: `HELP.md`

- [ ] Rewrite the README with purpose, stack, quick start, API examples, testing, Docker, OpenAPI, and project highlights.
- [ ] Remove generated starter help content.
- [ ] Run a final full verification command.
