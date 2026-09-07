# Functional Requirements for Auth Service

## 1. Issue Token

- **Endpoint**: `POST /api/v1/tokens/issue`
- **Description**: Sign a JWT for an already-authenticated user. Called by user-service after it verifies credentials — auth-service does not check a password itself.
- **Inputs**: `userId`, `username`.
- **Business Rules**:
  - Claims: `userId`, `username`; subject set to `username`.
  - Signed HS256 with `jwt.secret-key` (from Vault), expiry from `jwt.expiration-time`.
- **Outputs**: `200 OK` with `{token}`.

## 2. Validate Token

- **Endpoint**: `POST /api/v1/tokens/validate`
- **Description**: Full validation of a token — used by the API Gateway on every authenticated request.
- **Inputs**: `token`.
- **Business Rules**:
  - Rejects the token if it's in the blacklist (Redis-backed).
  - Rejects the token if the signature is invalid or it has expired.
- **Outputs**: `200 OK` (echoes the token) if valid; `400 Bad Request` for a malformed/invalid-signature token; `401 Unauthorized` for an expired token.
- **Known bug**: a *blacklisted* token currently returns `500 Internal Server Error`, not `401`. The blacklist check throws a plain `RuntimeException`, which isn't caught by the `JwtException`/`ExpiredJwtException` handlers and falls through to the generic exception handler. The normal "user logged out, token blacklisted" case looks like a server error to callers.

## 3. Invalidate Token

- **Endpoint**: `POST /api/v1/tokens/invalidate`
- **Description**: Blacklist a token so it's rejected on every future validation, even if it hasn't expired yet.
- **Inputs**: `token`.
- **Outputs**: `200 OK` with an empty body.
- **Acceptance Criteria**:
  - No check that the token being invalidated is even a valid JWT — any string is accepted and stored.
  - Blacklisting is permanent (no TTL matching the token's own expiry) — the blacklist entry outlives the token itself.

## 4. Check Invalidation Status

- **Endpoint**: `POST /api/v1/tokens/is-invalidated`
- **Description**: Cheaper check than full **Validate** — only checks the blacklist, not signature or expiry.
- **Inputs**: `token`.
- **Outputs**: `200 OK` if not blacklisted; `400 Bad Request` if blacklisted.
- **Acceptance Criteria**:
  - Does not verify the token is a real, unexpired JWT — a garbage string that was never issued returns 200 (not blacklisted) rather than an error.
