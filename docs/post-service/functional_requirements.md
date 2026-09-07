# Functional Requirements for Post Service

## 1. Create Post

- **Endpoint**: `POST /api/v1/posts`
- **Description**: Create a new post.
- **Inputs**: `title`, `content`, `authorId`.
- **Business Rules**:
  - Evicts the author's `user-posts` cache entry.
- **Outputs**: `201 Created` with an empty body.
- **Known gap**: `authorId` comes directly from the request body, not the authenticated principal. Nothing cross-checks it against the caller's identity — any authenticated request can create a post attributed to an arbitrary user.

## 2. Get a User's Posts

- **Endpoint**: `GET /api/v1/posts/users/{userId}`
- **Description**: List all posts by a given author.
- **Outputs**: `200 OK` with a list of posts (author, title, content, like count/likers).
- **Acceptance Criteria**:
  - Result is cached per author (`user-posts`) and evicted on that author's next create/like/unlike.

## 3. Get Feed

- **Endpoint**: `GET /api/v1/posts/feed`
- **Description**: Paginated feed of posts from the accounts the current user follows.
- **Inputs**: `X-User-Id` header (set by the API Gateway from the validated token — not client-supplied), `page` (default 0), `size` (default 20).
- **Business Rules**:
  - Calls user-service (via Feign) for the caller's followings list.
  - Falls back to an empty page if the user follows no one, or if user-service is unreachable (Resilience4j circuit breaker — see the circuit-breaker k6 test).
  - Sorted by `createdAt` descending.
- **Outputs**: `200 OK` with a paginated list of posts.

## 4. Toggle Like

- **Endpoint**: `PUT /api/v1/posts/{postId}/likes`
- **Description**: Like a post if the user hasn't liked it yet, otherwise unlike it.
- **Inputs**: `postId` (path), `userId` (body).
- **Business Rules**:
  - Evicts the post author's `user-posts` cache entry either way.
  - **Liking** publishes a `likes-topic` event via the transactional outbox (see [ADR 003](../decisions/003-transactional-outbox.md)), which notification-service consumes to push a real-time notification.
  - **Unliking** does not publish any event — no "unlike" notification exists.
  - Unknown `postId` returns an error (`400`, via the generic `RuntimeException` handler — not a dedicated `404`).
- **Outputs**: `201 Created` if the post is now liked; `204 No Content` if the post is now unliked; `409 Conflict` if a concurrent update raced with this one (optimistic locking).
- **Known gap**: same as Create Post — `userId` comes from the request body, not the authenticated principal. A caller can like or unlike a post as an arbitrary user.
