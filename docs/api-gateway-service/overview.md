# API Gateway microservice

## Overview

The API Gateway is the single entry point for all client requests. It runs on port **8085** over HTTPS and is responsible for routing, authentication, and CORS handling.

Built with Spring Cloud Gateway (WebFlux/reactive). Routes are resolved via Netflix Eureka service discovery — each downstream service registers itself, and the gateway uses client-side load balancing to forward requests. When multiple instances of a service are registered, requests are distributed between them using round-robin.

## Routes

| Path pattern | Service | Notes |
|---|---|---|
| `/api/*/posts/**` | post-service | |
| `/api/*/users/**` | user-service | |
| `/api/*/tokens/**` | auth-service | |
| `/api/*/notifications` | notification-service | |
| `/ws-endpoint/**` | notification-service | WebSocket upgrade |

Path patterns use two wildcards:
- `*` — matches a **single path segment** (e.g. `v1`)
- `**` — matches **any number of segments** including nested paths (e.g. `/posts/123/likes`)

The `/ws-endpoint/**` route uses **`lb:ws://`** instead of `lb://` — this tells the gateway to forward the request as a **WebSocket connection** rather than a standard HTTP request. The client performs a **WebSocket handshake** (HTTP Upgrade) and the gateway proxies the **persistent connection** through to notification-service.
