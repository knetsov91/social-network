# Social network

## Project overview

A social network where users can create and like posts, follow each other, and chat in real time. Each core domain — users, authentication, posts, chat, and notifications — is implemented as an independent microservice and consumed by a React SPA through an API Gateway. Services communicate synchronously via REST and asynchronously via Kafka. Observability is provided by Prometheus and Grafana.

## Tech stack

- Java 21
- Spring Boot 3
- Spring Security 6
- Spring Cloud (Gateway, Eureka)
- React JS
- PostgreSQL
- MySQL
- MongoDb
- Redis
- Apache Kafka
- Docker
- JUnit5
- WebSocket
- HashiCorp Vault
- Prometheus
- Grafana

For more information about **database** visit [here](./docs/database.md).
For more information about **architecture** visit [here](./docs/architecture.md).

## Microservices documentation

- User microservice ([here](./docs/user-service/overview.md))
- Auth microservice ([here](./docs/auth-service/overview.md))
- Post microservice ([here](./docs/post-service/overview.md))
- Chat microservice ([here](./docs/chat-service/overview.md))
- Notification microservice ([here](./docs/notification-service/overview.md))
- API Gateway microservice ([here](./docs/api-gateway-service/overview.md))

### Encountered problems

- **Problem**: **ClassCastException** exception when caching posts.
  **Solution**: disable spring-boot-devtools dependency.

- **Problem**: When implement UserDetailsService as lambda or in UserDetails service there is circular dependency when used in OncePerRequestFilter.
  **Solution**: Implement UserDetailsService as Bean in separate class.

- **Problem**: When use HandlerExceptionResolver in OncePerRequestFilter there is circular dependency.
  **Solution**: Use field injection with @Lazy annotation. (*Temporary fix*)

- **Problem**: CORS - duplicated Access-Control-Allow-Origin in response headers.
  **Solution**: Add DedupeResponseHeader in API Gateway's **default-filters** property.

- **Problem**: When caching entity that contains LAZY loaded collection gives "failed to lazily initialize a collection of role: ... could not initialize proxy - no Session".
  **Solution**: Make Lazy loaded collection in Post entity as eagerly loaded.

- **Problem**: When build image in docker-compose.yaml give "ERROR: unable to prepare context: path "[Dockerfile-directory]/Dockerfile" not found".
  **Solution**: In docker-compose.yaml build attribute for service must contain [Dockerfile-directory] only.

- **Problem**: Eureka service discovery with docker when try to communicate with API Gateway microservice give following error: "Could not create URI object: Illegal character in hostname at index 14: http://service_disc:8761/eureka/apps/GATEWAY-SERVICE".
  **Solution**: Service name must not contain "-" character.

- **Problem**: Handling exceptions globally in CustomErrorWebExceptionHandler class return response without body.
  **Solution**: Add getters and setters for the class returned in response body.

- **Problem**: When using WebClient to make requests to service that use self-signed certificate gives "PKIX path building failed: unable to find valid certification path to requested target".
  **Solution**: Configuring WebClient with a custom Truststore.

- **Problem**: During integration test with H2 in-memory database get "h2 could not prepare statement [table "user" not found]".
  **Solution**: Use `spring.jpa.database-platform=org.hibernate.dialect.H2Dialect` instead of `spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect` in properties.yaml used for testing and use `@Table(name="`user`")` in User entity.
