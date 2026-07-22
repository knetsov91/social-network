# ADR 004 — HashiCorp Vault for Secrets Management

## Status

Accepted

## Context

**post-service** and **auth-service** need DB credentials, a JWT signing secret, and a Sentry DSN at startup. Previously these were plain environment variables (**POSTGRES_PASSWORD**, **JWT_SECRET_KEY**, **SENTRY_DSN**) resolved directly in **application.yaml**, visible in process environment and **.env** files with no access control or audit trail.

## Decision

Both services pull their secrets from HashiCorp Vault at startup instead of reading them from env vars. Each service's config only references its own KV v2 path, scoped by service name, and fails to start rather than run with a missing secret.

For local development, Vault runs in dev mode (**infrastructure/docker-compose.yaml**): in-memory storage, auto-unsealed, authenticated with a single root token (**VAULT_TOKEN**). **infrastructure/vault/seed.sh** writes the actual secret values into Vault from **.env**, since dev mode starts empty on every container restart.

## Consequences

**Benefits:**
- Secrets no longer sit in **application.yaml** as plain env-var placeholders.
- Startup fails fast on a missing or misconfigured secret instead of running with an unresolved placeholder.

**Drawbacks — not production-ready:**
- **Single root token, no isolation.** Every service authenticates with the same root token, which can read any path. Production needs per-service AppRole (or Kubernetes auth on K8s) with a policy restricting each one to its own path.
- **Dev-mode storage.** In-memory, no persistence, auto-unsealed on start — a restart wipes all secrets, which is why they have to be reseeded. Production needs a real storage backend, e.g. Vault's Raft integrated storage (a multi-node cluster that replicates data via the Raft consensus protocol, so no single node is a point of failure), with auto-unseal via a cloud KMS/HSM.
- **Static KV secrets.** DB credentials are one fixed username/password written once, never rotated. Vault's database secrets engine can instead issue short-lived credentials per lease.
- **Plaintext HTTP** — dev mode has no TLS.
- **Manual seeding.** **seed.sh** is a hand-run local script; production provisioning belongs in Terraform or a CI/CD pipeline step.

Moving any service's Vault usage to a shared, staging, or production environment requires revisiting every point above — that work should land as a separate ADR superseding this one.
