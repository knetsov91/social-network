<h1 style="text-align: center">API Gateway microservice</h1>

<h2>Overview</h2>
<p>This microservice is used as entry point to the application. It is used for routing request made by clients(frontend, etc.) to
different microservice. Implemented using Spring Cloud API Gateway which uses WebFlux reactive streams.
In order to locate microservice for which the request is intended this microservice communicate with
service discovery microservice.
Every other microservice is register to it in order to be discovered by API Gateway microservice.  
</p>