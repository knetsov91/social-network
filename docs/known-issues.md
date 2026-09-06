# Known issues / future work

Gaps identified but not yet fixed. Each is a correctness or availability issue once a service runs more than one replica — none of them show up in local dev, where every service is a single instance.

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
