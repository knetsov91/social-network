<h1>Social network</h1>

<h2>Project overview</h2>
<p>Project implement social network where people can create,like posts chat with other people. Microservice architecture is used where
main functionalities related to posts, users, authentication etc. are in separate projects. These services are consumed from front-end SPA application implemented using React UI Javascript library. For data persistance SQL and NoSQL databases are used i.e MongoDb, PostgreSQL.JWT is used for authentication and authorization. 

</p>

<h4>Tech stack</h4>
<ul>
  <li>Java 21</li>
  <li>Spring Boot 3</li>
  <li>Spring Security 6</li>
  <li>React JS</li>
  <li>PostgreSql</li>
  <li>MongoDb</li>
  <li>Docker</li>
  <li>JUnit5</li>
  <li>HashiCorp Vault</li>
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
</ul>
<p>
For more information about <b>database</b> visit  
<a href="./docs/database.md">here</a>.<br>
For more information about <b>architecture</b> visit  
<a href="./docs/architecture.md">here</a>.

</p>
