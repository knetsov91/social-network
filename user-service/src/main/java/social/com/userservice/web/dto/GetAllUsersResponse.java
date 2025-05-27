package social.com.userservice.web.dto;

import java.util.UUID;

public record GetAllUsersResponse(UUID userId, String username) {
}
