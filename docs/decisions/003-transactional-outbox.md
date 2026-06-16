# ADR 003 — Transactional Outbox in post-service

## Status

Accepted

## Context

**PostService.togglePostLike()** persists a like to PostgreSQL and publishes an event to Kafka so **notification-service** can push a real-time notification to the post author. The two operations are not atomic — a crash between the DB commit and **kafkaTemplate.send()** silently drops the event. The like is saved but the notification never fires.

Two approaches to guarantee delivery:

- **Transactional outbox** — write the event to an **outbox_events** table in the same DB transaction as the business operation. A separate poller picks up unpublished rows and forwards them to Kafka.
- **Debezium CDC** — CDC (Change Data Capture) tracks every row-level change in the database by reading the write-ahead log. Debezium uses CDC to tail the PostgreSQL WAL and stream new **outbox_events** rows to Kafka automatically with no polling code in the service.

## Decision

Use the transactional outbox with a polling approach. **PostService** writes an **OutboxEvent** row to **outbox_events** within the same transaction as the post update. **OutboxEventPoller** runs every 5 seconds, reads all unpublished rows, sends each to its target Kafka topic and marks them published within a **@Transactional** method.

Debezium was not chosen because it requires deploying and operating Kafka Connect alongside the existing infrastructure.

## Consequences

**Benefits:**
- Event delivery is guaranteed — if the DB transaction commits the event will eventually reach Kafka regardless of crashes or restarts.
- Kafka is no longer a required part of the business transaction — a Kafka outage does not fail the like operation.

**Drawbacks:**
- The poller runs on every service instance. Under horizontal scaling multiple instances will pick up the same unpublished rows and send duplicate events to Kafka. Fixing this requires ShedLock or rewriting the query to use **SELECT FOR UPDATE SKIP LOCKED** so each instance claims a disjoint batch.
- Delivery delay depends on the poll interval, currently fixed at 5 seconds in **OutboxEventPoller**.
