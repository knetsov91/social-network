#!/usr/bin/env bash
set -euo pipefail

INFRA_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="$(cd "$INFRA_DIR/.." && pwd)"
ENV_FILE="$PROJECT_ROOT/.env"

if [[ ! -f "$ENV_FILE" ]]; then
    echo "ERROR: $ENV_FILE not found"
    exit 1
fi

set -a
# shellcheck source=/dev/null
source "$ENV_FILE"
set +a

required_vars=(VAULT_TOKEN POSTGRES_USER POSTGRES_PASSWORD SENTRY_DSN JWT_SECRET_KEY JWT_EXP_TIME)
for var in "${required_vars[@]}"; do
    if [[ -z "${!var:-}" ]]; then
        echo "ERROR: $var is not set in $ENV_FILE"
        exit 1
    fi
done

echo "Seeding secret/post-service"
docker compose -f "$INFRA_DIR/docker-compose.yaml" exec -T -e VAULT_TOKEN="$VAULT_TOKEN" vault \
    vault kv put secret/post-service \
    spring.datasource.username="$POSTGRES_USER" \
    spring.datasource.password="$POSTGRES_PASSWORD" \
    sentry.dsn="$SENTRY_DSN"

echo "Seeding secret/auth-service"
docker compose -f "$INFRA_DIR/docker-compose.yaml" exec -T -e VAULT_TOKEN="$VAULT_TOKEN" vault \
    vault kv put secret/auth-service \
    jwt.secret-key="$JWT_SECRET_KEY" \
    jwt.expiration-time="$JWT_EXP_TIME" \
    sentry.dsn="$SENTRY_DSN"

echo "Done."
