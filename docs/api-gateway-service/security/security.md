# API Gateway security

## Overview

All requests entering the gateway pass through **`AuthFilter`** before being forwarded to any downstream service. The filter intercepts every request and delegates token validation to **auth-service**, which checks the token's signature, expiry, and blacklist.

## Authentication Flow

1. The filter checks if the request path matches a **public endpoint**. If it does, the request is forwarded immediately without any token check.
2. For all other requests, the filter reads the **`token` cookie** from the request. If the cookie is missing, the gateway returns **`401 Unauthorized`** immediately.
3. If the cookie is present, the filter calls **auth-service** to validate the token.
4. If validation fails, the gateway returns **`401 Unauthorized`** without forwarding the request downstream.
5. If validation succeeds, the request is forwarded to the target service.

## Public Endpoints

The following paths bypass authentication — no `token` cookie is required:

| Path | Description |
|---|---|
| `/api/*/users/register` | Register a new user |
| `/api/*/users/login` | Login and receive token cookie |
| `/api/*/tokens/**` | Token operations (issue, validate, invalidate) |
| `/actuator/**` | Spring Boot actuator endpoints |

Wildcards:
- `*` — matches a **single path segment** (e.g. `v1`)
- `**` — matches **any number of path segments** (e.g. `/tokens/validate`, `/tokens/issue`)

## Cookie Handling

Authentication is **cookie-based** — the JWT is stored in an **`HttpOnly`** cookie, so **JavaScript cannot read it**. The browser sends it automatically with every request; the client never needs to extract, store, or attach it manually.

The cookie is named **`token`**, is **`HttpOnly`** (not accessible via JavaScript), **`Secure`** (HTTPS only), **`SameSite=None`** (allows cross-origin requests from the frontend), and expires after **1 hour**.

The cookie is **set by user-service** on successful login. The gateway reads it from every subsequent request in **`AuthFilter`** and passes the value to **auth-service** for validation.
