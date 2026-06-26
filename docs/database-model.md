# FoodBook – Database Model

Database: **PostgreSQL 16**  
Managed by: **Flyway** (migration scripts in `backend/src/main/resources/db/migration/`)

---

## Entity-Relationship Overview

```
users ──────────────── recipes (author_id)
  │                       │
  │                     recipe_ingredients ── ingredients
  │                       │
  │                     recipe_steps
  │                       │
  ├── follows             recipe_comments ── users
  │   (follower_id,       │
  │    followed_id)     recipe_likes ── users
  │
  └── saved_recipes ── recipes
```

---

## Tables

### 1. `users`

Stores registered accounts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `BIGSERIAL` | PK | Auto-incrementing surrogate key |
| `username` | `VARCHAR(50)` | NOT NULL, UNIQUE | Display handle |
| `email` | `VARCHAR(255)` | NOT NULL, UNIQUE | Login identifier |
| `password_hash` | `VARCHAR(255)` | NOT NULL | BCrypt hash |
| `full_name` | `VARCHAR(100)` | NOT NULL | User's full name |
| `bio` | `TEXT` | | Short profile biography |
| `avatar_url` | `VARCHAR(500)` | | Profile picture URL |
| `role` | `VARCHAR(20)` | NOT NULL, DEFAULT `'USER'` | `USER` or `ADMIN` |
| `active` | `BOOLEAN` | NOT NULL, DEFAULT `true` | Soft-delete flag |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | Account creation time |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | Last update time |

**Indexes:** `idx_users_email` (UNIQUE), `idx_users_username` (UNIQUE)

---

### 2. `recipes`

Core recipe entity.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `BIGSERIAL` | PK | Auto-incrementing surrogate key |
| `author_id` | `BIGINT` | NOT NULL, FK → `users.id` | Recipe owner |
| `title` | `VARCHAR(200)` | NOT NULL | Recipe title |
| `description` | `TEXT` | | Short description |
| `prep_time_minutes` | `INT` | | Preparation time |
| `cook_time_minutes` | `INT` | | Cooking time |
| `servings` | `INT` | | Number of servings |
| `difficulty` | `VARCHAR(20)` | | `EASY`, `MEDIUM`, `HARD` |
| `cuisine_type` | `VARCHAR(50)` | | e.g. `Italian`, `Brazilian` |
| `cover_image_url` | `VARCHAR(500)` | | Main photo URL |
| `is_published` | `BOOLEAN` | NOT NULL, DEFAULT `false` | Visibility flag |
| `spoonacular_id` | `INT` | UNIQUE | External ID if imported |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |

**Indexes:** `idx_recipes_author_id`, `idx_recipes_is_published`, `idx_recipes_cuisine_type`  
**Full-text search index:** `idx_recipes_title_fts` on `to_tsvector('portuguese', title)`

---

### 3. `ingredients`

Canonical ingredient catalog (shared across recipes).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `BIGSERIAL` | PK | |
| `name` | `VARCHAR(100)` | NOT NULL, UNIQUE | Ingredient name |
| `spoonacular_id` | `INT` | UNIQUE | External reference |
| `image_url` | `VARCHAR(500)` | | |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |

**Indexes:** `idx_ingredients_name` (UNIQUE)

---

### 4. `recipe_ingredients`

Many-to-many join between recipes and ingredients, with quantity details.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `BIGSERIAL` | PK | |
| `recipe_id` | `BIGINT` | NOT NULL, FK → `recipes.id` ON DELETE CASCADE | |
| `ingredient_id` | `BIGINT` | NOT NULL, FK → `ingredients.id` | |
| `quantity` | `NUMERIC(10,2)` | | Numeric amount |
| `unit` | `VARCHAR(30)` | | `g`, `ml`, `cup`, `tbsp`, etc. |
| `notes` | `VARCHAR(200)` | | e.g. "finely chopped" |
| `order_index` | `INT` | NOT NULL, DEFAULT `0` | Display order |

**Indexes:** `idx_ri_recipe_id`, `idx_ri_ingredient_id`  
**Constraint:** UNIQUE(`recipe_id`, `ingredient_id`)

---

### 5. `recipe_steps`

