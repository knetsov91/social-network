# Functional Requirements for Chat Service

## 1. Create Chat

- **Endpoint**: `POST /api/v1/chats/create`
- **Description**: Create a chat between a set of participants.
- **Inputs**: `createdBy`, `participants` (list of user IDs).
- **Business Rules**:
  - Rejects creation if a chat already exists with exactly this set of participants (matched by `$all` + `$size`, so order doesn't matter but the set must match exactly).
  - Unlike **Follow a User** in user-service, this is *not* idempotent — calling it twice for the same participants returns an error on the second call rather than silently succeeding.
- **Outputs**: `201 Created` on success; `400 Bad Request` ("Chat between these participants already exists") on duplicate.

## 2. Get Chat by ID

- **Endpoint**: `GET /api/v1/chats/{chatId}`
- **Outputs**: `200 OK` with the chat's metadata; `400 Bad Request` if the chat doesn't exist.

## 3. Get a User's Chats

- **Endpoint**: `GET /api/v1/chats/users/{userId}`
- **Description**: List every chat the given user participates in, with each participant's username resolved via a call to user-service.
- **Outputs**: `200 OK` with a list of chats and their resolved participants.
- **Open question**: the service throws a "user not found" error when `findByParticipantsContains` returns an empty `Optional`. Whether Spring Data MongoDB actually returns an empty `Optional` for a user with zero chats (as opposed to `Optional` wrapping an empty list) hasn't been verified against a real MongoDB instance — there's no integration test for this service. If it does, a brand-new user with no chats yet would incorrectly get an error instead of an empty list.

## 4. Get Chat Messages

- **Endpoint**: `GET /api/v1/chats/{chatId}/messages`
- **Outputs**: `200 OK` with every message in the chat, in insertion order.
- **Acceptance Criteria**: no pagination — all messages are returned in a single response regardless of chat history length.

## 5. Send Message

- **Endpoint**: `POST /api/v1/messages`
- **Description**: Persist a chat message and push it to anyone currently viewing that chat.
- **Inputs**: `chatId`, `senderId`, `receiverId`, `text`.
- **Business Rules**:
  - Persists the message and appends it to the chat's message list.
  - Broadcasts it over STOMP to `/topic/chat/{chatId}`.
- **Outputs**: `201 Created` with the saved message.
- **Known gap**: like post-service's create/like endpoints, `senderId` is taken from the request body, not the authenticated principal — nothing stops a caller from sending a message as another user.
