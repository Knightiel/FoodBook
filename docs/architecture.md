# FoodBook – Architecture

## Overview

FoodBook is split into two main components:

| Component | Technology | Responsibility |
|-----------|-----------|---------------|
| **Backend API** | Spring Boot 3.3 / Java 21 | Business logic, persistence, JWT auth, external integrations |
| **Android App** | Kotlin / Jetpack Compose | Mobile UI, MVVM, offline cache |

---

## Backend – Layered Architecture

```
HTTP Request
    │
    ▼
┌─────────────────────────────────┐
│         Controller Layer        │  @RestController – receives HTTP, delegates to service
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│          Service Layer          │  @Service – orchestrates business rules, calls repositories
└────────────────┬────────────────┘
                 │
        ┌────────┴────────┐
        ▼                 ▼
┌──────────────┐  ┌──────────────────┐
│  Repository  │  │  Integration     │
│  Layer       │  │  Layer           │
│  (JPA/SQL)   │  │  (Spoonacular,   │
└──────┬───────┘  │   external APIs) │
       │          └──────────────────┘
       ▼
┌─────────────────────────────────┐
│         PostgreSQL 16           │
└─────────────────────────────────┘
```

### Package responsibilities

| Package | Role |
|---------|------|
| `controller` | REST endpoints, request mapping, input validation delegation |
| `service` | Use-case implementation, transaction boundaries (`@Transactional`) |
| `repository` | Spring Data JPA interfaces; complex queries via JPQL/native SQL |
| `entity` | JPA-mapped domain objects |
| `dto/request` | Incoming payloads (validated with Bean Validation) |
| `dto/response` | Outgoing payloads (never expose entities directly) |
| `mapper` | MapStruct interfaces to convert entity ↔ DTO |
| `config` | Spring `@Configuration` beans (Security, Swagger, CORS, etc.) |
| `security` | JWT filter, `UserDetailsService`, `SecurityFilterChain` |
| `integration` | HTTP clients for external APIs (Spoonacular) |
| `exception` | `GlobalExceptionHandler`, `BusinessException` |
| `util` | Stateless utility helpers |
| `validation` | Custom `@Constraint` validators |

---

## Security – JWT Flow

```
Client                       Backend
  │                             │
  │── POST /api/auth/login ───►│
  │   {email, password}         │  1. Authenticate with Spring Security
  │                             │  2. Generate access token (15 min)
  │◄── {accessToken,            │  3. Generate refresh token (7 days)
  │     refreshToken} ─────────│
  │                             │
  │── GET /api/recipes ────────►│
  │   Authorization: Bearer ... │  4. JwtAuthFilter extracts token
  │                             │  5. Validate signature + expiry
  │◄── 200 OK ─────────────────│  6. Set SecurityContext, proceed
  │                             │
  │── POST /api/auth/refresh ──►│
  │   {refreshToken}            │  7. Validate refresh token
  │◄── {newAccessToken} ────────│  8. Issue new access token
```

### Token storage

- Access token: in-memory on Android (not persisted to disk).
- Refresh token: `EncryptedSharedPreferences` on Android.

---

## Android – MVVM + Clean Architecture

```
┌─────────────────────────────────────┐
│              UI Layer               │
│  Composables  ◄──  ViewModel        │
│               (StateFlow / UiState) │
└────────────────────┬────────────────┘
                     │ calls
                     ▼
┌─────────────────────────────────────┐
│            Domain Layer             │
│          Use Cases (optional)       │
└────────────────────┬────────────────┘
                     │ calls
                     ▼
┌─────────────────────────────────────┐
│             Data Layer              │
│   Repository  ◄──  RemoteDataSource │  Retrofit → Backend API
│               ◄──  LocalDataSource  │  Room → SQLite cache
└─────────────────────────────────────┘
```

---

## Integration Layer Pattern

External API calls (Spoonacular, future integrations) follow a consistent pattern:

1. A `*Client` interface declares the operations.
2. A `*ClientImpl` uses `RestTemplate` / `WebClient` and handles HTTP errors.
3. A `*Service` in the service layer owns the business logic around those calls.
4. API keys are injected via `@Value("${application.integration.*.api-key}")`.
5. Errors from external APIs are wrapped in `BusinessException` to propagate cleanly to the client.

---

## Key Architectural Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| ORM | Hibernate via Spring Data JPA | Productivity, JPQL support, mature ecosystem |
| Migrations | Flyway | SQL-first, deterministic, version-controlled schema |
| Auth | Stateless JWT | No server-side session; scales horizontally |
| DTO mapping | MapStruct | Compile-time, null-safe, no reflection overhead |
| API docs | SpringDoc (Swagger UI) | Auto-generated from annotations, interactive console |
| Password hashing | BCrypt via Spring Security | Industry standard, adaptive cost factor |
| Error handling | Single `@RestControllerAdvice` | Consistent error envelope across all endpoints |
