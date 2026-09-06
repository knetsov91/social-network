# Known issues

Gaps identified but not yet fixed. Some only surface once a service runs more than one replica; others are correctness bugs present today, single instance or not.

## Outbox poller duplicates events under horizontal scaling

**Where:** `post-service`, `OutboxEventPoller.poll()`

The poller has no leader election or row-level locking. Run more than one `post-service` instance and every replica's `@Scheduled` poll picks up the same unpublished rows, sending duplicate events to Kafka.

Already documented as a drawback in [ADR 003](./decisions/003-transactional-outbox.md#consequences). Fix: either `@SchedulerLock` (ShedLock) so only one instance polls at a time, or rewrite the query with `SELECT ... FOR UPDATE SKIP LOCKED` so each instance claims a disjoint batch — the latter scales with replica count, ShedLock doesn't but is a smaller change.

## Kafka consumer swallows failures instead of retrying or dead-lettering

**Where:** `notification-service`, `NotificationService.likes()`

```java
} catch (Exception e) {
    log.error("Failed to process likes event", e);
}
```

Catching and logging counts as success to Spring Kafka's default ack mode — the offset commits, the message is gone, no retry, no dead-letter topic. A transient failure here (e.g. the WebSocket send throwing) silently drops the notification with no trace of it having failed.

Fix: configure a `DefaultErrorHandler` with retry/backoff and a `DeadLetterPublishingRecoverer` instead of catching the exception locally.

## In-memory STOMP broker doesn't fan out across replicas

**Where:** `notification-service` and `chat-service`, `WebsocketConfig.configureMessageBroker()`

Both use `registry.enableSimpleBroker("/topic")` — Spring's in-process broker, which only knows about WebSocket sessions held by that instance. The API Gateway load-balances WebSocket handshakes round-robin with no sticky routing (`lb:ws://notification-service`), so a client connected to instance A never sees a message that instance B pushed via `convertAndSend()`. Scale either service past one replica and delivery becomes instance-dependent — most clients silently stop getting real-time updates, with no error anywhere.

Fix: replace the simple broker with a relay to a shared broker — `enableStompBrokerRelay` to RabbitMQ, or a Redis pub/sub backplane, since Redis is already in the stack.

## Outbox poller marks events published before confirming delivery

**Where:** `post-service`, `OutboxEventPoller.poll()`

```java
kafkaTemplate.send(event.getTopic(), like);
event.setPublished(true);
```

`send()` is async and returns a future; the code doesn't wait on it. `event.setPublished(true)` runs unconditionally and commits with the surrounding `@Transactional`, regardless of whether the broker ever acknowledged the message. If the send fails after the method returns (broker unreachable, timeout), the row is already marked done and never retried — silent event loss, independent of replica count.

Fix: block on the send future (with a timeout) before flipping the flag, or move the flag flip into the send's success callback, so a failed send leaves the row `published = false` for the next poll to retry.

## One malformed outbox row blocks and duplicates the whole batch

**Where:** `post-service`, `OutboxEventPoller.poll()`

The method loops over every pending row inside a single `@Transactional`. If `objectMapper.readValue()` throws on one malformed payload, the whole transaction rolls back — but any `kafkaTemplate.send()` calls already issued earlier in that same loop iteration are not rolled back, since Kafka isn't part of the DB transaction. The next poll resends those as duplicates, while the bad row retries every 5 seconds forever with no backoff or dead-letter path.

Fix: isolate failure per row (try/catch per iteration instead of one transaction for the whole batch), and add a retry-count or DLQ column so a poison row gets quarantined instead of retried indefinitely.

## Kafka bootstrap servers hardcoded to `localhost:9092`

**Where:** `post-service` and `notification-service`, both `KafkaConfig` classes

Every other cross-service host in this repo is parameterized via env var (`SERVICE_DISCOVERY_HOST`, `REDIS_HOST`, `POSTGRES_HOST`, ...), set in `docker-compose.yaml`. Kafka's bootstrap servers are the one exception — hardcoded to `localhost:9092` in both services' `KafkaConfig`, with no override anywhere in `application.yaml`/`.properties`. Inside a container, `localhost` isn't the `kafka` service, so this looks like it would fail to connect when actually run via `docker compose` rather than local dev against a bare Kafka broker.

Fix: parameterize via a `KAFKA_BOOTSTRAP_SERVERS` (or similar) env var, same pattern as every other service host.

## No Kafka producer partition key

**Where:** `post-service`, `OutboxEventPoller.poll()` — `kafkaTemplate.send(event.getTopic(), like)`

Sends without a key. `likes-topic` is single-partition today (`notification-service` `KafkaConfig.t1()`), so ordering happens to hold by accident, not by design. Nothing enforces it — round-robin partitioning kicks in the moment partition count increases for throughput, and per-post like ordering silently breaks.

Fix: key by post ID so all events for a given post land on the same partition.
