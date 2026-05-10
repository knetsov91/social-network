package social.com.userservice.web.dto;

import java.util.UUID;

public record UserResponse(UUID userId, String username) {
}
