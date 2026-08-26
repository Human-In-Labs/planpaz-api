# Planpaz API

API for the mobile app Planpaz.

## Authentication Endpoints

These endpoints are unauthenticated and are used to obtain a JWT token.

- **`POST /auth/register`**
  - **Description**: Registers a new user.
  - **Body**: `{"name": "User", "email": "user@example.com", "password": "password123"}`
  - **Response**: JWT Token

- **`POST /auth/login`**
  - **Description**: Authenticates an existing user.
  - **Body**: `{"email": "user@example.com", "password": "password123"}`
  - **Response**: JWT Token

## User Endpoints

Requires `Authorization: Bearer <token>` header.

- **`GET /user`**
  - **Description**: Verifies if the user is authenticated.

## Plant Species Endpoints (`/plants`)

Requires `Authorization: Bearer <token>` header. These endpoints manage the catalog of plant species available in the app.

- **`GET /plants`**
  - **Description**: Lists all available plant species in the system.

- **`GET /plants/{id}`**
  - **Description**: Gets details of a specific plant species by ID.

- **`POST /plants`**
  - **Description**: Adds a new plant species to the catalog.
  - **Body**: JSON object containing plant details (e.g., `{"name": "Rosa", "scientificName": "Rosa rubiginosa"}`).

- **`PUT /plants/{id}`**
  - **Description**: Updates an existing plant species.
  - **Body**: JSON object containing updated plant details.

- **`DELETE /plants/{id}`**
  - **Description**: Deletes a plant species from the catalog.

## Garden Plants Endpoints (`/garden-plants`)

Requires `Authorization: Bearer <token>` header. These endpoints manage the authenticated user's personal garden.

- **`GET /garden-plants`**
  - **Description**: Lists all plants in the authenticated user's garden.

- **`POST /garden-plants`**
  - **Description**: Adds a plant to the user's garden.
  - **Body**: JSON object containing garden plant details (e.g., `{"nickname": "My Fern", "stage": 1, "plant": {"id": "<plant-uuid>"}}`).

- **`PUT /garden-plants/{id}`**
  - **Description**: Updates a plant in the user's garden (e.g., updating the nickname).
  - **Body**: JSON object containing the updated garden plant details.

- **`DELETE /garden-plants/{id}`**
  - **Description**: Removes a plant from the user's garden.

- **`POST /garden-plants/{id}/watering`**
  - **Description**: Registers that the user has watered the specific garden plant.

- **`GET /garden-plants/{id}/status-rega?cidade={cidade}`**
  - **Description**: Checks the watering status and weather conditions for the plant based on the provided city. Triggers a notification.