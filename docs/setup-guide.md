# FoodBook – Setup Guide

This guide walks you through setting up the full development environment from scratch.

---

## 1. Prerequisites

Install the following tools before proceeding:

| Tool | Version | Download |
|------|---------|---------|
| JDK | 21 | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org/download.cgi (or use `./mvnw`) |
| PostgreSQL | 16 | https://www.postgresql.org/download |
| Android Studio | Hedgehog 2023.1.1+ | https://developer.android.com/studio |
| Git | Latest | https://git-scm.com |

Verify your Java installation:

```bash
java -version
# Expected: openjdk version "21.x.x"
```

---

## 2. Clone the Repository

```bash
git clone https://github.com/your-org/foodworks.git
cd foodworks
```

---

## 3. PostgreSQL Setup

### 3.1 Start PostgreSQL

**Windows (Service):**
```powershell
Start-Service postgresql-x64-16
```

**macOS (Homebrew):**
```bash
brew services start postgresql@16
```

**Linux (systemd):**
```bash
sudo systemctl start postgresql
```

### 3.2 Create the database and user

Connect as the `postgres` superuser:

```bash
psql -U postgres
```

Run the following SQL:

```sql
-- Create dedicated database
CREATE DATABASE foodbook;

-- Create application user
CREATE USER foodbook_user WITH PASSWORD 'foodbook_pass';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE foodbook TO foodbook_user;

-- (PostgreSQL 15+) Also grant schema privileges
\c foodbook
GRANT ALL ON SCHEMA public TO foodbook_user;

\q
```

Verify the connection:

```bash
psql -U foodbook_user -d foodbook -c "SELECT version();"
```

---

## 4. Environment Variables

The backend reads configuration from environment variables. Set them before running:

### Windows (PowerShell – current session)

```powershell
$env:DB_USERNAME = "foodbook_user"
$env:DB_PASSWORD = "foodbook_pass"
$env:JWT_SECRET  = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
$env:SPOONACULAR_API_KEY = "your_spoonacular_key_here"
```

To persist across sessions, add them to your user environment:

```powershell
[System.Environment]::SetEnvironmentVariable("DB_USERNAME", "foodbook_user", "User")
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD", "foodbook_pass", "User")
[System.Environment]::SetEnvironmentVariable("JWT_SECRET", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970", "User")
[System.Environment]::SetEnvironmentVariable("SPOONACULAR_API_KEY", "demo", "User")
```

### macOS / Linux

```bash
export DB_USERNAME=foodbook_user
export DB_PASSWORD=foodbook_pass
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
export SPOONACULAR_API_KEY=your_spoonacular_key_here
```

Add these lines to `~/.bashrc` or `~/.zshrc` to persist them.

> **Note:** Never commit actual secrets to Git. The `.gitignore` already excludes `.env` and `application-local.yml` files.

---

## 5. Get a Spoonacular API Key (optional)

The backend works with `SPOONACULAR_API_KEY=demo` for basic testing. For full integration:

1. Register at https://spoonacular.com/food-api
2. Go to your profile → **API Console** → copy your API key.
3. Set the `SPOONACULAR_API_KEY` environment variable.

---

## 6. Run the Backend

```bash
cd backend

# Option A: Maven Wrapper (recommended, no local Maven needed)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Option B: Local Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Windows PowerShell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

On first run, **Flyway** will apply all migration scripts automatically.

### Verify it started

```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

---

## 7. Open Swagger UI

Navigate to: **http://localhost:8080/swagger-ui.html**

From here you can:

- Browse all API endpoints
- Try requests directly in the browser
- Authenticate by clicking **Authorize** and pasting a Bearer token

OpenAPI spec (JSON): **http://localhost:8080/api-docs**

---

## 8. Run Tests

```bash
cd backend
./mvnw test
```

The test suite uses an H2 in-memory database (Flyway is disabled in the `test` profile), so no PostgreSQL connection is needed for tests.

---

## 9. Android App Setup

1. Open **Android Studio**.
2. Select **Open** and navigate to the `android/` directory.
3. Wait for Gradle sync to complete.
4. In `android/app/src/main/res/values/config.xml` (or the equivalent config file), update the base URL to point to your backend:
   ```xml
   <string name="base_url">http://10.0.2.2:8080/</string>
   ```
   (`10.0.2.2` is the Android emulator's alias for `localhost` on the host machine.)
5. Run the app on an emulator or physical device.

---

## 10. Common Issues

### Port 8080 already in use

```bash
# Find the process
netstat -ano | findstr :8080    # Windows
lsof -i :8080                   # macOS/Linux

# Kill it, or change the port in application.yml:
# server:
#   port: 8081
```

### Flyway migration fails

- Ensure the database `foodbook` exists and the user has the correct privileges (step 3.2).
- Check that `DB_USERNAME` and `DB_PASSWORD` environment variables are set correctly.
- Inspect Flyway's history table: `SELECT * FROM flyway_schema_history;`

### `java.net.ConnectException` on startup

- PostgreSQL is not running. Start it (step 3.1).
- Check that the host/port in `application.yml` matches your PostgreSQL setup.

### JWT errors in tests

- Ensure the test profile is active: `@ActiveProfiles("test")`.
- The test `application-test.yml` provides a hard-coded secret key so no env var is needed.
