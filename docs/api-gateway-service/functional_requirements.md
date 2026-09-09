# Functional Requirements for API Gateway

The gateway has no business endpoints of its own — its job is routing, authentication gatekeeping, rate limiting, and CORS for every other service. See [routes.md](routes/routes.md) and [security.md](security/security.md) for the full routing table and auth flow; this covers the behavior in requirements terms.

## 1. Route Requests to Backend Services

- **Description**: Forward each request to the correct service based on path, using Eureka-backed load balancing (`lb://<service-name>`), including WebSocket upgrade requests for chat and notifications (`lb:ws://`).
- **Acceptance Criteria**: a route exists for every business service; unmatched paths fall through to the default error handler.

## 2. Gatekeep Authentication

- **Description**: Reject unauthenticated requests before they reach any downstream service, except for an explicit public-path allowlist.
- **Business Rules**:
  - `OPTIONS` requests (CORS preflight) always pass through, no token check.
  - A request to a public path is forwarded without a token check.
  - Every other request must carry a `token` cookie; missing it returns `401` immediately.
  - Otherwise the token is validated against auth-service; on success, the request is forwarded with an `X-User-Id` header (extracted from the token's unverified payload, purely to save downstream services a lookup — the actual trust decision is auth-service's validation call, not this header).
- **Known bug**: the public-path pattern `/api/v1/users/*` is meant for single-segment paths like `/api/v1/users/{id}`, but `/api/v1/users/follow` also has exactly one segment after `/users/`, so it matches too. This endpoint is unintentionally public at the gateway — and, separately, unintentionally public at user-service's own security layer for the identical reason (see user-service's functional requirements). The two bugs compound: neither layer actually enforces authentication on **Follow a User**.

## 3. Rate Limit Requests

- **Description**: Throttle requests per caller using a Redis-backed token bucket.
- **Business Rules**:
  - Key resolution: the JWT `sub` claim from the `token` cookie if present and parseable, otherwise the caller's remote IP address (see `RateLimitConfigUTest`).
  - Limits: 10 requests/second sustained, burst capacity of 20.
- **Outputs**: `429 Too Many Requests` once the bucket is exhausted; requests succeed again as the bucket refills.

## 4. Enforce CORS

- **Description**: Allow the configured frontend origin to make credentialed cross-origin requests.
- **Business Rules**:
  - Duplicate `Access-Control-Allow-Origin`/`Access-Control-Allow-Credentials` headers are explicitly deduplicated (`DedupeResponseHeader` filter) — both Spring Cloud Gateway's own CORS handling and a custom `CorsConfigurationSource` bean are in play, so without this filter the headers would be sent twice.
  - CORS headers are added even on `401` responses from **Gatekeep Authentication**, so the browser can actually surface the error to frontend JavaScript instead of silently failing as a CORS violation.

## 5. Uniform Error Responses

- **Description**: Every unhandled error, gateway-side or from a downstream service, is rendered as a consistent JSON body (`{error, status, message}`) rather than a framework default error page.
