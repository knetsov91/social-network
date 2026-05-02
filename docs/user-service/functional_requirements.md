# Functional Requirements for User Service

## 1. User Registration
- **Description**: Allow new users to create an account.
- **Inputs**: `username` (alphanumeric, ≥ 2 chars), `email` (valid format), `password` (≥ 8 chars), `confirmPassword`.
- **Business Rules**:
  - Username must be unique.
  - `password` must match `confirmPassword`.
  - Password is encoded before storage.
- **Outputs**: HTTP 200 with user summary (excluding password) and JWT cookie on success; HTTP 400 with validation errors on failure.
- **Acceptance Criteria**:
  - Successful registration returns user object (no password) and sets JWT cookie.
  - Validation errors return 400 with clear messages.
  - Password is never stored in plain text.
  - Registration evicts `users` cache.
  
  

