package social.com.chatservice.user.client.dto;

import java.util.UUID;

public record UserResponse(UUID userId, String username) {
}
