# ADR 001 — Cookie-based JWT authentication

## Status

Accepted

## Context

The API Gateway needs to authenticate every request before forwarding it downstream. The JWT token issued by auth-service has to travel from the browser to the gateway on each request. Two common approaches:

- **Authorization header** (`Authorization: Bearer <token>`) — the frontend reads the token from the login response, stores it (localStorage or memory), and attaches it manually to every request.
- **HttpOnly cookie** — the token is set as a cookie by the server on login. The browser sends it automatically with every request; JavaScript cannot read it.

The application has a React SPA frontend making cross-origin requests to the gateway.

## Decision

Use an `HttpOnly`, `Secure`, `SameSite=None` cookie named `token`. The cookie is set by user-service on successful login and read by `AuthFilter` in the API Gateway on every subsequent request.

## Consequences

**Benefits:**
- The token is never accessible to JavaScript — XSS attacks cannot steal it.
- The frontend does not need to manage token storage or attach headers manually.

**Trade-offs:**
- Requires `SameSite=None; Secure` because the frontend and gateway run on different origins — this only works over HTTPS.
- CSRF attacks are possible since cookies are sent automatically by the browser. The risk is reduced because the API is JSON-only — cross-origin JSON requests with `Content-Type: application/json` trigger a CORS preflight, which is blocked if CORS is configured correctly. However, CSRF protection is not explicitly implemented.
- Slightly more complex CORS setup — `allowCredentials: true` is required and wildcard origins are not allowed.
