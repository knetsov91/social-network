# CI/CD

## Overview

CI is handled by **GitHub Actions**. Each microservice has its own workflow that triggers only when files in its directory change — unrelated services are not rebuilt. All workflows share a single reusable build definition to avoid duplication.

## Workflow Structure

The CI setup consists of two types of workflow files:

- **`_gradle-build.yml`** — reusable workflow that contains the build and test steps. Called by all per-service workflows.
- **`ci-<service>.yml`** — one file per service. Defines the trigger (branches and path filter) and calls the reusable workflow with the service name.

## Triggers

Each per-service workflow triggers on:

- **Push** to `main` or `dev` — when files inside the service directory change
- **Pull request** targeting `main` or `dev` — when the PR diff touches the service directory

Changes outside a service directory (e.g. docs, infrastructure) do not trigger any build.

## Build Steps

The reusable workflow runs the following steps for each service:

1. **Checkout** — fetches the repository
2. **Set up Java 21** — uses the Temurin distribution with Gradle dependency caching enabled
3. **Build** — compiles and packages the service (`./gradlew build -x test`)
4. **Unit tests** — runs unit tests only

Integration tests are excluded — they require real databases and are not yet wired up in CI.
