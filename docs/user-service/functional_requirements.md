# Functional Requirements for User Service

## 1. User Registration

- **Endpoint**: `POST /api/v1/users/register`
- **Description**: Allow new users to create an account.
- **Inputs**: `username` (≥ 2 chars), `password` (≥ 8 chars), `confirmPassword` (≥ 8 chars).
- **Business Rules**:
  - Username must be unique (checked in the service, enforced by a DB unique constraint).
  - `password` must match `confirmPassword`.
  - Password is encoded before storage.
  - Registration evicts the `users` cache.
- **Outputs**: `201 Created` with an empty body on success; `400 Bad Request` with a validation message on failure (missing fields, password/confirmation mismatch, or username already taken).
- **Acceptance Criteria**:
  - Successful registration persists the user with an encoded password and returns 201 with no cookie set — the client must call **Login** separately to authenticate.
  - Validation errors return 400 with a message describing what failed.
  - Password is never stored in plain text.

## 2. User Login

- **Endpoint**: `POST /api/v1/users/login`
- **Description**: Authenticate a user and issue a session cookie.
- **Inputs**: `username`, `password` (≥ 8 chars).
- **Business Rules**:
  - Credentials are verified against the stored encoded password.
  - On success, user-service calls auth-service to issue a signed JWT.
- **Outputs**: `200 OK` with `{id, username}` and a `Set-Cookie: token=...` (`HttpOnly`, `Secure`, `SameSite=Strict`, 1 hour expiry) on success; `400 Bad Request` on invalid credentials or validation failure.
- **Acceptance Criteria**:
  - Successful login sets the `token` cookie used by all subsequent authenticated requests.
  - Invalid credentials never reveal whether the username or password was wrong.

## 3. List Users

- **Endpoint**: `GET /api/v1/users`
- **Description**: Return every registered user.
- **Outputs**: `200 OK` with a list of `{userId, username}`.
- **Acceptance Criteria**:
  - Result is cached (`users` cache) and evicted on registration.
  - No authentication is currently enforced on this endpoint.

## 4. Get User by ID

- **Endpoint**: `GET /api/v1/users/{id}`
- **Description**: Fetch a single user's public profile.
- **Outputs**: `200 OK` with `{userId, username}`.
- **Acceptance Criteria**:
  - Unknown `id` results in an error response (no dedicated 404 handling — falls through to the generic exception handler).

## 5. Check Authentication Status

- **Endpoint**: `GET /api/v1/users/is-authenticated`
- **Description**: Let a client check whether its current session cookie is still valid.
- **Business Rules**:
  - Reads the `token` cookie and validates it against auth-service (signature, expiry, blacklist).
- **Outputs**: `200 OK` if the token is present and valid; `401 Unauthorized` if the cookie is missing or invalid.

## 6. Follow a User

- **Endpoint**: `POST /api/v1/users/follow`
- **Description**: Create a follow relationship from the authenticated user to another user.
- **Inputs**: `followeId` (UUID of the user to follow).
- **Business Rules**:
  - Idempotent: following the same user twice is a no-op rather than an error, enforced via a DB unique constraint and a caught `DataIntegrityViolationException` (see [ADR 002](../decisions/002-idempotent-follow-via-db-constraint.md)).
  - Evicts the follower's `followings` cache entry.
- **Outputs**: `200 OK` on success, including when the follow relationship already existed.
- **Acceptance Criteria**:
  - Intended to require authentication — the follower is taken from the authenticated principal, not a request field.
  - Calling this endpoint repeatedly with the same `followeId` never creates duplicate rows or returns an error.
- **Known bug**: `SecurityConfig`'s `requestMatchers("/api/v1/users/*")` is a single-segment wildcard meant for `GET /api/v1/users/{id}`, but `/api/v1/users/follow` also has exactly one segment after `/users/`, so it matches too — making this endpoint unintentionally `permitAll()`. An unauthenticated call doesn't get rejected with 401; `@AuthenticationPrincipal` resolves to `null`, and the subsequent `user.getId()` throws a `NullPointerException`, surfacing as a generic `400` instead.

## 7. Get a User's Followings

- **Endpoint**: `GET /api/v1/users/{userId}/followings`
- **Description**: List the IDs of users a given user follows.
- **Outputs**: `200 OK` with a list of `followeeId` UUIDs (empty list if none).
- **Acceptance Criteria**:
  - Result is cached per `userId` and evicted whenever that user follows someone new.
