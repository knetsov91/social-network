# Functional Requirements for Notification Service

This service has no REST API — its two features are a Kafka consumer and a WebSocket presence tracker, both surfaced to clients only over STOMP.

## 1. Real-Time Like Notifications

- **Trigger**: consumes `likes-topic` (Kafka, consumer group `likes-group`), published by post-service's outbox poller when a post is liked (not on unlike — see post-service's functional requirements).
- **Business Rules**:
  - Broadcasts the like event to `/topic/likes` over STOMP.
- **Known gaps** (see [docs/known-issues.md](../known-issues.md) for full detail): failures are caught and logged rather than retried or dead-lettered, so a transient failure silently drops the notification with no trace; the in-memory STOMP broker doesn't fan out across replicas, so this only reaches clients connected to whichever instance happened to consume the Kafka message.

## 2. Presence Tracking

- **Trigger**: a client's WebSocket `CONNECT`/`DISCONNECT` events on `/ws-notifications`, carrying a `userId` STOMP header.
- **Business Rules**:
  - On connect: writes `presence:{userId} = ONLINE` to Redis with a 60-second TTL, and broadcasts `{userId, ONLINE}` to `/topic/presence`.
  - On disconnect: deletes the Redis key and broadcasts `{userId, OFFLINE}`.
  - `presence_online_users` (a custom Micrometer gauge, exposed via `/actuator/prometheus`) reports the count of non-expired presence keys in Redis.
- **Known bug**: the 60-second TTL is set once, at connect time, with no heartbeat or periodic refresh anywhere in the service. A connection open longer than 60 seconds will have its Redis presence key expire while the WebSocket is still fully connected — `isOnline()` and the `presence_online_users` metric will report the user as offline even though they aren't.
- **Known gap**: same in-memory-broker limitation as Like Notifications — a presence change on one replica only reaches clients connected to that same replica.
