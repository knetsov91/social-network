# User microservice

## Overview

This service is implemented following REST API architecture style using Spring Web. **MySQL** is used for data persistence via Spring Data JPA with Hibernate ORM. **Redis** is used as a cache to offload the main database.

## Functional requirements

- User can register and login with username and password.
- User can follow other users.
- User can view other users.