Ordered preparation steps for a recipe.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `BIGSERIAL` | PK | |
| `recipe_id` | `BIGINT` | NOT NULL, FK → `recipes.id` ON DELETE CASCADE | |
| `step_number` | `INT` | NOT NULL | Step order (1-based) |
| `description` | `TEXT` | NOT NULL | Instruction text |
| `image_url` | `VARCHAR(500)` | | Optional step photo |
| `duration_minutes` | `INT` | | Estimated step duration |

**Indexes:** `idx_steps_recipe_id`  
**Constraint:** UNIQUE(`recipe_id`, `step_number`)

---

### 6. `recipe_comments`

User comments on recipes.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `BIGSERIAL` | PK | |
| `recipe_id` | `BIGINT` | NOT NULL, FK → `recipes.id` ON DELETE CASCADE | |
| `user_id` | `BIGINT` | NOT NULL, FK → `users.id` ON DELETE CASCADE | |
| `content` | `TEXT` | NOT NULL | Comment body |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |

**Indexes:** `idx_comments_recipe_id`, `idx_comments_user_id`

---

### 7. `recipe_likes`

Likes (hearts) on recipes — one per user per recipe.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `recipe_id` | `BIGINT` | NOT NULL, FK → `recipes.id` ON DELETE CASCADE | |
| `user_id` | `BIGINT` | NOT NULL, FK → `users.id` ON DELETE CASCADE | |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |

**PK:** (`recipe_id`, `user_id`)  
**Indexes:** `idx_likes_recipe_id`, `idx_likes_user_id`

---

### 8. `saved_recipes`

User bookmarks / saved recipes collection.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `user_id` | `BIGINT` | NOT NULL, FK → `users.id` ON DELETE CASCADE | |
| `recipe_id` | `BIGINT` | NOT NULL, FK → `recipes.id` ON DELETE CASCADE | |
| `saved_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |

**PK:** (`user_id`, `recipe_id`)

---

### 9. `follows`

Social graph — follower/following relationship.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `follower_id` | `BIGINT` | NOT NULL, FK → `users.id` ON DELETE CASCADE | The user who follows |
| `followed_id` | `BIGINT` | NOT NULL, FK → `users.id` ON DELETE CASCADE | The user being followed |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |

**PK:** (`follower_id`, `followed_id`)  
**Constraint:** CHECK(`follower_id <> followed_id`) — users cannot follow themselves  
**Indexes:** `idx_follows_follower_id`, `idx_follows_followed_id`

---

### 10. `refresh_tokens`

Persistent refresh token store (enables token revocation).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | `BIGSERIAL` | PK | |
| `user_id` | `BIGINT` | NOT NULL, FK → `users.id` ON DELETE CASCADE | |
| `token` | `VARCHAR(500)` | NOT NULL, UNIQUE | Opaque token value |
| `expires_at` | `TIMESTAMPTZ` | NOT NULL | Expiry timestamp |
| `revoked` | `BOOLEAN` | NOT NULL, DEFAULT `false` | Revocation flag |
| `created_at` | `TIMESTAMPTZ` | NOT NULL, DEFAULT `now()` | |

**Indexes:** `idx_refresh_tokens_token` (UNIQUE), `idx_refresh_tokens_user_id`

---

## Naming Conventions

- All table and column names: **snake_case**.
- Primary keys: always `id BIGSERIAL`.
- Timestamps: always `TIMESTAMPTZ` (timezone-aware).
- Foreign keys: `<referenced_table_singular>_id`.
- Boolean flags: positive wording (`is_published`, `active`, `revoked`).
- All `created_at` / `updated_at` columns default to `now()`.

---

## Cascade Rules

| Relationship | On Delete |
|-------------|-----------|
| `recipes.author_id → users.id` | RESTRICT (prevent deleting users who have recipes) |
| `recipe_ingredients.recipe_id → recipes.id` | CASCADE |
| `recipe_steps.recipe_id → recipes.id` | CASCADE |
| `recipe_comments → recipes / users` | CASCADE |
| `recipe_likes → recipes / users` | CASCADE |
| `saved_recipes → users / recipes` | CASCADE |
| `follows → users` | CASCADE |
| `refresh_tokens.user_id → users.id` | CASCADE |
