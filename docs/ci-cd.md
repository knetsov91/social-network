# CI/CD

## Overview

CI is handled by **GitHub Actions**. Each microservice has its own workflow that triggers only when files in its directory change — unrelated services are not rebuilt. All workflows share a single reusable build definition to avoid duplication.
