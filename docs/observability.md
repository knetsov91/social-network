# Observability

## Metrics

Prometheus scrapes each service's `/actuator/prometheus` endpoint every 5 seconds. Grafana at `:3000` visualises the collected metrics.

Start the observability stack:

```bash
cd infrastructure/observability
docker compose up -d   # Prometheus :9090, Grafana :3000, Jaeger :16686
```

## Distributed Tracing

All services export traces via OpenTelemetry Protocol (OTLP) to Jaeger. Every request that enters the API Gateway generates a trace that spans all services it touches.

**Instrumentation stack:**
- `micrometer-tracing-bridge-otel` — bridges Spring's Micrometer Observation API to OpenTelemetry
- `opentelemetry-exporter-otlp` — sends spans over HTTP to Jaeger's OTLP collector (`:4318`)
- Sampling rate: `1.0` (100% of requests are traced)

**Instrumented services:** `gateway-service`, `auth-service`, `user-service`
