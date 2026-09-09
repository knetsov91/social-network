# Functional Requirements for Service Discovery

This service has no custom code — it's a Netflix Eureka server (`@EnableEurekaServer`) with default configuration.

## 1. Service Registry

- **Description**: Every other business service registers itself with this server on startup and sends periodic heartbeats. The API Gateway and any service making inter-service calls (post-service → user-service, for example) resolve target instances through it instead of hardcoded addresses.
- **Acceptance Criteria**:
  - A registered instance disappears from the registry if it stops sending heartbeats (Eureka's default self-preservation and eviction timing applies — not tuned for this project).
  - Single instance, no HA — this is a SPOF, and adds a short registration/eviction delay window on scale-up or scale-down.
