# Portfolio Modernization Design

## Goal

Modernize RecipeService so it reads as a maintained, professional Spring Boot portfolio API: easy to clone, easy to run, documented, tested, and clear about its production-minded choices.

## Scope

The update covers documentation, API contracts, error responses, database migrations, OpenAPI documentation, CI, coverage, and configuration hardening. The service remains a compact recipe and pantry API; this work does not add authentication, a frontend, or a large domain expansion.

## Architecture

Controllers expose DTOs instead of JPA entities. Services continue to own business behavior and repository access. A global exception handler turns validation, ID mismatch, missing resources, and stock conflicts into consistent JSON errors.

Flyway owns schema creation through versioned migrations. The Docker Compose MySQL service provides the database only; the application applies migrations during startup. H2 remains the fast test database.

## API Behavior

Create endpoints return `201 Created` with a `Location` header. Read endpoints return DTO JSON. Validation failures return `400 Bad Request`. Missing recipes and ingredients return `404 Not Found`. Insufficient stock during recipe preparation returns `409 Conflict`.

## Documentation

The README explains the purpose of the project, the stack, quick start steps, API examples, test commands, Docker usage, and portfolio highlights. Generated starter documentation is removed. OpenAPI is available through Swagger UI when the app is running.

## Testing

Tests cover successful controller flows, validation failures, created responses, ID mismatch behavior, missing resources, and recipe stock conflicts. Integration tests use JUnit assertions. JaCoCo produces coverage reports during verification.

## CI

GitHub Actions runs the Gradle test suite with Java 21 and publishes the JaCoCo report artifact. This gives reviewers a visible signal that the project is maintained and automatically verified.
