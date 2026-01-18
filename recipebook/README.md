# RecipeService

## Description
SAMPLE CODE
This repository contains a Spring Boot application that showcases some basic features of the platform including JPA repositories and property driven auto-configuration of DataSources.  Unit tests make use of extensive mocking while the integration tests use an in memory H2 database to exercise the code end to end.  This project uses Lombok to reduce boilerplate code.  See https://projectlombok.org/ for more info.

RecipeService is a service that allows users to manage and share their recipes. It provides functionality for creating, updating, and deleting recipes, as well as searching for recipes based on various criteria.

## Features
- CRUD operations for recipes
- Recipe search functionality

## Requirements
- Java 21 or higher
- Docker and Docker Compose (for containerized deployment)
- Gradle 8.x (wrapper included)

## Quick Start with Docker

The easiest way to run the application is using Docker Compose:

```bash
# Start the application and MySQL database
docker compose up -d

# View logs
docker compose logs -f

# Stop the application
docker compose down
```

The application will be available at:
- **API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Recipes Endpoint**: http://localhost:8080/recipes

## Local Development

### Option 1: Using Docker for Database Only

1. Start only the MySQL container:
   ```bash
   docker compose up -d mysql
   ```

2. Run the application locally:
   ```bash
   ./gradlew bootRun
   ```

### Option 2: Manual Setup

1. Clone the repository: `git clone https://github.com/your-username/RecipeService.git`
2. Run the build: `./gradlew build`
3. Create the required tables using `schema.sql` in your MySQL database
4. Update `application.properties` with your database connection info
5. Run the application: `./gradlew bootRun`

## Building

```bash
# Build the application
./gradlew build

# Build Docker image
docker compose build
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/recipes` | List all recipes |
| GET | `/recipes/{id}` | Get a recipe by ID |
| POST | `/recipes` | Create a new recipe |
| PUT | `/recipes/{id}` | Update a recipe |
| DELETE | `/recipes/{id}` | Delete a recipe |
| GET | `/ingredients` | List all ingredients |

## Configuration

Environment variables for Docker deployment:

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql:3306/recipes` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `recipe` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `recipe` | Database password |

## Contributing
Contributions are welcome! If you'd like to contribute to RecipeService, please follow these steps:
1. Fork the repository.
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Make your changes and commit them: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request.
