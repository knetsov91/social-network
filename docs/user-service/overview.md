# User microservice

## Overview

This service is implemented following REST API architecture style using Spring Web. **MySQL** is used for data persistence via Spring Data JPA with Hibernate ORM. **Redis** is used as a cache to offload the main database.

## Caching

Redis is used to cache two read-heavy queries:

- **All users list** — cached on `GET /users/all`, evicted when a new user registers
- **User followings** — cached per user, evicted when that user follows someone

Cache entries expire after **10 minutes**.

## Functional requirements

- User can register and login with username and password.
- User can follow other users.
- User can view other users.
