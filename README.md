# FoodBook

A social network for culinary recipes — share, discover, and save your favourite dishes.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java (JDK) | 21 |
| Maven | 3.9+ (or use `./mvnw`) |
| PostgreSQL | 16 |
| Android Studio | Hedgehog 2023.1.1+ |

---

## Quick Start

### 1. Database

```bash
# Create the database and user (psql)
psql -U postgres -c "CREATE DATABASE foodbook;"
psql -U postgres -c "CREATE USER foodbook_user WITH PASSWORD 'foodbook_pass';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE foodbook TO foodbook_user;"
```

### 2. Environment variables

```bash
# Linux / macOS
export DB_USERNAME=foodbook_user
export DB_PASSWORD=foodbook_pass
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
export SPOONACULAR_API_KEY=your_key_here

# Windows (PowerShell)
$env:DB_USERNAME="foodbook_user"
$env:DB_PASSWORD="foodbook_pass"
$env:JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
$env:SPOONACULAR_API_KEY="your_key_here"
```

### 3. Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI spec: `http://localhost:8080/api-docs`

### 4. Android app

Open the `android/` directory in **Android Studio**, sync Gradle, and run on an emulator or device.

---

## Tech Stack

### Backend

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.3.x |
| Language | Java 21 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Security | Spring Security + JWT (jjwt 0.12.x) |
| Validation | Jakarta Bean Validation |
| Mapping | MapStruct |
| Boilerplate | Lombok |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |

### Android

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Architecture | MVVM + Clean Architecture |
| UI | Jetpack Compose |
| DI | Hilt |
| Networking | Retrofit 2 + OkHttp |
| Images | Coil |
| Navigation | Navigation Component |

---

## Project Structure

```
FoodWorks/
├── .gitignore
├── README.md
├── backend/                        # Spring Boot API
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/foodbook/
│       │   │   ├── FoodbookApiApplication.java
│       │   │   ├── config/         # Spring configuration beans
│       │   │   ├── controller/     # REST controllers
│       │   │   ├── dto/
│       │   │   │   ├── request/    # Incoming payloads
│       │   │   │   └── response/   # Outgoing payloads
│       │   │   ├── entity/         # JPA entities
│       │   │   ├── exception/      # GlobalExceptionHandler + custom exceptions
│       │   │   ├── integration/    # External API clients (Spoonacular, etc.)
│       │   │   ├── mapper/         # MapStruct mappers
│       │   │   ├── repository/     # Spring Data repositories
│       │   │   ├── security/       # JWT filters, UserDetails, SecurityConfig
│       │   │   ├── service/        # Business logic
│       │   │   ├── util/           # Utility classes
│       │   │   └── validation/     # Custom validators
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       └── db/migration/   # Flyway SQL scripts
│       └── test/
│           ├── java/com/foodbook/
│           └── resources/
│               └── application-test.yml
├── android/                        # Android application (future)
└── docs/                           # Project documentation
    ├── README.md
    ├── architecture.md
    ├── database-model.md
    ├── api-endpoints.md
    └── setup-guide.md
```

---

## Documentation

See the [`docs/`](docs/README.md) directory for detailed documentation:

- [Architecture](docs/architecture.md)
- [Database Model](docs/database-model.md)
- [API Endpoints](docs/api-endpoints.md)
- [Setup Guide](docs/setup-guide.md)

---

## License

MIT
