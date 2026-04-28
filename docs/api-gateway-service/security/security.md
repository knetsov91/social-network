# API Gateway security

## Overview

All requests entering the gateway pass through **`AuthFilter`** before being forwarded to any downstream service. The filter intercepts every request and delegates token validation to **auth-service**, which checks the token's signature, expiry, and blacklist.
