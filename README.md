# Simple Library Management System

A Spring Boot REST API for managing books and their copies, with Thymeleaf UI and Swagger documentation.

## Requirements

- Java 25
- Maven 3.9+ (or use the included Maven Wrapper)
- PostgreSQL (or any PostgreSQL-compatible database, e.g. CockroachDB)

## Database configuration

Before running the application, configure your database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/library_management
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## How to run

### 1. Clone the repository

```bash
git clone https://github.com/UZG1/simple-library-management-system.git
cd simple-library-management-system
```

### 2. Configure the database

Update `application.properties` with your database settings.

Make sure the database exists before starting the application.

### 3. Start the application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

### 4. Load sample data (optional)

To inject sample books and copies on startup, activate the `sample-data` profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=sample-data
```

When `sample-data` is active, the application loads demo data only if the database is empty:

- **Clean Code** — Robert C. Martin (2 copies: one available, one checked out)
- **Effective Java** — Joshua Bloch (1 available copy)
- **The Pragmatic Programmer** — David Thomas (1 available copy)

## Application URLs

All paths are served under the `/api` context path.

| Resource | URL |
|---|---|
| Web UI (books list) | http://localhost:8080/api/ |
| REST API | http://localhost:8080/api/books |
| Swagger UI | http://localhost:8080/api/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api/v3/api-docs |

## API overview

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/books` | List books (paginated, supports sorting) |
| `POST` | `/api/books` | Create a book |
| `GET` | `/api/books/{id}` | Get a book with its copies |
| `PUT` | `/api/books/{id}` | Update a book |
| `DELETE` | `/api/books/{id}` | Delete a book |
| `GET` | `/api/books/{id}/copies` | Get available copies for a book |
| `POST` | `/api/books/{id}/copies` | Add a copy for a book |
| `PUT` | `/api/books/{id}/copies/{copyId}` | Update copy availability |

Pagination example:

```
GET /api/books?page=0&size=10&sort=title,asc
```

## Running tests

Tests use an in-memory H2 database and do not require a running PostgreSQL instance:

```bash
./mvnw test
```
