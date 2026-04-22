# Chat microservice

## Overview

This microservice is responsible for real-time messaging between users. WebSocket (STOMP) is used for real-time communication, MongoDB for message persistence, and Kafka for publishing chat events to the notification-service.

## Functional requirements

- User can create a chat with another user.
- User can send messages in a chat.
- User can view their chats.
- User can view messages in a chat in real time.
