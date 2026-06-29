# ADR 002 — Idempotent follow via DB unique constraint

## Status

Accepted

## Context

The `POST /api/v1/users/follow` endpoint creates a follow relationship between two users. Without any guard, calling it multiple times with the same pair inserts duplicate rows, corrupting the followings list.

Two approaches to make the operation idempotent:

- **Application-level check** — query for an existing `(followerId, followeeId)` row before inserting. If it exists, return early.
- **DB unique constraint + catch** — add a unique constraint on `(followerId, followeeId)` at the DB level. Let the insert proceed and catch `DataIntegrityViolationException` on duplicate, returning normally.

## Decision

Enforce uniqueness with a `@UniqueConstraint` on `(followerId, followeeId)` in the `Follow` entity and catch `DataIntegrityViolationException` in `FollowService.follow()` to make the operation a no-op on duplicates.

## Consequences

**Benefits:**
- The DB is the single source of truth for the uniqueness rule — it holds even if the check is bypassed (e.g. direct DB access, future service changes).
- One DB call instead of two (no SELECT before INSERT).
- No race condition — two concurrent follow requests for the same pair both hit the constraint; only one succeeds, the other is caught and ignored without corrupting data.

**Trade-offs:**
- The constraint only takes effect after the next Hibernate schema update (`ddl-auto: update`). Existing duplicate rows are not removed automatically.
- Catching `DataIntegrityViolationException` is broader than necessary — it would also suppress other constraint violations on the same table if any are added later. This can be narrowed if it becomes a problem.
