# FoodBook – API Endpoints

Base URL: `http://localhost:8080`  
Auth: Bearer JWT in `Authorization` header (except public endpoints marked with `PUBLIC`).  
Content-Type: `application/json`

Interactive docs: `http://localhost:8080/swagger-ui.html`

---

## Authentication

### POST /api/auth/register `PUBLIC`

Register a new user account.

**Request:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Str0ng!Pass",
  "fullName": "John Doe"
}
```

**Response 201:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g...",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

---

### POST /api/auth/login `PUBLIC`

Authenticate and receive tokens.

**Request:**
```json
{
  "email": "john@example.com",
  "password": "Str0ng!Pass"
}
```

**Response 200:** same shape as `/register`.

**Response 401:**
```json
{
  "message": "Credenciais inválidas",
  "status": 401,
  "details": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

---

### POST /api/auth/refresh `PUBLIC`

Exchange a refresh token for a new access token.

**Request:**
```json
{
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g..."
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "bmV3cmVmcmVzaHRva2Vu...",
  "tokenType": "Bearer",
  "expiresIn": 900000
}
```

---

### POST /api/auth/logout

Revoke the current refresh token.

**Request:**
```json
{
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g..."
}
```

**Response 204:** No content.

---

## Users

### GET /api/users/{id} `PUBLIC`

Get a user's public profile.

**Response 200:**
```json
{
  "id": 1,
  "username": "johndoe",
  "fullName": "John Doe",
  "bio": "Passionate home cook.",
  "avatarUrl": "https://cdn.foodbook.com/avatars/1.jpg",
  "recipesCount": 12,
  "followersCount": 340,
  "followingCount": 85,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

---

### GET /api/users/me

Get the authenticated user's own profile.

**Response 200:** same as above, plus `email` field.

---

### PATCH /api/users/me

Update the authenticated user's profile.

**Request (partial update):**
```json
{
  "fullName": "John M. Doe",
  "bio": "Chef & food blogger."
}
```

**Response 200:** Updated profile object.

---

### GET /api/users/{id}/recipes `PUBLIC`

List published recipes by a user.

**Query params:** `page` (default 0), `size` (default 20), `sort` (`createdAt,desc`)

**Response 200:**
```json
{
  "content": [ { "id": 1, "title": "Pasta Carbonara", ... } ],
  "totalElements": 12,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

---

### POST /api/users/{id}/follow

Follow a user.

**Response 204:** No content.  
**Response 409:** Already following.

---

### DELETE /api/users/{id}/follow

Unfollow a user.

**Response 204:** No content.

---

### GET /api/users/me/feed

Get the authenticated user's feed (recipes from followed users).

**Query params:** `page`, `size`

**Response 200:** Paginated list of recipe summaries.

---

## Recipes

### GET /api/recipes `PUBLIC`

Search and list published recipes.

**Query params:**

| Param | Type | Description |
|-------|------|-------------|
| `q` | string | Full-text search on title |
| `cuisine` | string | Filter by cuisine type |
| `difficulty` | string | `EASY`, `MEDIUM`, `HARD` |
| `maxPrepTime` | int | Max total time in minutes |
| `page` | int | Page number (default 0) |
| `size` | int | Page size (default 20) |
| `sort` | string | e.g. `createdAt,desc` |

**Response 200:** Paginated list of recipe summaries.

---

### POST /api/recipes

Create a new recipe (draft).

**Request:**
```json
{
  "title": "Classic Pasta Carbonara",
  "description": "Creamy Roman pasta with eggs and guanciale.",
  "prepTimeMinutes": 15,
  "cookTimeMinutes": 20,
  "servings": 4,
  "difficulty": "MEDIUM",
  "cuisineType": "Italian",
  "ingredients": [
    { "ingredientId": 1, "quantity": 400, "unit": "g", "notes": null },
    { "ingredientId": 2, "quantity": 4, "unit": "units", "notes": "large, room temperature" }
  ],
  "steps": [
    { "stepNumber": 1, "description": "Boil pasta until al dente.", "durationMinutes": 10 },
    { "stepNumber": 2, "description": "Fry guanciale until crisp.", "durationMinutes": 8 }
  ]
}
```

**Response 201:**
```json
{
  "id": 42,
  "title": "Classic Pasta Carbonara",
  "isPublished": false,
  ...
}
```

---

### GET /api/recipes/{id} `PUBLIC for published`

Get a single recipe with full details.

**Response 200:**
```json
{
  "id": 42,
  "title": "Classic Pasta Carbonara",
  "description": "...",
  "author": { "id": 1, "username": "johndoe", "avatarUrl": "..." },
  "prepTimeMinutes": 15,
  "cookTimeMinutes": 20,
  "servings": 4,
  "difficulty": "MEDIUM",
  "cuisineType": "Italian",
  "coverImageUrl": "https://cdn.foodbook.com/recipes/42.jpg",
  "isPublished": true,
  "likesCount": 128,
  "commentsCount": 14,
  "likedByCurrentUser": false,
  "savedByCurrentUser": true,
  "ingredients": [ ... ],
  "steps": [ ... ],
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2024-01-15T10:00:00Z"
}
```

---

### PUT /api/recipes/{id}

Replace a recipe (author only).

**Request:** same shape as POST.  
**Response 200:** Updated recipe object.

---

### PATCH /api/recipes/{id}/publish

Publish a draft recipe (author only).

**Response 200:** `{ "isPublished": true }`

---

### DELETE /api/recipes/{id}

Delete a recipe (author or ADMIN only).

**Response 204:** No content.

---

### POST /api/recipes/{id}/like

Like a recipe.

**Response 204:** No content.  
**Response 409:** Already liked.

---

### DELETE /api/recipes/{id}/like

Remove a like.

**Response 204:** No content.

---

### POST /api/recipes/{id}/save

Save (bookmark) a recipe.

**Response 204:** No content.

---

### DELETE /api/recipes/{id}/save

Remove a saved recipe.

**Response 204:** No content.

---

## Comments

### GET /api/recipes/{id}/comments `PUBLIC`

List comments for a recipe.

**Query params:** `page`, `size`

**Response 200:**
```json
{
  "content": [
    {
      "id": 5,
      "author": { "id": 2, "username": "janedoe", "avatarUrl": "..." },
      "content": "Loved this recipe!",
      "createdAt": "2024-01-16T08:00:00Z"
    }
  ],
  "totalElements": 14,
  ...
}
```

---

### POST /api/recipes/{id}/comments

Add a comment.

**Request:**
```json
{ "content": "Tried it last night — amazing!" }
```

**Response 201:** Created comment object.

---

### PATCH /api/comments/{id}

Edit a comment (author only).

**Request:** `{ "content": "Updated text." }`  
**Response 200:** Updated comment object.

---

### DELETE /api/comments/{id}

Delete a comment (author or ADMIN).

**Response 204:** No content.

---

## Ingredients

### GET /api/ingredients `PUBLIC`

Search the ingredient catalog.

**Query params:** `q` (name search), `page`, `size`

**Response 200:** Paginated list of ingredients.

---

### POST /api/ingredients (ADMIN)

Add a new ingredient to the catalog.

**Request:**
```json
{ "name": "Guanciale", "imageUrl": "https://cdn.foodbook.com/ingredients/guanciale.jpg" }
```

**Response 201:** Created ingredient object.

---

## Integration – Spoonacular

### GET /api/integration/recipes/search `PUBLIC`

Proxy search to Spoonacular and return normalized results.

**Query params:** `q`, `cuisine`, `maxReadyTime`, `number` (default 10)

**Response 200:**
```json
{
  "results": [
    {
      "spoonacularId": 716429,
      "title": "Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs",
      "image": "https://spoonacular.com/recipeImages/716429-312x231.jpg",
      "readyInMinutes": 45
    }
  ],
  "totalResults": 86
}
```

---

### POST /api/integration/recipes/{spoonacularId}/import

Import a Spoonacular recipe into the user's account as a draft.

**Response 201:** Created recipe object (draft, with `spoonacularId` set).

---

## Error Envelope

All errors follow this structure:

```json
{
  "message": "Human-readable description",
  "status": 400,
  "details": { "fieldName": "validation message" },
  "timestamp": "2024-01-15T10:30:00"
}
```

`details` is `null` for most errors; it contains a field-error map for `400 Validation` errors.

---

## HTTP Status Codes Summary

| Code | Meaning |
|------|---------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request / Validation error |
| 401 | Unauthorized (missing or invalid token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 409 | Conflict (duplicate like, follow, etc.) |
| 500 | Internal Server Error |
