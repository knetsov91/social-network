<h1 style="text-align: center">API Gateway security</h1>

<h2>Overview</h2>
<p>Spring Security is used to protect endpoints that leads to microservices.
Every request from client is checked if contains cookie with JWT and for protected endpoints.
If JWT is present its validity is checked by making request to auth microservice.
JWT is stored in secure HTTP-only cookie and it is transferred from client to this service
via HTTPS.
</p>

 
