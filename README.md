# InventoryManagerBE

This is a Spring Boot application for managing inventory products with REST API endpoints and Swagger documentation.

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Java 21** or higher
- **Maven 3.6+** (or use the included Maven wrapper)
- **Git** for cloning the repository

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/egvr2002/InventoryManagerBE
cd inventory-manager-be
```

### Run the Application

You can run the project using either the Maven wrapper (recommended) or your local Maven installation:

#### Using Maven Wrapper

```bash
# On macOS/Linux
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

#### Using Local Maven

```bash
mvn spring-boot:run
```

The application will start on **port 9090** by default.

### Documentation

API documentation could be located in the following links:

- **API Documentation (Swagger UI)**: http://localhost:9090/swagger-ui.html
- **API Docs (OpenAPI JSON)**: http://localhost:9090/docs

## Running Tests

### Run All Tests

#### Using Maven Wrapper

```bash
# On macOS/Linux
./mvnw test

# On Windows
mvnw.cmd test
```

#### Using Local Maven

```bash
mvn test
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── inc/encora/inventory_manager/
│   │       ├── InventoryManagerApplication.java
│   │       ├── common/           # Shared utilities and configuration
│   │       └── product/          # Product-related functionality
│   └── resources/
│       ├── application.properties
│       ├── static/
│       └── templates/
└── test/
    └── java/
        └── inc/encora/inventory_manager/
            ├── InventoryManagerApplicationTests.java
            └── product/          # Product-related tests
```

### Technologies

- **Spring Boot 3.5.0**
- **Java 21**
- **Maven** for dependency management
- **Lombok** for reducing boilerplate code
- **Spring Boot DevTools** for development
- **Swagger/OpenAPI** for API documentation
