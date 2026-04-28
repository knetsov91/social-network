# Routes

| Path pattern | Service | Notes |
|---|---|---|
| `/api/*/posts/**` | post-service | |
| `/api/*/users/**` | user-service | |
| `/api/*/tokens/**` | auth-service | |
| `/api/*/notifications` | notification-service | |
| `/ws-endpoint/**` | notification-service | WebSocket upgrade |

Path patterns use two wildcards:
- `*` — matches a **single path segment** (e.g. `v1`)
- `**` — matches **any number of segments** including nested paths (e.g. `/posts/123/likes`)

The `/ws-endpoint/**` route uses **`lb:ws://`** instead of `lb://` — this tells the gateway to forward the request as a **WebSocket connection** rather than a standard HTTP request. The client performs a **WebSocket handshake** (HTTP Upgrade) and the gateway proxies the **persistent connection** through to notification-service.
