# RecipeService

## Description
SAMPLE CODE
This repository contains a Spring Boot application that showcases some basic features of the platform including JPA repositories and property driven auto-configuration of DataSources.  Unit tests make use of extensive mocking while the integration tests use an in memory H2 database to exercise the code end to end.  This project uses Lombok to reduce boilerplate code.  See https://projectlombok.org/ for more info.

RecipeService is a service that allows users to manage and share their recipes. It provides functionality for creating, updating, and deleting recipes, as well as searching for recipes based on various criteria.

## Features
- CRUD operations for recipes
- Recipe search functionality

## Installation
1. Clone the repository: `git clone https://github.com/your-username/RecipeService.git`
2. Run the build using `gradle build`
3. Use schema.sql to create the required tables in your database server.
4. Update application.properties with your DB info


## Contributing
Contributions are welcome! If you'd like to contribute to RecipeService, please follow these steps:
1. Fork the repository.
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Make your changes and commit them: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request.
