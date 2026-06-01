# API Gateway microservice

## Overview

The API Gateway is the single entry point for all client requests. It runs on port **8085** over HTTPS and is responsible for routing, authentication, rate limiting, and CORS handling.

Built with Spring Cloud Gateway (WebFlux/reactive). Routes are resolved via Netflix Eureka service discovery — each downstream service registers itself, and the gateway uses client-side load balancing to forward requests. When multiple instances of a service are registered, requests are distributed between them using round-robin.

## Rate Limiting

All routes go through a token bucket rate limiter backed by Redis. Authenticated requests are keyed by JWT subject; unauthenticated requests fall back to client IP. Requests exceeding the bucket receive `429 Too Many Requests`.

- Replenish rate: 10 requests / second
- Burst capacity: 20 requests

## More information

- [Routes](./routes/routes.md)
- [Security](./security/security.md)
