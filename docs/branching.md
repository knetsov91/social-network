# Branching strategy

## Previous strategy (until PR #11)

Per-service feature branches were used during initial development, one branch per microservice:

- `user-service`
- `auth-service`
- `chat-service`
- `notification-service`
- `service/post-service`
- `docs`
- `frontend`

Each branch was merged into `dev` and then PR'd into `main`.

This worked well while each service was being built in isolation from scratch.

## Current strategy

Now that the initial build phase is complete, changes frequently span multiple services. Per-service branches add overhead without benefit for a solo project, so the workflow is simplified:

```
main    — stable, production-ready
dev     — integration branch, always in a working state
```

All work is committed directly to `dev`, then `dev` is PR'd into `main` when changes are complete.

## Commit message format

Semantic commit messages are used:

```
type(scope): description
```

**Types:**

| Type | When to use |
|---|---|
| `feat` | new functionality |
| `fix` | bug fix |
| `docs` | README or docs folder changes |
| `refactor` | code change with no behavior change |
| `perf` | performance optimization with no behavior change |
| `test` | adding or fixing tests |
| `chore` | build, infrastructure, configuration |
| `ci` | CI/CD pipeline changes |
| `style` | formatting only |

**Scopes** match service or area names: `user-service`, `post-service`, `auth-service`, `chat-service`, `notification-service`, `api-gateway`, `infrastructure`, `readme`.

**Examples:**

```
feat(user-service): add user search endpoint
fix(post-service): use @CreationTimestamp on createdAt field
docs(readme): add quick-start section
chore(infrastructure): add multi-stage Dockerfile for user service
test(user-service): add integration tests for follow service
refactor(auth-service): remove redundant isValid method
```
