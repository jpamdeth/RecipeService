# RecipeService

RecipeService is a Spring Boot REST API for managing recipes, pantry ingredients, and the inventory changes that happen when a recipe is made. It is intentionally small, but built with production-minded habits: layered code, DTO-based API boundaries, validation, database migrations, Docker Compose, automated tests, coverage reports, and OpenAPI documentation.

## What This Demonstrates

- Java 21 and Spring Boot service development
- REST controller design with request/response DTOs
- JPA repositories and transactional business logic
- MySQL runtime persistence with Flyway migrations
- Fast H2-backed integration tests plus one MySQL Testcontainers migration test
- Dockerized local runtime
- GitHub Actions CI and JaCoCo coverage reporting
- Consistent JSON error responses

## Tech Stack

| Area | Technology |
| --- | --- |
| Runtime | Java 21, Spring Boot 3.5 |
| API | Spring MVC, Bean Validation, springdoc-openapi |
| Persistence | Spring Data JPA, MySQL 8, Flyway |
| Tests | JUnit 5, MockMvc, H2, Mockito, Testcontainers, JaCoCo |
| Local runtime | Docker Compose |
| CI | GitHub Actions |

## Quick Start

From this directory:

```bash
cp .env.example .env
docker compose up -d --build
```

The API starts on `http://localhost:8080`.

Useful URLs:

| URL | Description |
| --- | --- |
| `http://localhost:8080/actuator/health` | Health check |
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |
| `http://localhost:8080/recipes` | Recipes API |
| `http://localhost:8080/ingredients` | Ingredients API |

Stop the stack:

```bash
docker compose down
```

Remove the database volume if you want a clean database on the next run:

```bash
docker compose down -v
```

## Local Development

Start only MySQL:

```bash
cp .env.example .env
docker compose up -d mysql
```

Run the app locally:

```bash
./gradlew bootRun
```

Run tests and generate coverage. The suite includes one Docker-backed MySQL integration test; if Docker is unavailable, Testcontainers skips that test. CI pre-pulls `mysql:8.0`; locally, run `docker pull mysql:8.0` once if you do not already have that image.

```bash
./gradlew test
```

The HTML coverage report is written to:

```text
build/reports/jacoco/test/html/index.html
```

## API Examples

Create an ingredient:

```bash
curl -i -X POST http://localhost:8080/ingredients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "cheddar",
    "type": "dairy",
    "state": "refrigerated",
    "amount": 12,
    "unit": "slices"
  }'
```

Create a recipe:

```bash
curl -i -X POST http://localhost:8080/recipes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Grilled cheese sandwich",
    "description": "A simple toasted sandwich",
    "category": "lunch",
    "directions": "Butter bread, add cheese, toast until golden.",
    "ingredients": []
  }'
```

Attach ingredients to a recipe:

```bash
curl -i -X POST http://localhost:8080/recipes/{recipeId}/ingredients \
  -H "Content-Type: application/json" \
  -d '[
    {
      "ingredientId": "{ingredientId}",
      "amount": 2,
      "unit": "slices"
    }
  ]'
```

Make a recipe and decrement pantry stock:

```bash
curl -i -X POST http://localhost:8080/recipes/{recipeId}/make
```

If stock is insufficient or units do not match, the API returns `409 Conflict`.

## API Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/recipes` | List recipes |
| `GET` | `/recipes/{id}` | Get one recipe |
| `POST` | `/recipes` | Create a recipe |
| `PUT` | `/recipes/{id}` | Update a recipe |
| `DELETE` | `/recipes/{id}` | Delete a recipe |
| `POST` | `/recipes/{id}/ingredients` | Attach ingredients to a recipe |
| `POST` | `/recipes/{id}/make` | Make a recipe and decrement ingredient stock |
| `GET` | `/ingredients` | List ingredients |
| `GET` | `/ingredients/{id}` | Get one ingredient |
| `POST` | `/ingredients` | Create an ingredient |
| `PUT` | `/ingredients/{id}` | Update an ingredient |
| `DELETE` | `/ingredients/{id}` | Delete an ingredient |

## Error Format

Errors use Spring's RFC 7807 `ProblemDetail` format. Validation errors include an `errors` extension:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/recipes",
  "errors": [
    "title: must not be blank"
  ]
}
```

## Configuration

Docker Compose reads `.env` values. Start from `.env.example` and change secrets locally.

| Variable | Purpose |
| --- | --- |
| `MYSQL_DATABASE` | MySQL database name |
| `MYSQL_USER` | MySQL application user |
| `MYSQL_PASSWORD` | MySQL application password |
| `MYSQL_ROOT_PASSWORD` | MySQL root password |
| `SPRING_DATASOURCE_URL` | JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | Spring datasource user |
| `SPRING_DATASOURCE_PASSWORD` | Spring datasource password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate schema mode, default `none` |
| `SPRING_FLYWAY_ENABLED` | Enables Flyway migrations, default `true` |

## Database

Flyway migrations live in:

```text
src/main/resources/db/migration
```

The first migration creates the recipe, ingredient, and recipe ingredient tables, including a composite primary key on the join table and seed data for a grilled cheese example.

## Project Structure

```text
src/main/java/org/recipes/recipebook
  controller/   REST controllers and API error handling
  dto/          Request and response contracts
  model/        JPA entities
  repository/   Spring Data repositories
  service/      Business logic and transactions
```

## Verification

The main local verification command is:

```bash
./gradlew test
```

GitHub Actions runs the same test suite on pushes to `main` and on pull requests.
