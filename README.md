<h1>Social network</h1>

<h2>Project overview</h2>
<p>Project implement social network where people can create,like posts chat with other people. Microservice architecture is used where
main functionalities related to posts, users, authentication etc. are in separate projects.
These services are consumed from front-end SPA application implemented using React UI Javascript library.
For data persistence SQL and NoSQL databases are used i.e. MongoDb, PostgreSQL. 
JWT is used for authentication and authorization. Redis NoSQL datastore is used in some microservices
as cache to offload pressure from the RDBMS. WebSocket protocol is used in some microservices for real-time communication.
Observability is implemented by using Prometheus to collect and query
metrics from microservices and Grafana for their visualization. Kafka is used for asynchronous communication
between some microservices
</p>

<h4>Tech stack</h4>
<ul>
  <li>Java 21</li>
  <li>Spring Boot 3</li>
  <li>Spring Security 6</li>
  <li>React JS</li>
  <li>PostgreSQL</li>
  <li>MongoDb</li>
  <li>Redis</li>
  <li>Docker</li>
  <li>JUnit5</li>
  <li>WebSocket</li>
  <li>HashiCorp Vault</li>
  <li>Prometheus</li>
  <li>Grafana</li>
</ul> 

<p>
    For more information about <b>database</b> visit  
    <a href="./docs/database.md">here</a>.<br>
    For more information about <b>architecture</b> visit  
    <a href="./docs/architecture.md">here</a>.
</p>

<h2>Microservices documentation</h2>
<ul>
    <li>User microservice (<a href="./docs/user-service/overview.md">here</a>)</li>
    <li>Auth microservice (<a href="./docs/auth-service/overview.md">here</a>)</li>
    <li>Post microservice (<a href="./docs/post-service/overview.md">here</a>)</li>
    <li>Notification microservice (<a href="./docs/notification-service/overview.md">here</a>)</li>
    <li>API Gateway microservice (<a href="./docs/api-gateway-service/overview.md">here</a>)</li>
</ul>

<h3>Encountered problems</h3>
<ul>
    <li><b>Problem</b>: <b>ClassCastException</b> exception when caching posts. <br>
        <b>Solution</b>: disable spring-boot-devtools dependency  </li>
    <li><b>Problem</b>: When implement UserDetailsService as lambda or in UserDetails service there is circular dependency when used in OncePerRequestFilter. <br>
        <b>Solution</b>: Implement UserDetailsService as Bean in separate class.</li>
    <li><b>Problem</b>: When use HandlerExceptionResolver in OncePerRequestFilter there is circular dependency.<br>
        <b>Solution</b>: Use field injection with @Lazy annotation. (<u>Temporary fix</u>)</li>
    <li><b>Problem</b>: CORS - duplicated Access-Control-Allow-Origin in response headers. <br>
        <b>Solution</b>: Add DedupeResponseHeader in API Gateway's <b>default-filters</b> property.</li>
    <li><b>Problem</b>: When caching entity that contains LAZY loaded collection gives "failed to lazily initialize a collection of role: ... could not initialize proxy - no Session"<br>
        <b>Solution</b>: Make Lazy loaded collection in Post entity as eagerly loaded.</li>
    <li><b>Problem</b>: When build image in docker-compose.yaml give "ERROR: unable to prepare context: path "[Dockerfile-directory]/Dockerfile" not found".<br>
        <b>Solution</b>: In docker-compose.yaml build attribute for service must contain [Dockerfile-directory] only.</li>
    <li><b>Problem</b>: Eureka service discovery with docker when try to communicate with API Gateway microservice give following error: "Request execution error. endpoint=DefaultEndpoint{ serviceUrl='http://service_disc:8761/eureka/}, exception=Could not create URI object: Illegal character in hostname at index 14: http://service_disc:8761/eureka/apps/GATEWAY-SERVICE stacktrace=java.lang.IllegalStateException: Could not create URI object: Illegal character in hostname at index 14: http://service_disc:8761/eureka/apps/GATEWAY-SERVICE".<br>
        <b>Solution</b>: Service name must not contain "-" character. </li>
    <li><b>Problem</b>: Handling exceptions globally in CustomErrorWebExceptionHandler class return response without body.<br>
        <b>Solution</b>: Add getters and setters for the class returned in response body. </li>
    <li><b>Problem</b>: When using WebClient to make requests to service that use self-signed certificate gives "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target".<br>
        <b>Solution</b>: Configuring WebClient with a custom Truststore. </li>
    <b><b>Problem</b>: During integration test with H2 in-memory database get "h2 could not prepare statement [table "user" not found (this database is empty); sql statement:".<br>
        <b>Solution</b>: Use <b></b>spring.jpa.database-platform=org.hibernate.dialect.H2Dialect</b> instead of <b>spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect</b> in properties.yaml used for testing and use @Table(name="`user`") in User entity.</li>

</ul>
