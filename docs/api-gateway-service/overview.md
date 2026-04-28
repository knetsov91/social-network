# API Gateway microservice

## Overview

The API Gateway is the single entry point for all client requests. It runs on port **8085** over HTTPS and is responsible for routing, authentication, and CORS handling.

Built with Spring Cloud Gateway (WebFlux/reactive). Routes are resolved via Netflix Eureka service discovery — each downstream service registers itself, and the gateway uses client-side load balancing to forward requests. When multiple instances of a service are registered, requests are distributed between them using round-robin.
